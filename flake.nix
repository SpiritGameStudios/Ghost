{
  description = "Spirit Studios' ephemeral assistant";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixpkgs-unstable";
    systems.url = "github:nix-systems/default";
  };

  outputs =
    {
      self,
      nixpkgs,
      systems,
    }:
    let
      inherit (nixpkgs) lib;
      eachSystem = (lib.genAttrs (import systems));
      eachPkgs = (f: (eachSystem (system: f nixpkgs.legacyPackages.${system})));
    in
    {
      packages = eachPkgs (pkgs: {
        default = (pkgs.callPackage ./nix/package.nix { });
      });

      devShells = eachPkgs (pkgs: {
        default = pkgs.mkShellNoCC {
          buildInputs = with pkgs; [
            nixd
            nixfmt
          ];
        };
      });

      nixosModules.default = import ./nix/nixosModule.nix self;
    };
}
