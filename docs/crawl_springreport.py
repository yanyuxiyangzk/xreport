# -*- coding: utf-8 -*-
"""批量抓取 SpringReport 文档"""
import os, sys, re, subprocess, time

sys.stdout.reconfigure(encoding='utf-8')

BASE_DIR = r"D:\project\aicoding\XReport\docs\springreport_docs"
os.makedirs(BASE_DIR, exist_ok=True)

URLS = [
    ("1.项目介绍.md", "https://www.springreport.vip/2.%E9%A1%B9%E7%9B%AE%E4%BB%8B%E7%BB%8D.html"),
    ("2.常见问题.md", "https://www.springreport.vip/3.%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98.html"),
    ("3.本地运行环境搭建文档.md", "https://www.springreport.vip/4.SpringReport%E6%9C%AC%E5%9C%B0%E8%BF%90%E8%A1%8C%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E6%96%87%E6%A1%A3.html"),
    ("4.其他系统免登录跳转.md", "https://www.springreport.vip/5.%E5%85%B6%E4%BB%96%E7%B3%BB%E7%BB%9F%E5%85%8D%E7%99%BB%E5%BD%95%E8%B7%B3%E8%BD%AC%E5%88%B0SpringReport.html"),
    ("5.代码生成器用法简介.md", "https://www.springreport.vip/6.%E4%BB%A3%E7%A0%81%E7%94%9F%E6%88%90%E5%99%A8%E7%94%A8%E6%B3%95%E7%AE%80%E4%BB%8B.html"),
    ("6.国际化文本说明.md", "https://www.springreport.vip/7.%E5%9B%BD%E9%99%85%E5%8C%96%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E.html"),
    ("部署/1.windows一键部署.md", "https://www.springreport.vip/%E9%83%A8%E7%BD%B2/1.windows%E4%B8%80%E9%94%AE%E9%83%A8%E7%BD%B2.html"),
    ("部署/2.linux-docker部署.md", "https://www.springreport.vip/%E9%83%A8%E7%BD%B2/2.linux-docker%E9%83%A8%E7%BD%B2.html"),
    ("部署/3.linux-centos7部署.md", "https://www.springreport.vip/%E9%83%A8%E7%BD%B2/3.linux(centos7)%E9%83%A8%E7%BD%B2.html"),
    ("部署/4.SpringReport宝塔部署.md", "https://www.springreport.vip/%E9%83%A8%E7%BD%B2/4.SpringReport%E5%AE%9D%E5%A1%94%E9%83%A8%E7%BD%B2.html"),
    ("部署/5.windows单节点启动服务.md", "https://www.springreport.vip/%E9%83%A8%E7%BD%B2/5.windows%E9%83%A8%E7%BD%B2-%E5%8D%95%E7%8B%AC%E5%90%AF%E5%8A%A8%E6%9C%8D%E5%8A%A1.html"),
    ("部署/6.nginx配置_vue2.md", "https://www.springreport.vip/%E9%83%A8%E7%BD%B2/6.nginx%E9%85%8D%E7%BD%AE_vue2.html"),
    ("部署/7.nginx配置_vue3.md", "https://www.springreport.vip/%E9%83%A8%E7%BD%B2/7.nginx%E9%85%8D%E7%BD%AE_vue3.html"),
    ("部署/8.部部分软件免费下载地址.md", "https://www.springreport.vip/%E9%83%A8%E7%BD%B2/8.%E9%83%A8%E5%88%86%E8%BD%AF%E4%BB%B6%E4%B8%8B%E8%BD%BD%E5%9C%B0%E5%9D%80%E5%88%86%E4%BA%AB.html"),
    ("swagger/swagger集成.md", "https://www.springreport.vip/swagger%E9%9B%86%E6%88%90/swagger%E9%9B%86%E6%88%90.html"),
    ("配置/1.多租户模式开关.md", "https://www.springreport.vip/%E9%85%8D%E7%BD%AE/1.%E5%A4%9A%E7%A7%9F%E6%88%B7%E6%A8%A1%E5%BC%8F%E5%BC%80%E5%90%AF%E5%85%B3%E9%97%AD.html"),
    ("配置/2.关闭rocketmq.md", "https://www.springreport.vip/%E9%85%8D%E7%BD%AE/2.%E5%A6%82%E4%BD%95%E9%85%8D%E7%BD%AE%E5%85%B3%E9%97%ADrocketmq.html"),
    ("配置/3.根据实际情况修改的配置.md", "https://www.springreport.vip/%E9%85%8D%E7%BD%AE/3.%E9%9C%80%E8%A6%81%E6%A0%B9%E6%8D%AE%E5%AE%9E%E9%99%85%E9%83%A8%E7%BD%B2%E4%BF%AE%E6%94%B9%E7%9A%84%E9%85%8D%E7%BD%AE.html"),
    ("集成/1.SpringReport-vue2与若依单点集成.md", "https://www.springreport.vip/%E7%AC%AC%E4%B8%89%E6%96%B9%E6%A1%86%E6%9E%B6%E9%9B%86%E6%88%90/1.SpringReport(vue2)%E4%B8%8E%E6%A9%99%E5%8D%95%E9%9B%86%E6%88%90.html"),
    ("集成/2.SpringReport-vue3与若依单点集成.md", "https://www.springreport.vip/%E7%AC%AC%E4%B8%89%E6%96%B9%E6%A1%86%E6%9E%B6%E9%9B%86%E6%88%90/2.SpringReport(vue3)%E4%B8%8E%E6%A9%99%E5%8D%95%E9%9B%86%E6%88%90.html"),
    ("集成/3.SpringReport-vue2与若依依赖集成.md", "https://www.springreport.vip/%E7%AC%AC%E4%B8%89%E6%96%B9%E6%A1%86%E6%9E%B6%E9%9B%86%E6%88%90/3.SpringReport(vue2)%E4%B8%8E%E8%8B%A5%E4%BE%9D%E9%9B%86%E6%88%90.html"),
    ("集成/4.SpringReport-vue3与若依依赖集成.md", "https://www.springreport.vip/%E7%AC%AC%E4%B8%89%E6%96%B9%E6%A1%86%E6%9E%B6%E9%9B%86%E6%88%90/4.SpringReport(vue3)%E4%B8%8E%E8%8B%A5%E4%BE%9D%E9%9B%86%E6%88%90.html"),
    ("功能模块/租户管理/1.权限模板.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E7%A7%9F%E6%88%B7%E7%AE%A1%E7%90%86/1.%E6%9D%83%E9%99%90%E6%A8%A1%E6%9D%BF.html"),
    ("功能模块/租户管理/2.租户管理.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E7%A7%9F%E6%88%B7%E7%AE%A1%E7%90%86/2.%E7%A7%9F%E6%88%B7%E7%AE%A1%E7%90%86.html"),
    ("功能模块/系统管理/1.角色管理.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E7%B3%BB%E7%BB%9F%E7%AE%A1%E7%90%86/1.%E8%A7%92%E8%89%B2%E7%AE%A1%E7%90%86.html"),
    ("功能模块/系统管理/2.岗位管理.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E7%B3%BB%E7%BB%9F%E7%AE%A1%E7%90%86/2.%E5%B2%97%E4%BD%8D%E7%AE%A1%E7%90%86.html"),
    ("功能模块/系统管理/3.用户管理.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E7%B3%BB%E7%BB%9F%E7%AE%A1%E7%90%86/3.%E7%94%A8%E6%88%B7%E7%AE%A1%E7%90%86.html"),
    ("功能模块/系统管理/4.菜单和菜单功能管理.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E7%B3%BB%E7%BB%9F%E7%AE%A1%E7%90%86/4.%E8%8F%9C%E5%8D%95%E5%92%8C%E8%8F%9C%E5%8D%95%E5%8A%9F%E8%83%BD%E7%AE%A1%E7%90%86.html"),
    ("功能模块/系统管理/5.部门管理.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E7%B3%BB%E7%BB%9F%E7%AE%A1%E7%90%86/5.%E9%83%A8%E9%97%A8%E7%AE%A1%E7%90%86.html"),
    ("功能模块/报表/1.数据源管理.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E6%8A%A5%E8%A1%A8/1.%E6%95%B0%E6%8D%AE%E6%BA%90%E7%AE%A1%E7%90%86.html"),
    ("功能模块/报表/2.Excel报表.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E6%8A%A5%E8%A1%A8/2.Excel%E6%8A%A5%E8%A1%A8.html"),
    ("功能模块/报表/3.Excel报表查看.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E6%8A%A5%E8%A1%A8/3.Excel%E6%8A%A5%E8%A1%A8%E6%9F%A5%E7%9C%8B.html"),
    ("功能模块/报表/4.Excel报表预览查看.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E6%8A%A5%E8%A1%A8/4.Excel%E6%8A%A5%E8%A1%A8%E9%A2%84%E8%A7%88%E6%9F%A5%E7%9C%8B.html"),
    ("功能模块/报表/5.协同文档管理.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E6%8A%A5%E8%A1%A8/5.%E5%8D%8F%E5%90%8C%E6%96%87%E6%A1%A3%E7%AE%A1%E7%90%86.html"),
    ("功能模块/报表/6.协同文档.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E6%8A%A5%E8%A1%A8/6.%E5%8D%8F%E5%90%8C%E6%96%87%E6%A1%A3.html"),
    ("功能模块/报表/7.word模板管理.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E6%8A%A5%E8%A1%A8/7.word%E6%A8%A1%E6%9D%BF%E7%AE%A1%E7%90%86.html"),
    ("功能模块/报表/8.PPT模板管理.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E6%8A%A5%E8%A1%A8/8.PPT%E6%A8%A1%E6%9D%BF%E7%AE%A1%E7%90%86.html"),
    ("功能模块/报表/9.大屏模板管理.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E6%8A%A5%E8%A1%A8/9.%E5%A4%A7%E5%B1%8F%E6%A8%A1%E6%9D%BF%E7%AE%A1%E7%90%86.html"),
    ("功能模块/报表/10.大屏预览与大屏查看的区别.md", "https://www.springreport.vip/%E5%8A%9F%E8%83%BD%E6%A8%A1%E5%9D%97/%E6%8A%A5%E8%A1%A8/10.%E5%A4%A7%E5%B1%8F%E9%A2%84%E8%A7%88%E4%B8%8E%E5%A4%A7%E5%B1%8F%E6%9F%A5%E7%9C%8B%E7%9A%84%E5%8C%BA%E5%88%AB.html"),
    ("报表设计/参数组件/1.参数组件简介.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6/1.%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6%E7%AE%80%E4%BB%8B.html"),
    ("报表设计/参数组件/2.字符串输入框.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6/2.%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6-%E5%AD%97%E7%AC%A6%E4%B8%B2%E8%BE%93%E5%85%A5%E6%A1%86.html"),
    ("报表设计/参数组件/3.数字输入框.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6/3.%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6-%E6%95%B0%E5%AD%97%E8%BE%93%E5%85%A5%E6%A1%86.html"),
    ("报表设计/参数组件/4.下拉单选.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6/4.%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6-%E4%B8%8B%E6%8B%89%E5%8D%95%E9%80%89.html"),
    ("报表设计/参数组件/5.下拉多选.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6/5.%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6-%E4%B8%8B%E6%8B%89%E5%A4%9A%E9%80%89.html"),
    ("报表设计/参数组件/6.下拉树单选.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6/6.%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6-%E4%B8%8B%E6%8B%89%E6%A0%91(%E5%8D%95%E9%80%89).html"),
    ("报表设计/参数组件/7.下拉树多选.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6/7.%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6-%E4%B8%8B%E6%8B%89%E6%A0%91%E5%A4%9A%E9%80%89.html"),
    ("报表设计/参数组件/8.日期.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6/8.%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6-%E6%97%A5%E6%9C%9F.html"),
    ("报表设计/参数组件/9.日期范围.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6/9.%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6-%E6%97%A5%E6%9C%9F%E8%8C%83%E5%9B%B4%E7%BB%84%E4%BB%B6.html"),
    ("报表设计/参数组件/10.级联下拉.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6/10.%E5%8F%82%E6%95%B0%E7%BB%84%E4%BB%B6-%E7%BA%A7%E8%81%94%E4%B8%8B%E6%8B%89.html"),
    ("报表设计/Excel报表设计/1.报表设计器简介.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/Excel%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/1.%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1%E7%AE%80%E4%BB%8B.html"),
    ("报表设计/Excel报表设计/2.数据源管理-sql语句.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/Excel%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/2.%E6%95%B0%E6%8D%AE%E9%9B%86%E7%AE%A1%E7%90%86-sql%E8%AF%AD%E5%8F%A5.html"),
    ("报表设计/Excel报表设计/3.数据源管理-参数配置.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/Excel%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/3.%E6%95%B0%E6%8D%AE%E9%9B%86%E7%AE%A1%E7%90%86-%E5%8F%82%E6%95%B0%E9%85%8D%E7%BD%AE.html"),
    ("报表设计/Excel报表设计/4.数据源管理-系统参数.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/Excel%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/4.%E6%95%B0%E6%8D%AE%E9%9B%86%E7%AE%A1%E7%90%86-%E7%B3%BB%E7%BB%9F%E5%8F%82%E6%95%B0.html"),
    ("报表设计/Excel报表设计/5.数据源管理-主子表参数.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/Excel%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/5.%E6%95%B0%E6%8D%AE%E9%9B%86%E7%AE%A1%E7%90%86-%E4%B8%BB%E5%AD%90%E8%A1%A8%E5%8F%82%E6%95%B0.html"),
    ("报表设计/Excel报表设计/7.动态参数sql.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/Excel%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/7.%E5%8A%A8%E6%80%81%E5%8F%82%E6%95%B0sql.html"),
    ("报表设计/Excel报表设计/8.动态参数存储过程.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/Excel%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/8.%E5%8A%A8%E6%80%81%E5%8F%82%E6%95%B0%E5%AD%98%E5%82%A8%E8%BF%87%E7%A8%8B.html"),
    ("报表设计/Excel报表设计/9.sql分页查询配置.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/Excel%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/9.sql%E5%88%86%E9%A1%B5%E6%9F%A5%E8%AF%A2%E9%85%8D%E7%BD%AE.html"),
    ("报表设计/Excel报表设计/10.api分页查询配置.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/Excel%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/10.api%E5%88%86%E9%A1%B5%E6%9F%A5%E8%AF%A2%E9%85%8D%E7%BD%AE.html"),
    ("报表设计/Excel报表设计/11.参数传递.md", "https://www.springreport.vip/%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/Excel%E6%8A%A5%E8%A1%A8%E8%AE%BE%E8%AE%A1/11.%E5%8F%82%E6%95%B0%E4%BC%A0%E9%80%92.html"),
]

print(f"Total: {len(URLS)} pages to crawl")
print("=" * 60)

success, failed = 0, []

for filename, url in URLS:
    filepath = os.path.join(BASE_DIR, filename)
    dirpath = os.path.dirname(filepath)
    os.makedirs(dirpath, exist_ok=True)

    code = f'''
import urllib.request, re, sys
url = "{url}"
try:
    req = urllib.request.Request(url, headers={{"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"}})
    with urllib.request.urlopen(req, timeout=20) as r:
        html = r.read().decode("utf-8", errors="replace")
    # Remove nav/toc
    html = re.sub(r'<div class="on-this-page[^"]*".*?</div>', '', html, flags=re.DOTALL)
    html = re.sub(r'<div class="table-of-contents[^"]*".*?</div>', '', html, flags=re.DOTALL)
    # Remove script/style
    html = re.sub(r'<script[^>]*>.*?</script>', '', html, flags=re.DOTALL)
    html = re.sub(r'<style[^>]*>.*?</style>', '', html, flags=re.DOTALL)
    # Convert to markdown
    md = html
    md = re.sub(r'<!--.*?-->', '', md, flags=re.DOTALL)
    md = re.sub(r'<h1[^>]*>(.*?)</h1>', r'\\n# \\1\\n', md, flags=re.DOTALL)
    md = re.sub(r'<h2[^>]*>(.*?)</h2>', r'\\n## \\1\\n', md, flags=re.DOTALL)
    md = re.sub(r'<h3[^>]*>(.*?)</h3>', r'\\n### \\1\\n', md, flags=re.DOTALL)
    md = re.sub(r'<h4[^>]*>(.*?)</h4>', r'\\n#### \\1\\n', md, flags=re.DOTALL)
    md = re.sub(r'<a[^>]*href="([^"]*)"[^>]*>(.*?)</a>', r'[\\2](\\1)', md, flags=re.DOTALL)
    md = re.sub(r'<img[^>]*src="([^"]*)"[^>]*>', r'![img](\\1)', md)
    md = re.sub(r'<li[^>]*>(.*?)</li>', r'\\n- \\1', md, flags=re.DOTALL)
    md = re.sub(r'<[^>]+>', '', md)
    md = re.sub(r'\\n\\n+', '\\n\\n', md).strip()
    with open(r"{filepath}", "w", encoding="utf-8") as f:
        f.write(md)
    print("OK")
except Exception as e:
    print("FAIL:" + str(e))
'''
    result = subprocess.run(["python", "-c", code],
        capture_output=True, text=True,
        encoding="utf-8", errors="replace")
    out = (result.stdout + result.stderr).strip()
    if "OK" in out:
        success += 1
        print(f"  OK {filename}")
    else:
        failed.append((filename, url))
        print(f"  FAIL {filename}")
    time.sleep(0.3)

print("=" * 60)
print(f"Done: {success}/{len(URLS)} OK, {len(failed)} failed")
if failed:
    for fn, u in failed:
        print(f"  - {fn}: {u}")
