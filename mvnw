#!/bin/sh
set -eu
version=3.9.11
checksum=03e2d65d4483a3396980629f260e25cac0d8b6f7f2791e4dc20bc83f9514db8d0f05b0479e699a5f34679250c49c8e52e961262ded468a20de0be254d8207076
cache="${MAVEN_WRAPPER_CACHE:-$HOME/.m2/finance-wrapper}"
maven="$cache/apache-maven-$version/bin/mvn"
if [ ! -x "$maven" ]; then
  mkdir -p "$cache"
  archive="$cache/apache-maven-$version-bin.zip"
  curl -fsSL "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$version/apache-maven-$version-bin.zip" -o "$archive"
  if command -v sha512sum >/dev/null 2>&1; then
    actual=$(sha512sum "$archive" | cut -d ' ' -f 1)
  else
    actual=$(shasum -a 512 "$archive" | cut -d ' ' -f 1)
  fi
  if [ "$actual" != "$checksum" ]; then
    echo "Maven archive checksum mismatch" >&2
    rm -f "$archive"
    exit 1
  fi
  unzip -q -o "$archive" -d "$cache"
  chmod +x "$maven"
fi
exec "$maven" "$@"
