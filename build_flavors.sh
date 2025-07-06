#!/bin/bash

echo "🚀 Building SumUp flavors..."
echo "============================"

# Build Production version first
echo ""
echo "📱 Building Production version (com.example.sumup)..."
./gradlew assembleProdDebug

if [ $? -eq 0 ]; then
    echo "✅ Production build successful!"
    
    # Build Dev version
    echo ""
    echo "📱 Building Dev version (com.example.sumup.dev)..."
    ./gradlew assembleDevDebug
    
    if [ $? -eq 0 ]; then
        echo "✅ Dev build successful!"
        
        # Build Staging version
        echo ""
        echo "📱 Building Staging version (com.example.sumup.staging)..."
        ./gradlew assembleStagingDebug
        
        if [ $? -eq 0 ]; then
            echo "✅ Staging build successful!"
            
            echo ""
            echo "📁 APK files created:"
            echo "   - Production: app/build/outputs/apk/prod/debug/app-prod-debug.apk"
            echo "   - Dev: app/build/outputs/apk/dev/debug/app-dev-debug.apk"
            echo "   - Staging: app/build/outputs/apk/staging/debug/app-staging-debug.apk"
            
            echo ""
            echo "📲 To install on device/emulator:"
            echo "   ./gradlew installProdDebug    # Install Production"
            echo "   ./gradlew installDevDebug      # Install Dev"
            echo "   ./gradlew installStagingDebug  # Install Staging"
        else
            echo "❌ Staging build failed!"
        fi
    else
        echo "❌ Dev build failed!"
    fi
else
    echo "❌ Production build failed!"
fi