#define MyAppName "Gamity Launcher"
#define MyAppVersion "2.4.2"
#define MyAppPublisher "Gamity Launcher"
#define MyAppURL ""
#define MyAppExeName "Gamity Launcher.exe"
#define MyAppFolder "Gamity Launcher"
#define MyAppLicense ""
#define MyAppIcon "C:\Users\thomas\Desktop\launcher\gamity_launcher\logo.ico"

[Setup]
AppId={{{#MyAppName}}}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppFolder}
DisableDirPage=yes
DisableProgramGroupPage=yes
DisableFinishedPage=yes
DisableWelcomePage=yes
PrivilegesRequired=admin
PrivilegesRequiredOverridesAllowed=commandline
LicenseFile={#MyAppLicense}
SetupIconFile={#MyAppIcon}
UninstallDisplayIcon={app}\{#MyAppExeName}
Compression=lzma
SolidCompression=yes
ArchitecturesInstallIn64BitMode=x64

[Languages]
Name: "French"; MessagesFile: "compiler:Languages\French.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Registry]

[Files]
Source: "C:\Users\thomas\Desktop\launcher\gamity_launcher\GamityBootstrap\build\Gamity Launcher\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\logo.ico"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\logo.ico"; Tasks: desktopicon

[Run]

[Code]

function GetInstallLocation(): String;
var
    unInstPath: String;
    installLocation: String;
begin
    unInstPath := ExpandConstant('Software\Microsoft\Windows\CurrentVersion\Uninstall\{#emit SetupSetting("AppId")}_is1');
    installLocation := '';
    if not RegQueryStringValue(HKLM, unInstPath, 'InstallLocation', installLocation) then
        RegQueryStringValue(HKCU, unInstPath, 'InstallLocation', installLocation);
    Result := RemoveQuotes(installLocation);
end;


procedure CurStepChanged(CurStep: TSetupStep);
begin
    if CurStep = ssInstall then
    begin
    end;
end;
