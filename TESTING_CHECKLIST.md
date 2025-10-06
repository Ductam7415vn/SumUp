# Testing Checklist - Priority 2 Features

**Date**: October 6, 2025
**Status**: 🟡 Pending Testing
**Features to Test**: 7

---

## 🎯 Pre-Testing Setup

### 1. Build & Install
```bash
# Stop all daemons
./gradlew --stop

# Clean build
./gradlew clean assembleDebug

# Install on device
./gradlew installDebug

# Or install APK manually
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 2. Firebase Console Setup
- [ ] Open Firebase Console
- [ ] Go to Analytics → DebugView
- [ ] Enable debug mode on device:
```bash
adb shell setprop debug.firebase.analytics.app com.example.sumup
```
- [ ] Keep DebugView open during testing

### 3. Logcat Setup
```bash
# Terminal 1: General logs
adb logcat | grep -E "MainViewModel|HistoryViewModel|SettingsViewModel|ResultViewModel"

# Terminal 2: Analytics logs
adb logcat | grep -E "AnalyticsManager|Crashlytics|PerformanceMonitor"

# Terminal 3: Error logs
adb logcat | grep -E "FATAL|AndroidRuntime|Exception"
```

---

## ✅ Feature Testing

### 1. Export UI Integration

**Test Cases:**
- [ ] Create a text summary (100 words)
- [ ] Click Export button
- [ ] Verify 3 formats shown: PDF, Markdown, Text
- [ ] Export as PDF
  - [ ] File created successfully
  - [ ] File opens correctly
  - [ ] Content is readable
- [ ] Export as Markdown
  - [ ] File created successfully
  - [ ] Markdown formatting correct
- [ ] Export as Plain Text
  - [ ] File created successfully
  - [ ] Content matches summary

**Expected Analytics Events:**
- [ ] `screen_view` (result_screen)
- [ ] `export_summary` (format: pdf/markdown/txt, success: true)
- [ ] Check Firebase DebugView for events

**Edge Cases:**
- [ ] Test export with very long summary (>1000 words)
- [ ] Test export with special characters
- [ ] Test export failure (no storage permission)

**Pass/Fail**: ⬜ PASS | ⬜ FAIL
**Notes**: _____________________

---

### 2. Draft Recovery Dialog

**Test Cases:**
- [ ] Open main screen
- [ ] Type "This is a test draft" (20 characters)
- [ ] Wait 3 seconds (auto-save triggers)
- [ ] Force close app (swipe from recent apps)
- [ ] Reopen app
- [ ] Verify dialog appears
- [ ] Check preview shows "This is a test draft"
- [ ] Check timestamp shows "Just now" or "1 minute ago"
- [ ] Check character count shows "20 characters"
- [ ] Click "Restore"
- [ ] Verify text restored to input field
- [ ] Type new text
- [ ] Force close again
- [ ] Reopen app
- [ ] Click "Discard"
- [ ] Verify input field is empty

**Expected Behavior:**
- [ ] Dialog appears within 1 second of app start
- [ ] Preview shows first 200 characters
- [ ] Timestamp is human-readable
- [ ] Restore works correctly
- [ ] Discard clears draft

**Edge Cases:**
- [ ] Test with very long draft (>1000 chars)
- [ ] Test with draft older than 24 hours
- [ ] Test with empty draft
- [ ] Test with special characters in draft

**Pass/Fail**: ⬜ PASS | ⬜ FAIL
**Notes**: _____________________

---

### 3. Search & Filter Implementation

#### 3a. Search Functionality

**Test Cases:**
- [ ] Open History screen with 10+ summaries
- [ ] Type "a" in search (1 character)
  - [ ] Results update immediately
- [ ] Type "test" (4 characters)
  - [ ] Debounce works (300ms delay)
  - [ ] Results filter correctly
- [ ] Type "nonexistent"
  - [ ] Empty state shows "No results found"
  - [ ] Clear filters button appears
- [ ] Click clear (X) button
  - [ ] Search cleared
  - [ ] All summaries shown

**Performance:**
- [ ] Type quickly (10 characters in 1 second)
- [ ] Verify only final search executes (debounce works)
- [ ] Search completes in < 500ms

**Expected Analytics Events:**
- [ ] `screen_view` (history_screen)
- [ ] `share` (method: search, contentType: query) when length >= 3

**Pass/Fail**: ⬜ PASS | ⬜ FAIL
**Notes**: _____________________

#### 3b. Filter Functionality

**Date Filter:**
- [ ] Click filter button
- [ ] Bottom sheet opens
- [ ] Select "Today"
  - [ ] Shows only today's summaries
  - [ ] Filter chip appears
  - [ ] Count updates
- [ ] Select "Yesterday"
  - [ ] Shows only yesterday's summaries
- [ ] Select "Last 7 Days"
  - [ ] Shows summaries from last week
- [ ] Select "All Time"
  - [ ] Shows all summaries

**Persona Filter:**
- [ ] Open filter sheet
- [ ] Toggle "General" persona
  - [ ] Shows only General summaries
  - [ ] Persona chip appears
- [ ] Toggle "Study" persona
  - [ ] Shows General + Study summaries (OR logic)
- [ ] Toggle "General" off
  - [ ] Shows only Study summaries
  - [ ] General chip removed

**Input Type Filter:**
- [ ] Toggle "TEXT" type
  - [ ] Shows only text summaries
- [ ] Toggle "DOCUMENT" type
  - [ ] Shows text + document summaries
- [ ] Toggle "OCR" type
  - [ ] Shows all 3 types

**Favorites Filter:**
- [ ] Toggle "Favorites only"
  - [ ] Shows only favorited summaries
  - [ ] Favorites chip appears
- [ ] Toggle off
  - [ ] Shows all summaries

**Combined Filters:**
- [ ] Apply Date: "Last 7 Days"
- [ ] Apply Persona: "General"
- [ ] Apply Type: "TEXT"
- [ ] Apply Favorites: ON
- [ ] Verify all filters work together (AND logic)
- [ ] Verify result count is correct
- [ ] Click "Clear All Filters"
- [ ] Verify all filters cleared
- [ ] Verify all summaries shown

**Expected Analytics Events:**
- [ ] Crashlytics action for each filter change
- [ ] `clear_all_filters` when cleared

**Pass/Fail**: ⬜ PASS | ⬜ FAIL
**Notes**: _____________________

---

### 4. Analytics - MainViewModel

**Test Cases:**
- [ ] Open main screen
  - [ ] Check Firebase: `screen_view` (main_screen)
- [ ] Select input type: TEXT → DOCUMENT
  - [ ] Check Firebase: `settings_change` (setting: input_type, value: document)
- [ ] Select a PDF document
  - [ ] Check Crashlytics: "Document selected" action
- [ ] Type invalid text (< 10 chars)
  - [ ] Submit
  - [ ] Check Firebase: `error` (errorType: InputValidationError)
- [ ] Type valid text (100 words)
  - [ ] Submit
  - [ ] Check Firebase: `summary_created` with metrics:
    - persona
    - wordCount
    - reductionPercentage
    - processingTimeMs
    - source: text
- [ ] Test PDF summarization
  - [ ] Check Firebase: `pdf_processed` with:
    - pageCount
    - success: true
    - processingTimeMs

**Expected Events (Minimum):**
- [ ] `screen_view` on init
- [ ] `settings_change` on input type change
- [ ] `error` on validation failure
- [ ] `summary_created` on success
- [ ] `pdf_processed` on PDF success

**Pass/Fail**: ⬜ PASS | ⬜ FAIL
**Notes**: _____________________

---

### 5. Analytics - HistoryViewModel

**Test Cases:**
- [ ] Open history screen
  - [ ] Check Firebase: `screen_view` (history_screen)
- [ ] Search for "test"
  - [ ] Check Firebase: `share` (method: search)
- [ ] Delete a summary
  - [ ] Check Firebase: `share` (method: delete_single)
- [ ] Select multiple summaries
  - [ ] Delete all
  - [ ] Check Firebase: `share` (method: delete_multiple)
- [ ] Toggle favorite on a summary
  - [ ] Check Firebase: `share` (method: add_favorite)
- [ ] Toggle favorite off
  - [ ] Check Firebase: `share` (method: remove_favorite)
- [ ] Apply date filter
  - [ ] Check Crashlytics: "Apply date filter" action
- [ ] Apply persona filter
  - [ ] Check Crashlytics: "Toggle persona filter" action

**Expected Events (Minimum):**
- [ ] `screen_view` on init
- [ ] `share` for search/delete/favorite
- [ ] Crashlytics actions for filters

**Pass/Fail**: ⬜ PASS | ⬜ FAIL
**Notes**: _____________________

---

### 6. Analytics - SettingsViewModel

**Test Cases:**
- [ ] Open settings screen
  - [ ] Check Firebase: `screen_view` (settings_screen)
- [ ] Change theme: Light → Dark
  - [ ] Check Firebase: `settings_change` (setting: theme_mode, value: dark)
- [ ] Toggle dynamic colors
  - [ ] Check Firebase: `settings_change` (setting: dynamic_color, value: true)
- [ ] Change language
  - [ ] Check Firebase: `settings_change` (setting: language, value: en)
- [ ] Add API key
  - [ ] Enter invalid key
  - [ ] Check Firebase: `error` (errorType: ApiKeyValidationError)
  - [ ] Enter valid key
  - [ ] Check Firebase: `api_key_added` (provider: gemini)
- [ ] Remove API key
  - [ ] Check Crashlytics: "API key cleared" action

**Expected Events (Minimum):**
- [ ] `screen_view` on init
- [ ] `settings_change` for all setting changes
- [ ] `api_key_added` on valid key
- [ ] `error` on validation failure

**Pass/Fail**: ⬜ PASS | ⬜ FAIL
**Notes**: _____________________

---

### 7. Analytics - ResultViewModel (Previous Session)

**Quick Verification:**
- [ ] Open result screen
  - [ ] Check Firebase: `screen_view` (result_screen)
- [ ] Export summary as PDF
  - [ ] Check Firebase: `export_summary` (format: pdf, success: true)
- [ ] Copy summary to clipboard
  - [ ] Check Firebase: `share` (method: clipboard, contentType: summary)

**Pass/Fail**: ⬜ PASS | ⬜ FAIL
**Notes**: _____________________

---

## 🐛 Bug Tracking

### Bugs Found

**Bug #1:**
- **Feature**: _____________________
- **Steps to Reproduce**: _____________________
- **Expected**: _____________________
- **Actual**: _____________________
- **Severity**: 🔴 Critical | 🟡 Medium | 🟢 Low
- **Screenshot**: _____________________

**Bug #2:**
- **Feature**: _____________________
- **Steps to Reproduce**: _____________________
- **Expected**: _____________________
- **Actual**: _____________________
- **Severity**: 🔴 Critical | 🟡 Medium | 🟢 Low
- **Screenshot**: _____________________

**Bug #3:**
- **Feature**: _____________________
- **Steps to Reproduce**: _____________________
- **Expected**: _____________________
- **Actual**: _____________________
- **Severity**: 🔴 Critical | 🟡 Medium | 🟢 Low
- **Screenshot**: _____________________

---

## 📊 Test Results Summary

### Overall Results
- **Total Features Tested**: ____ / 7
- **Features Passed**: ____
- **Features Failed**: ____
- **Bugs Found**: ____
- **Critical Bugs**: ____

### Firebase Verification
- [ ] All events appear in DebugView
- [ ] Event parameters are correct
- [ ] Crashlytics breadcrumbs work
- [ ] Performance traces work

### Performance Check
- [ ] Search debounce works (300ms)
- [ ] Filter response < 200ms
- [ ] Export completes < 3s
- [ ] No UI lag or freezing

### Next Steps
- [ ] Fix critical bugs
- [ ] Verify fixes
- [ ] Update documentation
- [ ] Ready for production: ⬜ YES | ⬜ NO

---

## 📝 Testing Notes

### Environment
- **Device**: _____________________
- **Android Version**: _____________________
- **App Version**: _____________________
- **Build Type**: Debug / Release
- **Date Tested**: _____________________

### Additional Observations
_____________________
_____________________
_____________________

---

**Tester**: _____________________
**Test Duration**: _____ hours
**Completion Date**: _____________________
**Status**: 🟢 PASS | 🔴 FAIL | 🟡 PARTIAL
