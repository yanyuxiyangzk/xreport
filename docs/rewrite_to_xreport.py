# -*- coding: utf-8 -*-
"""
将 SpringReport 文档改写为 XReport 文档
XReport 技术栈：React 18 + TypeScript + Vite（前端），Spring Boot 3.2.6 + JDK 17（后端）
核心模块：报表设计器、数据源管理、报表执行（Excel/PDF/CSV导出）
"""
import os, re, shutil, sys
sys.stdout.reconfigure(encoding='utf-8')

SPRING_DIR = r"D:\project\aicoding\XReport\docs\springreport_docs"
OUT_DIR = r"D:\project\aicoding\XReport\docs\xreport_docs"

# ---- 替换规则 ----
REPLACEMENTS = [
    # 品牌替换
    ("SpringReport", "XReport"),
    ("springreport.vip", "xreport.vip"),
    ("springreport", "xreport"),
    ("Spring Report", "XReport"),
    # SpringReport 特有功能（XReport 不包含的，跳过相关章节）
    # 租户管理 - XReport 无此模块
    ("租户管理", "（本功能XReport暂不包含）"),
    # 权限模板
    ("权限模板", "（本功能XReport暂不包含）"),
    # Word 模板 - XReport 当前不含 Word 设计器
    ("Word设计", "（XReport Word模板设计规划中）"),
    # PPT - XReport 当前不含
    ("PPT设计", "（XReport PPT设计规划中）"),
    # 大屏 - XReport 当前不含
    ("大屏模板", "（XReport 大屏设计规划中）"),
    ("大屏设计", "（XReport 大屏设计规划中）"),
    ("大屏组件", "（XReport 大屏设计规划中）"),
    # 协同文档 - XReport 当前不含
    ("协同文档", "（XReport 协同文档规划中）"),
    # 技术栈替换
    ("vue2+elementui/vue3+elementPlus", "React 18 + TypeScript + Vite"),
    ("vue3+elementPlus", "React 18 + TypeScript + Vite"),
    ("Vue3", "React 18"),
    ("vue3", "React 18"),
    ("Vue2", "React 18"),
    ("vue2", "React 18"),
    ("Luckysheet", "Luckysheet（电子表格引擎）"),
    ("element-ui", "Ant Design 5"),
    ("elementPlus", "Ant Design 5"),
    # 第三方数据库支持（XReport 仅支持 MySQL/PostgreSQL）
    ("Oracle", "（XReport当前版本仅支持MySQL/PostgreSQL）"),
    ("SqlServer", "（XReport当前版本仅支持MySQL/PostgreSQL）"),
    ("达梦数据库", "（XReport当前版本仅支持MySQL/PostgreSQL）"),
    ("人大金仓", "（XReport当前版本仅支持MySQL/PostgreSQL）"),
    ("瀚高数据库", "（XReport当前版本仅支持MySQL/PostgreSQL）"),
    ("clickhouse", "（XReport当前版本暂不支持）"),
    ("influxdb", "（XReport当前版本暂不支持）"),
    ("TDengine", "（XReport当前版本暂不支持）"),
    ("elasticsearch", "（XReport当前版本暂不支持）"),
    ("doris", "（XReport当前版本暂不支持）"),
    ("宝塔部署", "（XReport推荐Docker部署）"),
    ("若依", "（XReport支持标准OAuth2/SAML SSO集成）"),
    ("橙单", "（XReport支持标准SSO）"),
    ("rocketmq", "（XReport当前版本暂不支持消息队列）"),
    # 部署相关
    ("apache2.0", "MIT License（开源友好）"),
]

# 需要完全跳过（排除）的内容
# 只按文件名关键词跳过（不按内容）
SKIP_FILENAMES = [
    "swagger",
    "数据库表结构",
    "报表示例",
    "第三方框架集成",
    "onlyoffice",
    "Word报表设计",
    "PPT报表设计",
    "大屏设计",
    "大屏组件",
    "多租户",
    "租户管理",
    "协同文档",
    "宝塔",
    "本地运行环境",
]

def should_skip(filepath):
    """按路径判断文档是否需要跳过"""
    path_str = filepath.replace(SPRING_DIR, "").lower()
    for kw in SKIP_FILENAMES:
        if kw.lower() in path_str:
            return True
    return False

def apply_replacements(content):
    """应用所有替换规则"""
    for old, new in REPLACEMENTS:
        content = content.replace(old, new)
    return content

def process_file(src_path, dst_path):
    """处理单个文件"""
    with open(src_path, "r", encoding="utf-8") as f:
        content = f.read()

    if should_skip(src_path):
        return "skip"

    # 应用替换
    content = apply_replacements(content)

    # 清理多余空行
    content = re.sub(r'\n{4,}', '\n\n', content)

    # 确保目录存在
    os.makedirs(os.path.dirname(dst_path), exist_ok=True)

    with open(dst_path, "w", encoding="utf-8") as f:
        f.write(content)

    return "ok"

def main():
    if os.path.exists(OUT_DIR):
        shutil.rmtree(OUT_DIR)

    total = ok = skip = 0

    for root, dirs, files in os.walk(SPRING_DIR):
        for filename in files:
            if not filename.endswith(".md"):
                continue

            src_path = os.path.join(root, filename)
            rel_path = os.path.relpath(src_path, SPRING_DIR)
            dst_path = os.path.join(OUT_DIR, rel_path)

            total += 1
            result = process_file(src_path, dst_path)
            if result == "ok":
                ok += 1
                print(f"  OK {rel_path}")
            elif result == "skip":
                skip += 1
                print(f"  SKIP {rel_path}")

    print(f"\nDone: {ok}/{total} converted, {skip} skipped")
    print(f"Output: {OUT_DIR}")

if __name__ == "__main__":
    main()
