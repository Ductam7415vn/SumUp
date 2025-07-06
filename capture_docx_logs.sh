#!/bin/bash

# Clear existing logs
adb logcat -c

echo "Starting log capture for DOCX debugging..."
echo "Try to open a DOCX file in the app now..."
echo "========================================="

# Capture logs with DOCX-related filters
adb logcat -v time | grep -E "DocxProcessor|HybridDocProcessor|MainViewModel|DocumentProcessor|ProcessDocumentUseCase|extractDocxTextForPreview|Mammoth|Invalid DOCX|validateDocument|determineDocumentType"