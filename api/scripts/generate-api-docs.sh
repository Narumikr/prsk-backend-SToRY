#!/bin/bash
set -e

# get directory path
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
API_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
DIST_DIR=$API_DIR/dist

# file name
BUNDLED_OPENAPI="bundled-openapi.yaml"
GEN_INDEX_HTML="index.html"

# create dist directory if not exists
mkdir -p $DIST_DIR

# bundle openapi yaml
echo "🎸 Bundling OpenAPI..."
echo
if ! redocly bundle $API_DIR/openapi.yaml -o $DIST_DIR/$BUNDLED_OPENAPI; then
    echo "❌ Failed to bundle OpenAPI"
    echo
    exit 1
fi

echo
echo "💫 Generated: $DIST_DIR/$BUNDLED_OPENAPI"
echo

# generate index.html
echo "🎵 Building HTML documentation..."
echo
if ! redocly build-docs $DIST_DIR/$BUNDLED_OPENAPI -o $DIST_DIR/$GEN_INDEX_HTML; then
    echo "❌ Failed to build documentation"
    echo
    exit 1
fi

echo
echo "🍀 Generated: $DIST_DIR/$GEN_INDEX_HTML"
echo

# open HTML file in browser
echo "🌐 Opening documentation in browser..."
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    open $DIST_DIR/$GEN_INDEX_HTML
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    if command -v xdg-open > /dev/null; then
        xdg-open $DIST_DIR/$GEN_INDEX_HTML
    elif command -v sensible-browser > /dev/null; then
        sensible-browser $DIST_DIR/$GEN_INDEX_HTML
    else
        echo "⚠️  ブラウザを自動で開けませんでした。手動で開いてください: $DIST_DIR/$GEN_INDEX_HTML"
    fi
elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]]; then
    # Windows (Git Bash / Cygwin)
    start $DIST_DIR/$GEN_INDEX_HTML
else
    echo "⚠️  お使いのOSでは自動でブラウザを開けませんでした。手動で開いてください: $DIST_DIR/$GEN_INDEX_HTML"
fi