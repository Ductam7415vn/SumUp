# Chucker Integration Guide

## Overview
Chucker has been successfully integrated into the SumUp app to help debug network requests and responses. It provides an in-app HTTP inspector that makes it easy to view all HTTP(S) traffic.

## Features
- 📱 **In-app Notification**: Shows a notification for each HTTP call
- 🔍 **Detailed Inspection**: View request/response headers, body, and timing
- 🚦 **Status Indicators**: Color-coded status codes for quick identification
- 📤 **Export Capability**: Share network logs for debugging
- 🔒 **Privacy**: Sensitive headers like Authorization are redacted

## Configuration
The Chucker interceptor is configured in `NetworkModule.kt` with:
- **Retention Period**: 1 hour (logs are kept for 1 hour)
- **Max Content Length**: 250KB (larger bodies are truncated)
- **Redacted Headers**: Authorization and Bearer tokens are hidden
- **Always Read Response**: Ensures response bodies are always captured

## Usage

### Viewing Network Traffic
1. When the app makes network requests, you'll see a notification from Chucker
2. Tap the notification to open Chucker's UI
3. You'll see a list of all HTTP requests made by the app
4. Tap any request to view details:
   - Overview (method, URL, status, duration)
   - Request headers and body
   - Response headers and body
   - Timing information

### Debug vs Release
- **Debug builds**: Full Chucker library with UI and notifications
- **Release builds**: No-op version (no performance impact, no UI)

### Filtering and Search
In Chucker's UI, you can:
- Filter by HTTP method (GET, POST, etc.)
- Filter by status code (2xx, 4xx, 5xx)
- Search by URL or response content
- Sort by time, duration, or size

### Sharing Logs
1. Long-press on any request in the list
2. Select "Share" to export as a cURL command or plain text
3. Useful for reproducing issues or sharing with team

## Testing Network Scenarios

### Simulating Network Errors
To test how the app handles network errors:
1. Turn on Airplane Mode to simulate no network
2. Use a proxy tool to introduce delays or errors
3. The app should show appropriate error messages via `SmartErrorHandler`

### Current Network Error Handling
- **No Internet**: Shows "No internet connection" with retry option
- **Server Errors**: Shows "Server error occurred" 
- **Timeouts**: Handled as network errors
- **API Key Issues**: Automatically falls back to mock service

## Tips
- Clear Chucker's database periodically via its settings
- Use the transaction details to debug API integration issues
- Check response times to identify performance bottlenecks
- Export problematic requests for bug reports

## Security Note
While Chucker redacts sensitive headers, be careful when sharing logs as request/response bodies may contain sensitive data.