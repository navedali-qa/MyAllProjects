taskkill /F /IM chrome.exe /T
taskkill /F /IM chromedriver.exe /T
taskkill /F /IM start Execution1.bat /T
taskkill /F /IM start Execution2.bat /T
taskkill /F /IM start Execution3.bat /T
taskkill /F /IM start Execution4.bat /T
taskkill /F /IM start Execution5.bat /T
timeout /t 2
start Execution1.bat
timeout /t 20
start Execution2.bat
timeout /t 20
start Execution3.bat
timeout /t 20
start Execution4.bat
timeout /t 20
start Execution5.bat