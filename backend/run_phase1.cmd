@echo off
chcp 65001 >nul
cd /d D:\project\aicoding\XReport\backend
claude --print --dangerously-skip-permissions < PHASE_1_input.txt
