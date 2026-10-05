param([int]$Port = 8080)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$localConfig = Join-Path $projectRoot 'config\environment.local.ps1'
if (Test-Path -LiteralPath $localConfig) { . $localConfig }
. (Join-Path $PSScriptRoot 'Load-Smtp.ps1')
if (-not $env:CATALINA_HOME) { throw 'Set CATALINA_HOME to Apache Tomcat 10.1.57.' }
if (-not $env:DB_URL) { throw 'Set DB_URL in config/environment.local.ps1 first.' }
$tomcatHome = (Resolve-Path -LiteralPath $env:CATALINA_HOME).Path
$runtimeRoot = Join-Path $projectRoot '.runtime'
$env:CATALINA_BASE = Join-Path $runtimeRoot 'tomcat'
$war = Join-Path $projectRoot 'target\HNHBOOKSTORE.war'
if (-not (Test-Path -LiteralPath $war)) { throw 'Run mvn clean package before starting Tomcat.' }
foreach ($folder in @('conf','logs','temp','webapps','work')) {
    New-Item -ItemType Directory -Force -Path (Join-Path $env:CATALINA_BASE $folder) | Out-Null
}
if (-not (Test-Path -LiteralPath (Join-Path $env:CATALINA_BASE 'conf\web.xml'))) {
    Copy-Item -Path (Join-Path $tomcatHome 'conf\*') -Destination (Join-Path $env:CATALINA_BASE 'conf') -Recurse
}
$serverXml = @"
<?xml version="1.0" encoding="UTF-8"?>
<Server port="-1">
  <Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
  <Service name="Catalina">
    <Connector address="127.0.0.1" port="$Port" protocol="HTTP/1.1" connectionTimeout="20000" URIEncoding="UTF-8" />
    <Engine name="Catalina" defaultHost="localhost">
      <Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="true" />
    </Engine>
  </Service>
</Server>
"@
[IO.File]::WriteAllText((Join-Path $env:CATALINA_BASE 'conf\server.xml'), $serverXml, [Text.UTF8Encoding]::new($false))
Copy-Item -LiteralPath $war -Destination (Join-Path $env:CATALINA_BASE 'webapps\HNHBOOKSTORE.war') -Force
$nativePath = Join-Path $runtimeRoot 'native'
$env:CATALINA_OPTS = "-Dfile.encoding=UTF-8 -Djava.library.path=`"$nativePath`" --enable-native-access=ALL-UNNAMED"
Write-Host "HNHBOOKSTORE: http://localhost:$Port/HNHBOOKSTORE/home"
& (Join-Path $tomcatHome 'bin\catalina.bat') run
