Execute Phase 0 of XReport backend project.

Read the task file: D:\project\aicoding\XReport\docs\PHASE_0_task.md
Read the CLAUDE.md: D:\project\aicoding\XReport\CLAUDE.md

Execute the task described in PHASE_0_task.md. 

Key requirements:
1. Create Maven multi-module project at D:\project\aicoding\XReport\backend\
2. Modules: xreport-common, xreport-pojo, xreport-mapper, xreport-service, xreport-web
3. Spring Boot 3.2.6 + JDK 17
4. MyBatis-Plus 3.5.6 + Spring Security 6 + JWT
5. Keep sys_* table structure consistent with SpringReport (so SQL scripts are reusable)
6. Create backend/sql/xreport_init.sql with the table creation scripts
7. Create backend/README.md

Do NOT do frontend work (Phase 0 is backend only).

After completion, verify with: mvn clean compile -f D:\project\aicoding\XReport\backend\pom.xml

Report what files were created and whether the build succeeded.
