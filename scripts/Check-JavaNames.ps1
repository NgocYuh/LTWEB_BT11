$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$failures = @()
Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src') -Filter '*.java' -Recurse | ForEach-Object {
    $source = Get-Content -LiteralPath $_.FullName -Raw -Encoding UTF8
    if ($_.BaseName -notmatch '_24133023$') { $failures += $_.FullName }
    foreach ($match in [regex]::Matches($source, '\b(?:class|interface|record|enum)\s+([A-Za-z_]\w*)')) {
        if ($match.Groups[1].Value -notmatch '_24133023$') { $failures += ($_.FullName + ': ' + $match.Groups[1].Value) }
    }
}
if ($failures.Count -gt 0) { throw ($failures -join "`n") }
Write-Host 'All project Java type names and filenames end in _24133023.'
