#!/bin/bash

# Script to capture SumUp app logs for debugging API calls

echo "=== SumUp Log Capture Script ==="
echo "This script will capture logs from your Android device/emulator"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[1;35m'
CYAN='\033[1;36m'
NC='\033[0m' # No Color

# Check if adb is available
if ! command -v adb &> /dev/null
then
    echo -e "${RED}Error: adb command not found!${NC}"
    echo "Please install Android SDK tools or add adb to your PATH"
    exit 1
fi

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo -e "${RED}Error: No Android device/emulator connected!${NC}"
    echo "Please connect a device or start an emulator"
    exit 1
fi

# Create logs directory
LOGS_DIR="logs"
mkdir -p $LOGS_DIR

# Generate timestamp for log file
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOGS_DIR/sumup_logs_$TIMESTAMP.txt"
FILTERED_LOG_FILE="$LOGS_DIR/sumup_api_logs_$TIMESTAMP.txt"

echo -e "${YELLOW}Clearing old logs...${NC}"
adb logcat -c

echo -e "${GREEN}Starting log capture...${NC}"
echo "Full logs will be saved to: $LOG_FILE"
echo "API-specific logs will be saved to: $FILTERED_LOG_FILE"
echo ""
echo "Filters applied:"
echo "- NetworkModule (API setup)"
echo "- GeminiAPI/EnhancedGeminiAPI/RealGeminiAPI (API calls)"
echo "- SummaryRepository (Repository layer)"
echo "- MainViewModel (UI layer)"
echo "- SummarizeTextUseCase (Use case layer)"
echo "- GeminiErrorHandler (Error handling)"
echo "- OkHttp (HTTP requests)"
echo ""
echo -e "${BLUE}Press Ctrl+C to stop capturing logs${NC}"
echo ""

# Capture all SumUp-related logs
{
    echo "=== SumUp Log Capture Started at $(date) ==="
    echo ""
    adb logcat -v time | grep -E "(SumUp|GeminiAPI|NetworkModule|OkHttp|MainViewModel|SummaryRepository|SummarizeTextUseCase|GeminiErrorHandler|EnhancedGeminiAPI|RealGeminiAPI|MockGeminiAPI)"
} | tee "$LOG_FILE" | while IFS= read -r line
do
    # Display with color coding
    if [[ $line == *"=== "* && $line == *" ==="* ]]; then
        echo -e "${YELLOW}$line${NC}"  # Yellow for section headers
        echo "$line" >> "$FILTERED_LOG_FILE"
    elif [[ $line == *"ERROR"* || $line == *"FAILED"* || $line == *"Error:"* || $line == *"GeminiErrorHandler"* ]]; then
        echo -e "${RED}$line${NC}"  # Red for errors
        echo "$line" >> "$FILTERED_LOG_FILE"
    elif [[ $line == *"NetworkModule"* ]]; then
        echo -e "${BLUE}$line${NC}"  # Blue for NetworkModule
        echo "$line" >> "$FILTERED_LOG_FILE"
    elif [[ $line == *"GeminiAPI"* || $line == *"EnhancedGeminiAPI"* || $line == *"RealGeminiAPI"* ]]; then
        echo -e "${GREEN}$line${NC}"  # Green for API calls
        echo "$line" >> "$FILTERED_LOG_FILE"
    elif [[ $line == *"SummaryRepository"* ]]; then
        echo -e "${YELLOW}$line${NC}"  # Yellow for Repository
        echo "$line" >> "$FILTERED_LOG_FILE"
    elif [[ $line == *"MainViewModel"* ]]; then
        echo -e "${MAGENTA}$line${NC}"  # Magenta for ViewModel
        echo "$line" >> "$FILTERED_LOG_FILE"
    elif [[ $line == *"SummarizeTextUseCase"* ]]; then
        echo -e "${CYAN}$line${NC}"  # Cyan for UseCase
        echo "$line" >> "$FILTERED_LOG_FILE"
    elif [[ $line == *"OkHttp"* || $line == *"HTTP"* ]]; then
        echo -e "${CYAN}$line${NC}"  # Cyan for HTTP
        echo "$line" >> "$FILTERED_LOG_FILE"
    else
        # Only save API-related logs to filtered file
        if echo "$line" | grep -E "(API|HTTP|summarize|Summary)" &> /dev/null; then
            echo "$line" >> "$FILTERED_LOG_FILE"
        fi
    fi
done

# On script exit
trap 'echo -e "\n${GREEN}Log capture stopped.${NC}\nFull logs saved to: $LOG_FILE\nAPI logs saved to: $FILTERED_LOG_FILE"' EXIT