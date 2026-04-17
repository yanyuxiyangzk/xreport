# -*- coding: utf-8 -*-
"""清洗抓取的文档：去掉导航侧边栏、残留HTML标签、冗余空行"""
import os, re

BASE_DIR = r"D:\project\aicoding\XReport\docs\springreport_docs"

def clean_file(filepath):
    with open(filepath, "r", encoding="utf-8") as f:
        content = f.read()

    original = content

    # 去掉顶部导航菜单行（如 [首页](/)[功能概览](/%E5%8A%9F%E8%83%BD...）
    content = re.sub(r'\[(首页|功能概览|更新记录|在线体验|联系我们|黑名单|源码下载|友商推荐|技术支持和收费说明|项目介绍|常见问题|SpringReport本地运行环境搭建文档|其他系统免登录跳转到SpringReport|代码生成器用法简介|国际化版本说明|部署|swagger集成|配置|第三方框架集成方案|功能模块|报表设计|租户管理|系统管理|报表|参数组件|EXCEL报表设计|EXCEL表格|EXCEL单元格属性|EXCEL报表属性|EXCEL图表|EXCEL公式函数)\]\([^)]+\)', '', content)

    # 去掉 [xxx](/path) 格式的残留链接（导航中的）
    content = re.sub(r'\[([^\]]{1,30})\]\(/\d+[^)]*\.html\)', r'\1', content)

    # 去掉搜索框和UI元素残留
    content = re.sub(r'\[Skip to content\].*', '', content)
    content = re.sub(r'\[Return to top\].*', '', content)
    content = re.sub(r'\[Main Navigation\].*', '', content)
    content = re.sub(r'\[Search\].*', '', content)
    content = re.sub(r'Search.*?Main Navigation', '', content)
    content = re.sub(r'\[!\[img\]\([^)]+\)\]\([^)]+\)', '', content)
    content = re.sub(r'!\[img\]\([^)]+\)', '', content)
    content = re.sub(r'\[![^\]]*\]\([^)]*\)', '', content)

    # 去掉 Logo 行
    content = re.sub(r'\[!\[\]\([^)]+\)SpringReport\]', 'SpringReport', content)

    # 去掉底部导航残留
    content = re.sub(r'\[\d+\.\d+ [^]]+\]\([^)]+\)', '', content)

    # 去掉 "## 目录" / "On this page" 等残留
    content = re.sub(r'(## On this page|## 目录|# 目录|# On this page)', '', content)

    # 清理多余空行（超过3个空行变成2个）
    content = re.sub(r'\n{4,}', '\n\n', content)

    # 去掉行首行尾空白
    lines = [line.rstrip() for line in content.split('\n')]
    content = '\n'.join(lines).strip()

    # 去掉纯导航字符的空行
    lines2 = content.split('\n')
    cleaned_lines = []
    for line in lines2:
        stripped = line.strip()
        # 跳过只有1-3个字符的行（很可能是残留）
        if len(stripped) <= 2 and len(stripped) > 0:
            continue
        cleaned_lines.append(line)
    content = '\n'.join(cleaned_lines)

    if content != original:
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)
        return True
    return False

total = 0
changed = 0
for root, dirs, files in os.walk(BASE_DIR):
    for filename in files:
        if filename.endswith(".md"):
            filepath = os.path.join(root, filename)
            total += 1
            if clean_file(filepath):
                changed += 1

print(f"Done: {changed}/{total} files cleaned")
