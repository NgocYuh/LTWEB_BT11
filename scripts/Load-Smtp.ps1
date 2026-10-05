$projectRoot = Split-Path -Parent $PSScriptRoot
$credentialPath = Join-Path $projectRoot '.runtime\smtp.credential.xml'
if (Test-Path -LiteralPath $credentialPath) {
    $smtpCredential = Import-Clixml -LiteralPath $credentialPath
    $env:SMTP_HOST = 'smtp.gmail.com'
    $env:SMTP_PORT = '587'
    $env:SMTP_USERNAME = $smtpCredential.UserName
    $env:SMTP_FROM = $smtpCredential.UserName
    $env:SMTP_PASSWORD = $smtpCredential.GetNetworkCredential().Password
    Remove-Variable smtpCredential
}
