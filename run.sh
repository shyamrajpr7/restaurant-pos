#!/bin/bash
echo ""
echo " =========================================="
echo "  FolioDesk POS - Restaurant Suite"
echo " =========================================="
echo ""

if [ -f "target/FolioDesk-POS.jar" ]; then
    echo " Starting FolioDesk POS..."
    java -jar target/FolioDesk-POS.jar
else
    echo " JAR not found. Compiling..."
    mvn package -q
    if [ $? -eq 0 ]; then
        echo " Build successful! Starting..."
        java -jar target/FolioDesk-POS.jar
    else
        echo " Build failed. Check errors above."
        exit 1
    fi
fi
