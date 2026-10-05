# Copy to environment.local.ps1 (ignored), then fill in the confirmed connection.
# Dot-source in the SAME PowerShell that starts Tomcat:
# . .\config\environment.local.ps1
$env:DB_URL = 'jdbc:sqlserver://YOUR_SERVER:1433;databaseName=HNHBOOKSTORE;encrypt=true;trustServerCertificate=false;loginTimeout=5'
$env:DB_USERNAME = 'YOUR_SQL_LOGIN'
# Read DB_PASSWORD securely in your local terminal; never paste it into chat.
# $secret = Read-Host 'SQL Server password' -AsSecureString
# $env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $secret).Password
# Windows integrated authentication instead requires the matching JDBC auth DLL
# and integratedSecurity=true in DB_URL. Leave DB_USERNAME/DB_PASSWORD unset.
$env:CATALINA_HOME = 'D:\Apache\apache-tomcat-10.1.57\apache-tomcat-10.1.57'
