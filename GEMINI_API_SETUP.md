# Gemini API Setup Guide

## 🔑 Getting Your Gemini API Key

1. **Visit Google AI Studio**
   - Go to [https://makersuite.google.com/app/apikey](https://makersuite.google.com/app/apikey)
   - Sign in with your Google account

2. **Create API Key**
   - Click "Create API Key"
   - Select your project or create a new one
   - Copy the generated API key

## 🛠️ Setting Up in SumUp

### Method 1: Using local.properties (Recommended)

1. Create or edit `local.properties` in your project root:
```properties
# Add this line with your actual API key
GEMINI_API_KEY=your_actual_api_key_here
```

2. Sync your project and rebuild

### Method 2: Environment Variable

Set the environment variable before running the app:
```bash
export GEMINI_API_KEY=your_actual_api_key_here
```

## ✅ Verifying Setup

1. **Check if API is working:**
   - Run the app
   - Try to summarize any text
   - If it works, you'll see real AI-generated summaries
   - If not, check logs for "GeminiAPI" tag

2. **Common Issues:**
   - **401 Error**: Invalid API key
   - **403 Error**: API not enabled for your project
   - **Network Error**: Check internet connection

## 🔒 Security Notes

- **NEVER** commit your API key to version control
- `local.properties` is already in `.gitignore`
- For production, use secure key management services

## 🚀 API Limits

Free tier includes:
- 60 requests per minute
- 1 million tokens per month
- Gemini 1.5 Flash model access

## 📝 Testing Without API Key

The app will automatically use mock responses if no valid API key is provided, so you can still test the UI/UX.