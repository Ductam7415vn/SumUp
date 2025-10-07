# SumUp - Jira Backlog Tasks

> **Professional backlog for SumUp Android application**
> Generated: 2025-10-07
> Total Tasks: 30

---

## 📊 Quick Overview

| Category | Total Tasks |
|----------|-------------|
| Critical Bugs & Technical Debt | 3 |
| Completed Features (Documentation) | 5 |
| Feature Enhancements | 6 |
| Quality & Testing | 4 |
| Future Expansion | 4 |
| Business & Marketing | 3 |
| Infrastructure & DevOps | 3 |
| UX Enhancements | 2 |
| **TOTAL** | **30** |

---

## 🔴 CRITICAL BUGS & TECHNICAL DEBT

### **SUMUP-001: Fix PDF Processing Runtime Crash**
**Type:** Bug
**Priority:** Critical
**Story Points:** 2
**Labels:** `bug`, `pdf`, `dependency`, `hotfix`

**Description:**
PDF upload feature causes runtime crash due to missing PDFBox dependency.

**Acceptance Criteria:**
- [ ] Add `com.tom-roush:pdfbox-android:2.0.27.0` to build.gradle.kts
- [ ] Verify PDF files up to 50 pages can be processed
- [ ] Test on Android 7.0+ devices without crashes
- [ ] Update ProGuard rules to preserve PDFBox classes

**Technical Notes:**
- Dependency already configured in code but commented out
- See: `app/build.gradle.kts` and `proguard-rules.pro`

---

### **SUMUP-002: Integrate Real Gemini API Service**
**Type:** Technical Debt
**Priority:** High
**Story Points:** 8
**Labels:** `api`, `ai`, `integration`, `backend`

**Story:**
As a user, I want to generate real AI summaries using Google Gemini API so that I can get accurate text summarization.

**Acceptance Criteria:**
- [ ] Replace `MockGeminiApiService` with real API calls
- [ ] Implement API key validation with `EnhancedApiKeyManager`
- [ ] Add retry logic with exponential backoff (3 attempts)
- [ ] Handle rate limits (60 req/min) with graceful degradation
- [ ] Test with 5,000+ character documents
- [ ] Implement timeout handling (60s max)
- [ ] Add Chucker HTTP inspector for debugging

**Technical Implementation:**
- Use existing `EnhancedGeminiApiService.kt` structure
- Configure `NetworkModule.kt` to switch from MOCK to REAL service
- See: `data/remote/EnhancedGeminiApiService.kt:45`

---

### **SUMUP-003: Remove Hardcoded API Keys from BuildConfig**
**Type:** Security
**Priority:** Critical
**Story Points:** 3
**Labels:** `security`, `api-keys`, `encryption`

**Description:**
Migrate from BuildConfig API keys to encrypted runtime key management using EncryptedSharedPreferences.

**Acceptance Criteria:**
- [ ] Verify BuildConfig.GEMINI_API_KEY is empty string
- [ ] Implement in-app API key entry in Settings screen
- [ ] Use `EnhancedApiKeyManager` for encrypted storage
- [ ] Support multiple API keys with rotation
- [ ] Add key validation before storage
- [ ] Migrate existing keys using `ApiKeyMigration.kt`

**Files:**
- `utils/EnhancedApiKeyManager.kt`
- `data/remote/security/SecureApiKeyProvider.kt`

---

## ✅ COMPLETED FEATURES (For Documentation)

### **SUMUP-004: Implement Clean Architecture with MVVM**
**Type:** Epic - Completed
**Priority:** High
**Story Points:** 21
**Labels:** `architecture`, `mvvm`, `completed`

**Completed Work:**
- [x] Set up 3-layer architecture (Data, Domain, Presentation)
- [x] Implement 7 ViewModels without @HiltViewModel annotation
- [x] Create repository pattern with interfaces
- [x] Build 20+ use cases for business logic
- [x] Configure Hilt dependency injection modules

**Implementation:**
- Presentation: `presentation/screens/*/` (7 screens)
- Domain: `domain/usecase/`, `domain/model/`, `domain/repository/`
- Data: `data/repository/`, `data/local/`, `data/remote/`

---

### **SUMUP-005: Build OCR Feature with CameraX and ML Kit**
**Type:** Feature - Completed
**Priority:** High
**Story Points:** 13
**Labels:** `ocr`, `ml-kit`, `camera`, `completed`

**Completed Work:**
- [x] Integrate CameraX 1.4.0 for camera control
- [x] Implement ML Kit Text Recognition v19.0.1
- [x] Create `OcrViewModel` with camera lifecycle management
- [x] Build `ImprovedOcrScreen` with permission handling
- [x] Add text preview and editing before summarization
- [x] Support real-time text detection

**Files:**
- `presentation/screens/ocr/ImprovedOcrScreen.kt`
- `presentation/viewmodel/OcrViewModel.kt`

---

### **SUMUP-006: Implement Room Database with History Management**
**Type:** Feature - Completed
**Priority:** High
**Story Points:** 8
**Labels:** `database`, `room`, `crud`, `completed`

**Completed Work:**
- [x] Create `SummaryEntity` with proper indexing
- [x] Implement `SummaryDao` with all CRUD operations
- [x] Build `HistoryViewModel` with search and favorites
- [x] Add swipe-to-delete functionality
- [x] Support offline history viewing
- [x] Implement database migrations

**Files:**
- `data/local/SumUpDatabase.kt`
- `data/local/dao/SummaryDao.kt`
- `data/local/entity/SummaryEntity.kt`

---

### **SUMUP-007: Create Material 3 UI with Dynamic Theming**
**Type:** Feature - Completed
**Priority:** Medium
**Story Points:** 8
**Labels:** `ui`, `material3`, `theming`, `completed`

**Completed Work:**
- [x] Implement Material 3 with Compose BOM 2024.09.00
- [x] Create custom theme with extended colors
- [x] Support light/dark modes with auto-switching
- [x] Build 7 complete screens with navigation
- [x] Add adaptive layouts for different screen sizes
- [x] Implement consistent spacing/typography system

**Files:**
- `ui/theme/Theme.kt`, `Color.kt`, `Typography.kt`
- `presentation/screens/*/`

---

### **SUMUP-008: Build Multi-Format Document Processing**
**Type:** Feature - Completed
**Priority:** High
**Story Points:** 13
**Labels:** `document-processing`, `pdf`, `docx`, `completed`

**Completed Work:**
- [x] Create `DocumentProcessorFactory` for type selection
- [x] Implement `PdfDocumentProcessor` with PDFBox
- [x] Build `DocxDocumentProcessor` with Mammoth 1.5.0
- [x] Add `TxtDocumentProcessor` and `RtfDocumentProcessor`
- [x] Support documents up to 30,000 characters
- [x] Implement smart sectioning for large files

**Files:**
- `domain/usecase/ProcessDocumentUseCase.kt`
- `domain/usecase/PdfDocumentProcessor.kt`
- `domain/usecase/DocxDocumentProcessor.kt`

---

## 🚀 FEATURE ENHANCEMENTS

### **SUMUP-009: Add Usage Limits and Freemium Model**
**Type:** Feature
**Priority:** High
**Story Points:** 8
**Labels:** `monetization`, `freemium`, `analytics`

**Story:**
As a product owner, I want to implement daily usage limits (5 summaries/day) so that we can convert free users to paid subscriptions.

**Acceptance Criteria:**
- [ ] Track summary count per day using DataStore
- [ ] Display remaining summaries in UI
- [ ] Show paywall screen after limit reached
- [ ] Reset counter at midnight (user timezone)
- [ ] Add "Upgrade to Pro" call-to-action
- [ ] Track conversion analytics

**Design Notes:**
- Non-intrusive counter badge in top bar
- Graceful degradation with clear messaging
- See: `docs/development/launch-checklist.md` Week 3

---

### **SUMUP-010: Implement Google Play Billing Integration**
**Type:** Feature
**Priority:** High
**Story Points:** 13
**Labels:** `billing`, `monetization`, `subscriptions`

**Story:**
As a user, I want to purchase a Pro subscription ($2.99/month) so that I can get unlimited summaries and premium features.

**Acceptance Criteria:**
- [ ] Integrate Google Play Billing Library 6.0+
- [ ] Create subscription products in Play Console
- [ ] Implement 7-day free trial
- [ ] Handle purchase flow with proper error handling
- [ ] Verify subscription status on app launch
- [ ] Support subscription management and cancellation
- [ ] Add restore purchases functionality

**Subscription Tiers:**
- **Free**: 5 summaries/day, text only
- **Pro**: Unlimited, all formats, priority processing

---

### **SUMUP-011: Add Firebase Analytics and Crashlytics**
**Type:** Feature
**Priority:** High
**Story Points:** 5
**Labels:** `analytics`, `crashlytics`, `firebase`, `monitoring`

**Story:**
As a developer, I want to track user behavior and crashes so that I can improve app quality and make data-driven decisions.

**Acceptance Criteria:**
- [ ] Integrate Firebase BOM 33.6.0 (prod flavor only)
- [ ] Implement custom events (summary_created, export_clicked, etc.)
- [ ] Track user properties (theme, language, subscription status)
- [ ] Configure Crashlytics with proper ProGuard mapping
- [ ] Add performance monitoring for API calls
- [ ] Create analytics dashboard in Firebase Console

**Implementation:**
- Structure exists in `analytics/AnalyticsHelper.kt`
- Enable in `AnalyticsModule.kt`
- Prod-only config: `app/build.gradle.kts` (flavors)

---

### **SUMUP-012: Implement Advanced Search and Filtering**
**Type:** Feature
**Priority:** Medium
**Story Points:** 5
**Labels:** `search`, `filtering`, `ux-enhancement`

**Story:**
As a user, I want to search and filter my summary history so that I can quickly find specific summaries.

**Acceptance Criteria:**
- [ ] Add search bar with real-time filtering
- [ ] Filter by date range (last 7 days, 30 days, custom)
- [ ] Filter by persona type
- [ ] Filter by favorites only
- [ ] Sort by date, length, or alphabetically
- [ ] Highlight search terms in results
- [ ] Show "No results" empty state

**UI Location:** History screen (`presentation/screens/history/`)

---

### **SUMUP-013: Add Export to Cloud Storage**
**Type:** Feature
**Priority:** Medium
**Story Points:** 8
**Labels:** `export`, `cloud`, `integration`

**Story:**
As a user, I want to export summaries to Google Drive or Dropbox so that I can access them across devices.

**Acceptance Criteria:**
- [ ] Integrate Google Drive API for export
- [ ] Add Dropbox SDK integration
- [ ] Support direct sharing to cloud apps
- [ ] Show export progress indicator
- [ ] Handle offline scenarios gracefully
- [ ] Respect user's cloud storage permissions

**Dependencies:**
- Google Play Services Drive
- Dropbox Core SDK

---

### **SUMUP-014: Implement Draft Auto-Save Enhancement**
**Type:** Improvement
**Priority:** Medium
**Story Points:** 3
**Labels:** `draft`, `ux-enhancement`, `persistence`

**Description:**
Enhance existing `DraftManager` with visual indicators and recovery UI.

**Acceptance Criteria:**
- [ ] Show "Draft saved" indicator in UI
- [ ] Add draft recovery dialog on app restart
- [ ] Display timestamp of saved draft
- [ ] Support manual draft deletion
- [ ] Extend recovery window to 48 hours
- [ ] Add draft preview in recovery dialog

**Current Implementation:**
- Auto-save exists with 2-second debounce
- See: `utils/drafts/DraftManager.kt`

---

## 🧪 QUALITY & TESTING

### **SUMUP-015: Increase Unit Test Coverage to 80%**
**Type:** Technical Debt
**Priority:** Medium
**Story Points:** 13
**Labels:** `testing`, `unit-tests`, `quality`

**Current Coverage:** ~45%
**Target Coverage:** 80%

**Acceptance Criteria:**
- [ ] Add ViewModel tests using Coroutine TestDispatcher
- [ ] Test all use cases with MockK
- [ ] Add repository tests with fake data sources
- [ ] Test error handling paths comprehensively
- [ ] Create parameterized tests for validators
- [ ] Generate JaCoCo coverage reports
- [ ] Set up CI/CD pipeline with coverage gates

**Test Structure:**
- ViewModels: `presentation/viewmodel/*Test.kt`
- Use Cases: `domain/usecase/*Test.kt`
- Repositories: `data/repository/*Test.kt`

---

### **SUMUP-016: Add UI/Integration Tests with Compose Testing**
**Type:** Technical Debt
**Priority:** Medium
**Story Points:** 8
**Labels:** `testing`, `ui-tests`, `compose-testing`

**Acceptance Criteria:**
- [ ] Write screen-level tests for all 7 screens
- [ ] Test navigation flows end-to-end
- [ ] Verify user interactions (clicks, swipes, input)
- [ ] Test error state displays
- [ ] Verify theme switching behavior
- [ ] Add screenshot tests for visual regression
- [ ] Run tests on multiple device configurations

**Framework:**
- Compose Testing (ui-test-junit4)
- Espresso for complex interactions

---

### **SUMUP-017: Performance Optimization and Profiling**
**Type:** Technical Debt
**Priority:** Medium
**Story Points:** 8
**Labels:** `performance`, `optimization`, `profiling`

**Acceptance Criteria:**
- [ ] Reduce cold start time to <500ms
- [ ] Optimize Compose recompositions (use remember/derivedStateOf)
- [ ] Implement image lazy loading with Coil
- [ ] Add database query indexing for history search
- [ ] Profile memory usage (target <150MB peak)
- [ ] Optimize APK size (target <50MB)
- [ ] Add ProGuard R8 optimization for release builds

**Tools:**
- Android Profiler (CPU, Memory, Network)
- Layout Inspector
- Baseline Profiles

---

### **SUMUP-018: Implement Comprehensive Error Handling**
**Type:** Improvement
**Priority:** Medium
**Story Points:** 5
**Labels:** `error-handling`, `ux`, `resilience`

**Description:**
Enhance existing `AppError` sealed class with more error types and recovery actions.

**Acceptance Criteria:**
- [ ] Add error types: `DocumentTooLargeError`, `UnsupportedFormatError`
- [ ] Implement contextual error display (Dialog/Snackbar/Inline)
- [ ] Add retry actions for transient errors
- [ ] Include haptic feedback on errors
- [ ] Log errors to Crashlytics
- [ ] Create user-friendly error messages (avoid technical jargon)

**Current Implementation:**
- `domain/model/AppError.kt` (sealed class)
- `utils/SmartErrorHandler.kt`

---

## 📱 FUTURE EXPANSION

### **SUMUP-019: Build iOS Version with Kotlin Multiplatform**
**Type:** Epic
**Priority:** Low
**Story Points:** 34
**Labels:** `ios`, `kmp`, `multiplatform`, `future`

**Story:**
As a product owner, I want an iOS version so that we can reach Apple users and increase market share.

**High-Level Requirements:**
- [ ] Set up Kotlin Multiplatform Mobile (KMM) project
- [ ] Share domain and data layers across platforms
- [ ] Build SwiftUI presentation layer for iOS
- [ ] Implement iOS-specific features (Face ID, Shortcuts)
- [ ] Configure Xcode project and signing
- [ ] Submit to Apple App Store

**Estimated Timeline:** 3-4 months

---

### **SUMUP-020: Create Chrome Extension for Web Summarization**
**Type:** Epic
**Priority:** Low
**Story Points:** 21
**Labels:** `chrome-extension`, `web`, `future`

**Story:**
As a user, I want a Chrome extension so that I can summarize web articles without leaving my browser.

**High-Level Requirements:**
- [ ] Build Manifest V3 Chrome extension
- [ ] Implement right-click context menu for text selection
- [ ] Add popup UI for quick summaries
- [ ] Share backend API with Android app
- [ ] Support browser dark mode
- [ ] Publish to Chrome Web Store

**Tech Stack:**
- TypeScript + React
- Chrome Extension APIs
- Shared Gemini API service

---

### **SUMUP-021: Add Multi-Language Support (10+ Languages)**
**Type:** Feature
**Priority:** Medium
**Story Points:** 13
**Labels:** `i18n`, `localization`, `languages`

**Story:**
As a global user, I want the app in my native language so that I can use it more comfortably.

**Acceptance Criteria:**
- [ ] Add string resources for: Spanish, French, German, Chinese, Japanese, Korean, Arabic, Portuguese, Russian, Hindi
- [ ] Implement locale switching in Settings
- [ ] Support RTL languages (Arabic)
- [ ] Translate UI strings (not summaries)
- [ ] Test with native speakers
- [ ] Update Play Store listings for each language

**Current Support:** English, Vietnamese

---

### **SUMUP-022: Implement Cloud Sync with Firebase**
**Type:** Feature
**Priority:** Medium
**Story Points:** 13
**Labels:** `cloud-sync`, `firebase`, `multi-device`

**Story:**
As a user, I want my summaries synced across devices so that I can access them anywhere.

**Acceptance Criteria:**
- [ ] Set up Firebase Firestore for summary storage
- [ ] Implement auth with Google Sign-In
- [ ] Sync summaries on app launch and after creation
- [ ] Handle offline sync conflicts
- [ ] Show sync status indicator
- [ ] Add "Delete from cloud" option
- [ ] Respect storage limits (100 summaries for free, unlimited for Pro)

**Security:**
- End-to-end encryption for sensitive summaries
- GDPR-compliant data handling

---

## 📊 BUSINESS & MARKETING

### **SUMUP-023: Create Google Play Store Marketing Assets**
**Type:** Task
**Priority:** High
**Story Points:** 5
**Labels:** `marketing`, `app-store`, `assets`

**Acceptance Criteria:**
- [ ] Design 5 high-quality screenshots (1080x1920)
- [ ] Create feature graphic (1024x500)
- [ ] Write optimized app description (4000 chars max)
- [ ] Design app icon in all required sizes
- [ ] Create promotional video (30-60s)
- [ ] Write short description (80 chars)
- [ ] Research and include ASO keywords

**Deliverables:**
- Screenshots: Main, Result, History, Settings, OCR
- Feature graphic with app branding
- Video showcasing key features

---

### **SUMUP-024: Write Privacy Policy and Terms of Service**
**Type:** Task
**Priority:** Critical
**Story Points:** 3
**Labels:** `legal`, `compliance`, `gdpr`

**Acceptance Criteria:**
- [ ] Draft privacy policy covering data collection
- [ ] Include GDPR compliance statements
- [ ] Detail API key storage and encryption
- [ ] Explain analytics and crash reporting
- [ ] Write terms of service with subscription terms
- [ ] Add refund policy
- [ ] Host documents on public URL
- [ ] Link from app Settings screen

**Legal Review:**
- Consider consulting legal expert for compliance

---

### **SUMUP-025: Set Up Beta Testing Program**
**Type:** Task
**Priority:** High
**Story Points:** 3
**Labels:** `beta`, `testing`, `user-feedback`

**Acceptance Criteria:**
- [ ] Create closed testing track in Play Console
- [ ] Recruit 20+ beta testers
- [ ] Set up feedback collection form (Google Forms)
- [ ] Define beta testing criteria and goals
- [ ] Run beta for 2 weeks minimum
- [ ] Collect and analyze feedback
- [ ] Fix critical issues before production launch

**Beta Focus Areas:**
- API stability and error handling
- UI/UX feedback
- Performance on various devices

---

## 🔧 INFRASTRUCTURE & DEVOPS

### **SUMUP-026: Set Up CI/CD Pipeline with GitHub Actions**
**Type:** Technical Debt
**Priority:** Medium
**Story Points:** 8
**Labels:** `ci-cd`, `automation`, `github-actions`

**Acceptance Criteria:**
- [ ] Create workflow for automated builds on PR
- [ ] Add unit test execution in CI
- [ ] Generate test coverage reports
- [ ] Run lint checks automatically
- [ ] Build APKs for all flavors (dev, staging, prod)
- [ ] Deploy beta builds to Play Console
- [ ] Add branch protection rules

**Workflow Files:**
- `.github/workflows/build.yml`
- `.github/workflows/test.yml`
- `.github/workflows/release.yml`

---

### **SUMUP-027: Implement Certificate Pinning for API Security**
**Type:** Security
**Priority:** Medium
**Story Points:** 5
**Labels:** `security`, `certificate-pinning`, `network`

**Acceptance Criteria:**
- [ ] Pin Gemini API certificates in production builds
- [ ] Use OkHttp CertificatePinner
- [ ] Add backup pins for certificate rotation
- [ ] Test with MITM proxy to verify pinning
- [ ] Handle certificate validation errors gracefully
- [ ] Document certificate update process

**Implementation:**
- Prod-only: `di/NetworkModule.kt`
- See ProGuard rules for security obfuscation

---

### **SUMUP-028: Add Proactive Health Monitoring Dashboard**
**Type:** Feature
**Priority:** Low
**Story Points:** 8
**Labels:** `monitoring`, `observability`, `devops`

**Acceptance Criteria:**
- [ ] Set up Firebase Performance Monitoring
- [ ] Track API response times (p50, p95, p99)
- [ ] Monitor app startup metrics
- [ ] Track memory and CPU usage trends
- [ ] Create alerts for error rate spikes
- [ ] Build custom dashboard in Firebase Console

**Metrics to Track:**
- Summary generation success rate
- Average processing time
- Crash-free session rate
- Daily active users (DAU)

---

## 🎨 UX ENHANCEMENTS

### **SUMUP-029: Implement Onboarding Flow for New Users**
**Type:** Feature
**Priority:** Medium
**Story Points:** 5
**Labels:** `onboarding`, `ux`, `first-run`

**Story:**
As a new user, I want a guided onboarding experience so that I understand how to use the app.

**Acceptance Criteria:**
- [ ] Create 3-5 onboarding screens with key features
- [ ] Show API key setup instructions
- [ ] Demonstrate text input and summarization
- [ ] Highlight export and history features
- [ ] Add "Skip" option for experienced users
- [ ] Show only on first app launch (DataStore flag)

**Design:**
- Use ViewPager2 for swipeable screens
- Include illustrations for visual appeal

**Current Implementation:**
- Screen exists: `presentation/screens/onboarding/OnboardingScreen.kt`
- Needs UI completion and integration

---

### **SUMUP-030: Add Voice Input for Text Summarization**
**Type:** Feature
**Priority:** Low
**Story Points:** 8
**Labels:** `voice`, `speech-to-text`, `accessibility`

**Story:**
As a user, I want to speak my text instead of typing so that I can create summaries hands-free.

**Acceptance Criteria:**
- [ ] Integrate Android SpeechRecognizer API
- [ ] Add microphone permission handling
- [ ] Show real-time speech-to-text transcription
- [ ] Support pause/resume recording
- [ ] Handle background noise gracefully
- [ ] Support English and Vietnamese voice input
- [ ] Add visual audio waveform indicator

**UI:**
- Floating microphone button on main screen
- Speech-to-text overlay with live transcription

---

## 🏷️ Labels Reference

### Priority Labels
- `critical` - Must be fixed immediately
- `high` - Important for next release
- `medium` - Should be included when possible
- `low` - Nice to have, future consideration

### Type Labels
- `bug` - Something broken that needs fixing
- `feature` - New functionality
- `improvement` - Enhancement to existing feature
- `technical-debt` - Code quality or architecture improvements
- `epic` - Large feature spanning multiple sprints
- `task` - Specific work item (documentation, assets, etc.)

### Category Labels
- `security` - Security-related work
- `api` - Backend/API integration
- `ui` - User interface changes
- `database` - Data persistence
- `testing` - Quality assurance
- `monetization` - Revenue-generating features
- `analytics` - Tracking and metrics
- `performance` - Speed and optimization
- `legal` - Legal compliance
- `marketing` - User acquisition and retention

### Status Labels
- `completed` - Work already done (documentation)
- `in-progress` - Currently being worked on
- `planned` - Scheduled for future sprint
- `blocked` - Waiting on dependencies

---

## 📋 Sprint Planning Guide

### **Sprint 1: Critical Fixes (1 week)**
- SUMUP-001 (PDF crash fix)
- SUMUP-002 (Real API integration)
- SUMUP-003 (API key security)

**Total Story Points:** 13

---

### **Sprint 2: Monetization Foundation (2 weeks)**
- SUMUP-009 (Usage limits)
- SUMUP-010 (Google Play Billing)
- SUMUP-011 (Firebase Analytics)
- SUMUP-023 (Marketing assets)

**Total Story Points:** 31

---

### **Sprint 3: Quality & Launch Prep (2 weeks)**
- SUMUP-015 (Unit tests)
- SUMUP-016 (UI tests)
- SUMUP-024 (Legal documents)
- SUMUP-025 (Beta testing)

**Total Story Points:** 27

---

### **Sprint 4: UX Enhancements (2 weeks)**
- SUMUP-012 (Advanced search)
- SUMUP-014 (Draft improvements)
- SUMUP-018 (Error handling)
- SUMUP-029 (Onboarding)

**Total Story Points:** 18

---

### **Future Sprints: Expansion Features**
- SUMUP-013 (Cloud export)
- SUMUP-021 (Multi-language)
- SUMUP-022 (Cloud sync)
- SUMUP-026 (CI/CD)
- SUMUP-027 (Certificate pinning)
- SUMUP-030 (Voice input)

**Total Story Points:** 47

---

### **Long-term Roadmap (3-6 months)**
- SUMUP-019 (iOS app)
- SUMUP-020 (Chrome extension)
- SUMUP-028 (Health monitoring)

**Total Story Points:** 63

---

## 📈 Velocity Tracking

**Recommended Sprint Velocity:**
- Small team (1-2 devs): 13-21 story points per 2-week sprint
- Medium team (3-4 devs): 26-34 story points per 2-week sprint
- Large team (5+ devs): 40+ story points per 2-week sprint

**Story Point Scale:**
- 1-2: Trivial task (hours)
- 3-5: Small feature (1-2 days)
- 8: Medium feature (3-5 days)
- 13: Large feature (1-2 weeks)
- 21+: Epic (multiple weeks, should be broken down)

---

## 🔗 Related Documentation

- [README.md](README.md) - Project overview
- [CLAUDE.md](CLAUDE.md) - Development guide
- [docs/development/05-development-roadmap.md](docs/development/05-development-roadmap.md) - Strategic roadmap
- [docs/development/launch-checklist.md](docs/development/launch-checklist.md) - Launch preparation
- [docs/api/planned-vs-implemented-features.md](docs/api/planned-vs-implemented-features.md) - Feature status

---

## 📝 How to Import to Jira

### **CSV Import Method:**
1. Export this file to CSV format
2. Map columns: Summary, Description, Story Points, Labels, Priority, Type
3. Import to Jira project
4. Assign epic links manually

### **Manual Creation Method:**
1. Create epics first (SUMUP-004, SUMUP-019, SUMUP-020)
2. Create individual stories under epics
3. Copy acceptance criteria to description
4. Add labels and story points
5. Link related issues

### **API/Automation Method:**
1. Use Jira REST API for bulk creation
2. Parse this markdown file
3. Create issues programmatically
4. Maintain traceability with issue keys

---

**Generated for:** SumUp Android Application
**Backlog Owner:** Duc Tam
**Last Updated:** 2025-10-07
**Total Estimated Effort:** 241 story points (~6-9 months for 2-3 developers)
