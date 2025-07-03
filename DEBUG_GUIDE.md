# Debug Guide for Real API Issues

## Current Status
- App is configured to use **REAL** Gemini API (forceMock = false)
- Comprehensive logging has been added throughout the API flow
- API key validation passes but generation returns 0%

## Log Capture Instructions

### Method 1: Using the capture script
```bash
# In terminal, run:
./capture_logs.sh
```

### Method 2: Manual filtering
```bash
# Clear logs first
adb logcat -c

# Then run with filters
adb logcat | grep -E "NetworkModule|EnhancedGeminiAPI|SummaryRepository|MainViewModel|OkHttp|SmartErrorHandler"
```

## Expected Log Flow

When you click "Summarize" button, you should see logs in this order:

1. **MainViewModel** - Button clicked
   ```
   MainViewModel: Summarize clicked with text length: XXX
   MainViewModel: Summary generation started
   ```

2. **SummaryRepository** - Processing request
   ```
   SummaryRepository: === SUMMARIZE TEXT CALLED ===
   SummaryRepository: Text length: XXX
   SummaryRepository: Persona: General (general)
   SummaryRepository: Calling remoteDataSource.summarizeText...
   ```

3. **NetworkModule** - API setup
   ```
   NetworkModule: === API SERVICE SETUP ===
   NetworkModule: API Key present: true
   NetworkModule: Using REAL Gemini API Service
   ```

4. **EnhancedGeminiAPI** - API call details
   ```
   EnhancedGeminiAPI: === STARTING API CALL ===
   EnhancedGeminiAPI: API Key: AIzaSyXXX...
   EnhancedGeminiAPI: Request text length: XXX
   EnhancedGeminiAPI: Prompt length: XXX
   EnhancedGeminiAPI: Executing API call...
   EnhancedGeminiAPI: Making actual HTTP request...
   ```

5. **OkHttp/NetworkModule** - HTTP request
   ```
   NetworkModule: === HTTP REQUEST ===
   NetworkModule: URL: https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=XXX
   NetworkModule: Method: POST
   OkHttp: --> POST https://generativelanguage.googleapis.com/...
   OkHttp: Content-Type: application/json
   OkHttp: {"contents":[{"parts":[{"text":"..."}]}],...}
   ```

6. **Response or Error**
   - Success:
     ```
     NetworkModule: === HTTP RESPONSE ===
     NetworkModule: Code: 200
     OkHttp: <-- 200 OK
     EnhancedGeminiAPI: Got Gemini response in XXXms
     ```
   - Failure:
     ```
     NetworkModule: === HTTP REQUEST FAILED ===
     NetworkModule: Error: XXX
     EnhancedGeminiAPI: === API CALL FAILED ===
     SmartErrorHandler: Error occurred: XXX
     ```

## Common Issues and Solutions

### 1. No logs appearing
- Make sure device is connected: `adb devices`
- Restart ADB: `adb kill-server && adb start-server`
- Check app is installed: `adb shell pm list packages | grep sumup`

### 2. API Key Issues
- Check key format in NetworkModule logs
- Verify key is not expired at: https://makersuite.google.com/app/apikey
- Test key directly:
  ```bash
  curl -X POST \
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=YOUR_KEY" \
    -H "Content-Type: application/json" \
    -d '{"contents":[{"parts":[{"text":"Hello"}]}]}'
  ```

### 3. Timeout Issues
- Current timeout: 30 seconds
- If logs show "Timed out waiting for 30000 ms", the API is too slow
- Check network connectivity

### 4. Rate Limit Issues
- Free tier: 60 requests/minute
- Look for HTTP 429 errors
- Solution: Wait or upgrade API plan

## Test Steps

1. **Start log capture**
   ```bash
   ./capture_logs.sh
   ```

2. **Open the app**

3. **Enter test text**
   - Use simple text: "This is a test paragraph to summarize."

4. **Click Summarize**

5. **Watch logs for the flow**

6. **Share the complete log output**

## Additional Debug Info

To check current API configuration:
```bash
# Check if mock is enabled
grep -n "forceMock" app/src/main/java/com/example/sumup/di/NetworkModule.kt

# Check API endpoint
grep -n "baseUrl" app/src/main/java/com/example/sumup/di/NetworkModule.kt
```

## Need More Help?

If you see specific errors, check:
- HTTP status codes (400s = client error, 500s = server error)
- Exception types (SocketTimeoutException, HttpException, etc.)
- Response body for error messages