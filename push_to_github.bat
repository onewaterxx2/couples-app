@echo off
echo ====================================
echo 上传到GitHub
echo ====================================
echo.
echo 请确保已在GitHub上创建了仓库！
echo.
echo 仓库名称建议: couples-app
echo GitHub URL: https://github.com/onewaterxx2/couples-app
echo.
pause

echo.
echo 正在连接到GitHub仓库...
git remote add origin https://github.com/onewaterxx2/couples-app.git

echo.
echo 正在推送代码到GitHub...
git push -u origin main

echo.
echo ====================================
echo 上传完成！
echo ====================================
echo.
echo 访问你的GitHub仓库:
echo https://github.com/onewaterxx2/couples-app
echo.
pause
