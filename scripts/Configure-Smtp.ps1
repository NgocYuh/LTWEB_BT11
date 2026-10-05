$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$runtimeRoot = Join-Path $projectRoot '.runtime'
New-Item -ItemType Directory -Force -Path $runtimeRoot | Out-Null
Write-Host 'HNHBOOKSTORE - Configure Gmail SMTP'
Write-Host 'Enter a Google App Password, NOT your normal Gmail password.'
$email = Read-Host 'Gmail sender (Enter = huyhoang.260806@gmail.com)'
if ([string]::IsNullOrWhiteSpace($email)) { $email = 'huyhoang.260806@gmail.com' }
$secret = Read-Host 'Google App Password (hidden)' -AsSecureString
$credential = [Management.Automation.PSCredential]::new($email, $secret)
$client = [Net.Mail.SmtpClient]::new('smtp.gmail.com', 587)
$client.EnableSsl = $true
$client.Timeout = 20000
$client.Credentials = $credential.GetNetworkCredential()
$message = [Net.Mail.MailMessage]::new($email, 'huyhoang.260806@gmail.com')
$message.Subject = 'HNHBOOKSTORE - SMTP connection test'
$message.Body = 'This is the SMTP test requested for HNHBOOKSTORE (24133023). No action is required. Registration OTP will be sent separately by the application.'
try {
    $client.Send($message)
    $credential | Export-Clixml -LiteralPath (Join-Path $runtimeRoot 'smtp.credential.xml')
    [IO.File]::WriteAllText((Join-Path $runtimeRoot 'smtp-test-ok.txt'), [DateTime]::Now.ToString('o'))
    Write-Host 'SUCCESS: Gmail SMTP accepted the test email. Check your inbox/spam folder.' -ForegroundColor Green
    Write-Host 'Credential saved with Windows DPAPI encryption for this Windows account only.'
} catch {
    Write-Host 'SMTP test failed. Check the App Password and Google account settings, then run this script again.' -ForegroundColor Red
    Write-Host ('Error type: ' + $_.Exception.GetType().Name)
} finally {
    $message.Dispose()
    $client.Dispose()
}
Read-Host 'Press Enter to close'
