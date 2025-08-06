@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

set OUTPUT=index.html

:: ヘッダ書き込み
(
echo ^<!DOCTYPE html^>
echo ^<html xmlns:th="http://www.thymeleaf.org"^>
echo ^<head^>
echo     ^<meta charset="UTF-8"^>
echo     ^<title th:text="${title}"^>^</title^>
echo     ^<meta name="viewport" content="width=device-width,initial-scale=1.0"^>
echo     ^<link rel="stylesheet" href="./css/style.css"^>
echo ^</head^>
echo ^<body^>
) > %OUTPUT%

:: 各ファイルから抽出＆追記
for %%F in (*.html) do (
    if /I not "%%F"=="%OUTPUT%" (
        echo 処理中: %%F
        powershell -Command ^
            "$content = Get-Content -Raw '%%F';" ^
            "[regex]::Matches($content, '<div class=[\"'']page[\"''][^>]*?>.*?</div>', 'Singleline') | ForEach-Object { $_.Value } | Out-File -Encoding utf8 -Append '%OUTPUT%'"
    )
)

:: フッター書き込み
(
echo     ^<script type="module" src="./js/main.js"^>^</script^>
echo ^</body^>
echo ^</html^>
) >> %OUTPUT%

echo 統合完了: %OUTPUT%
pause
