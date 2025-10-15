#!/bin/bash

# get directory path
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
API_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
DIST_DIR=$API_DIR/dist

# create dist directory if not exists
mkdir -p $DIST_DIR

# bundle openapi yaml
redocly bundle $API_DIR/openapi.yaml -o $DIST_DIR/bundled-openapi.yaml

# generate index.html
redocly build-docs $DIST_DIR/bundled-openapi.yaml -o $DIST_DIR/index.html

echo "✅ Generated: $DIST_DIR/bundled-openapi.yaml"
echo "✅ Generated: $DIST_DIR/index.html"
