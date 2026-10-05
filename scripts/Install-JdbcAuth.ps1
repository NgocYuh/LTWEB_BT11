$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$nativePath = Join-Path $projectRoot '.runtime\native'
New-Item -ItemType Directory -Force -Path $nativePath | Out-Null
$filename = 'mssql-jdbc_auth-13.2.1.x64.dll'
$target = Join-Path $nativePath $filename
$source = 'https://repo.maven.apache.org/maven2/com/microsoft/sqlserver/mssql-jdbc_auth/13.2.1.x64/' + $filename
Invoke-WebRequest -UseBasicParsing -Uri $source -OutFile $target
$signature = Get-AuthenticodeSignature -LiteralPath $target
if ($signature.Status -ne 'Valid' -or $signature.SignerCertificate.Subject -notmatch 'Microsoft Corporation') {
    throw 'JDBC authentication DLL did not pass Microsoft signature validation.'
}
Write-Host 'Microsoft JDBC Windows authentication DLL downloaded and signature verified.'
