#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

mkdir -p out/production/YRRAK
javac -encoding UTF-8 -d out/production/YRRAK $(find src -name "*.java")
java -cp out/production/YRRAK Main
