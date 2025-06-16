# SumUp - AI-Powered Text Summarization App

<div align="center">
  <img src="docs/assets/app_icon.png" alt="SumUp Logo" width="120"/>
  
  **Transform lengthy content into concise, actionable summaries**
  
  [![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
  [![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
  [![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-orange.svg)](https://kotlinlang.org)
  [![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
</div>

## 🚀 Features

- **📝 Text Summarization**: Instantly summarize any text using Google's Gemini AI
- **📸 OCR Scanner**: Extract and summarize text from images using ML Kit
- **📄 PDF Support**: Process and summarize PDF documents (up to 50MB)
- **🎯 Multiple Personas**: Choose from different summary styles (General, Educational, Actionable, Precise)
- **📱 Material You**: Beautiful Material 3 design with dynamic theming
- **🌙 Dark Mode**: Full dark theme support
- **📊 History**: Track all your summaries with search and favorites
- **🔒 Privacy First**: Your data stays on your device

## 📱 Screenshots

<div align="center">
  <img src="docs/screenshots/main_screen.png" width="200" alt="Main Screen"/>
  <img src="docs/screenshots/result_screen.png" width="200" alt="Result Screen"/>
  <img src="docs/screenshots/ocr_screen.png" width="200" alt="OCR Screen"/>
  <img src="docs/screenshots/history_screen.png" width="200" alt="History Screen"/>
</div>

## 🏗️ Architecture

This app follows **Clean Architecture** principles with **MVVM** pattern:

```
app/
├── data/           # Data layer (Repository implementations, APIs, Database)
├── domain/         # Domain layer (Use cases, Repository interfaces, Models)
├── presentation/   # Presentation layer (UI, ViewModels, Compose)
└── di/            # Dependency injection modules
```

### Tech Stack

- **UI**: Jetpack Compose, Material 3
- **Architecture**: MVVM, Clean Architecture
- **DI**: Hilt
- **Database**: Room
- **Networking**: Retrofit, OkHttp
- **Async**: Coroutines, Flow
- **Image Loading**: Coil
- **OCR**: ML Kit Text Recognition
- **PDF**: Apache PDFBox

## 🔧 Setup

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 35
- Kotlin 1.9.0+

### API Key Setup

1. Get a free API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Copy `local.properties.template` to `local.properties`
3. Add your API key:
   ```properties
   GEMINI_API_KEY=your_actual_api_key_here
   ```

### Building

```bash
# Clone the repository
git clone https://github.com/yourusername/SumUp.git

# Open in Android Studio and sync project

# Build debug APK
./gradlew assembleDebug

# Install on device
./gradlew installDebug
```

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run all checks
./gradlew check
```

## 📖 Documentation

- [API Setup Guide](GEMINI_API_SETUP.md)
- [Testing Guide](TEST_CASES.md)
- [Architecture Overview](docs/technical/architecture.md)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Google Gemini AI for text summarization
- ML Kit for OCR capabilities
- The Android community for amazing libraries

## 📞 Contact

- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com

---

<div align="center">
  Made with ❤️ using Kotlin & Jetpack Compose
</div>