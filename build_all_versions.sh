#!/bin/bash

echo "🚀 Building all SumUp versions..."
echo "================================"

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build all debug versions
echo ""
echo "🔨 Building all debug versions..."
./gradlew assembleDebug

# Check if build successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    
    # Install all versions
    echo ""
    echo "📱 Installing Dev version..."
    ./gradlew installDevDebug
    
    echo ""
    echo "📱 Installing Staging version..."
    ./gradlew installStagingDebug
    
    echo ""
    echo "📱 Installing Prod version..."
    ./gradlew installProdDebug
    
    echo ""
    echo "✅ All versions built and installed!"
    echo ""
    echo "📁 APK files location:"
    echo "   - Dev: app/build/outputs/apk/dev/debug/app-dev-debug.apk"
    echo "   - Staging: app/build/outputs/apk/staging/debug/app-staging-debug.apk"
    echo "   - Prod: app/build/outputs/apk/prod/debug/app-prod-debug.apk"
else
    echo "❌ Build failed! Please check the error messages above."
    exit 1
fi