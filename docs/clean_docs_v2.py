# -*- coding: utf-8 -*-
"""更彻底地清洗文档"""
import os, re

BASE_DIR = r"D:\project\aicoding\XReport\docs\springreport_docs"

def deep_clean(filepath):
    with open(filepath, "r", encoding="utf-8") as f:
        content = f.read()

    original = content

    # 1. 去掉所有 [text](/path.html) 格式的站内链接
    content = re.sub(r'\[([^\]]+)\]\(/[^)]+\.html\)', r'\1', content)

    # 2. 去掉残留的 markdown 图片行
    content = re.sub(r'!\[[^\]]*\]\([^)]+\)', '', content)

    # 3. 去掉行首行尾空白
    lines = [l.rstrip() for l in content.split('\n')]
    content = '\n'.join(lines).strip()

    # 4. 去掉只有几个字符的孤立行（如残留符号）
    lines = content.split('\n')
    good_lines = []
    for line in lines:
        stripped = line.strip()
        # 跳过太短的孤立行（导航残留）
        if len(stripped) <= 1 and stripped and stripped not in ['#', '##', '###', '-']:
            continue
        # 跳过只含特殊字符的行
        if stripped and not any(c.isalnum() for c in stripped):
            continue
        good_lines.append(line)
    content = '\n'.join(good_lines)

    # 5. 合并多余空行
    content = re.sub(r'\n{4,}', '\n\n', content)

    # 6. 去掉 "On this page" 残留行
    content = re.sub(r'On this page\s*', '', content)
    content = re.sub(r'#\s*On this page\s*#?', '', content)

    # 7. 清理HTML实体
    content = content.replace('&nbsp;', ' ')
    content = content.replace('&lt;', '<')
    content = content.replace('&gt;', '>')
    content = content.replace('&amp;', '&')
    content = content.replace('&#', '')

    # 8. 去掉开头的多余空行
    content = content.lstrip('\n')

    if content != original:
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)
        return True
    return False

total = changed = 0
for root, dirs, files in os.walk(BASE_DIR):
    for filename in files:
        if filename.endswith(".md"):
            filepath = os.path.join(root, filename)
            total += 1
            if deep_clean(filepath):
                changed += 1

print(f"Deep clean done: {changed}/{total} changed")
