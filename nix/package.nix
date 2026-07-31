{
  gnugrep,
  unzip,
  stdenv,
  gradle_9,
  openjdk25,
  makeWrapper,
}:
let
  self = stdenv.mkDerivation (finalAttrs: {
    pname = "ghost";
    version = "1.0.0";

    src = ../.;

    nativeBuildInputs = [
      gradle_9
      unzip
      gnugrep
      makeWrapper
    ];

    mitmCache = gradle_9.fetchDeps {
      pkg = self;

      # update or regenerate this by running
      # $(nix build .#ghost.mitmCache.updateScript --print-out-paths)
      data = ./deps.json;
    };

    gradleFlags = [
      "-Dorg.gradle.java.home=${openjdk25}"
    ];

    # This entire thing is a crime against humanity.
    installPhase = ''
      mkdir -p $out/{bin,lib}

      unzip build/distributions/Ghost-${finalAttrs.version}.zip

      cp -r Ghost-${finalAttrs.version}/lib/* $out/lib/

      DIST_CLASSPATH="$(grep -m1 "^CLASSPATH=" Ghost-${finalAttrs.version}/bin/Ghost)"
      DIST_CLASSPATH="''${DIST_CLASSPATH//'$APP_HOME'/"$out"}"
      DIST_CLASSPATH="''${DIST_CLASSPATH/#CLASSPATH=/}"

      makeWrapper ${openjdk25}/bin/java $out/bin/ghost \
      --set CLASSPATH "$DIST_CLASSPATH" \
      --add-flags '-classpath "$CLASSPATH" -XX:+UseZGC -XX:+UseCompactObjectHeaders -XX:+UseStringDeduplication $JAVA_OPTS dev.spiritstudios.ghost.GhostKt'
    '';
  });
in
self
