self:
{
  lib,
  config,
  pkgs,
  ...
}:
let
  cfg = config.services.ghost;
in
{
  options.services.ghost = {
    enable = lib.mkEnableOption "Ghost";
    package = lib.mkPackageOption self.packages.${pkgs.stdenv.hostPlatform.system} "default" { };

    workingDirectory = lib.mkOption {
      type = lib.types.path;
      default = "/var/lib/ghost";
    };

    tokenFile = lib.mkOption {
      type = lib.types.path;
    };
  };

  config = lib.mkIf cfg.enable {
    systemd.services.ghost = {
      enable = true;
      wantedBy = [ "multi-user.target" ];
      after = [ "network.target" ];

      script = ''
        export TOKEN="$(<${cfg.tokenFile})"
        ${lib.getExe cfg.package}
      '';

      serviceConfig = {
        Type = "exec";
        Restart = "on-failure";

        WorkingDirectory = cfg.workingDirectory;
      };
    };
  };
}
