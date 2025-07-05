# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Feature request template
- Bug report template

## [1.0.3] - 2025-01-07

### Added
- AI Quality Metrics system with 20+ metrics for summary analysis
- Firebase integration (Analytics, Crashlytics, Performance Monitoring)
- Enterprise-grade security with encrypted API key storage
- Certificate pinning for Google APIs
- Interactive tooltip system with dynamic positioning
- Welcome card for new user onboarding
- API usage dashboard with real-time tracking
- Quick Brief persona (6th summarization style)
- Enhanced error handling with categorized display modes
- Feature discovery system for better user experience
- Comprehensive analytics tracking
- Memory-optimized PDF processing for large files
- Smart caching with OkHttp
- API usage tracking in database
- Enhanced persona recommendation based on content

### Changed
- Improved test coverage from 83.7% to 85.2%
- Enhanced UI/UX with Material You dynamic theming
- Optimized cold start time by 25%
- Improved PDF processing speed by 33%
- Reduced memory usage by 20%
- Updated dependencies to latest stable versions
- Enhanced error messages with recovery actions

### Fixed
- Memory leaks in PDF processing
- Crash rate reduced from 0.05% to 0.02%
- API timeout handling improvements
- Navigation state restoration issues
- Dark mode consistency across all screens

### Security
- Implemented SecureApiKeyProvider with multi-layer protection
- Added certificate pinning for all API calls
- Enhanced ProGuard rules for better obfuscation
- Automatic sensitive data clearing from memory

## [2.0.0] - 2024-01-16

### Added
- Complete rewrite with Clean Architecture
- Material 3 design system with dynamic theming
- Google Gemini AI integration for text summarization
- OCR text extraction using ML Kit
- PDF document processing (up to 50MB)
- Multiple summary personas (General, Educational, Actionable, Precise)
- Offline history with Room database
- Dark theme support
- Swipe gestures for history items
- Loading animations and transitions
- Input validation and sanitization
- Secure API key management
- ProGuard rules for release builds
- Comprehensive error handling
- Haptic feedback

### Changed
- Migrated from XML layouts to Jetpack Compose
- Improved app performance with coroutines
- Enhanced UI/UX with Material You guidelines
- Better error messages for users

### Fixed
- Memory leaks in camera preview
- Crash on invalid enum values
- Navigation state issues
- Database query optimization

### Security
- API keys now stored securely
- Input sanitization to prevent injection
- Clipboard security enhancements

## [1.0.0] - 2023-12-01

### Added
- Initial release
- Basic text summarization
- Simple UI with XML layouts
- Local storage

[Unreleased]: https://github.com/Ductam7415vn/SumUp/compare/v1.0.3...HEAD
[1.0.3]: https://github.com/Ductam7415vn/SumUp/compare/v2.0.0...v1.0.3
[2.0.0]: https://github.com/Ductam7415vn/SumUp/compare/v1.0.0...v2.0.0
[1.0.0]: https://github.com/Ductam7415vn/SumUp/releases/tag/v1.0.0