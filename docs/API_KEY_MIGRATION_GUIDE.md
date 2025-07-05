# API Key Migration Guide

## Overview

This document describes the migration from the old `ApiKeyManager` to the new `EnhancedApiKeyManager` system in the SumUp Android app.

## Migration Benefits

### Old System (ApiKeyManager)
- Single API key support only
- Basic encryption using Android Keystore
- No usage tracking
- No key rotation reminders
- Limited functionality

### New System (EnhancedApiKeyManager)
- Multiple API keys support
- Advanced encryption with Android Keystore
- Comprehensive usage tracking and analytics
- Key rotation reminders for security
- Export/Import functionality
- Per-key usage statistics
- Active key selection

## Migration Process

### Automatic Migration

The migration happens automatically when the app starts:

1. **On App Launch**: `MainActivity` triggers `ApiKeyMigration.migrateIfNeeded()`
2. **Check Migration Status**: If not already migrated, proceed
3. **Retrieve Old Keys**: 
   - Check old encrypted storage (`secure_api_keys`)
   - Check BuildConfig as fallback
4. **Transfer to New System**: Add keys to `EnhancedApiKeyManager`
5. **Cleanup**: Remove old storage files
6. **Mark Complete**: Set migration flag to prevent re-running

### Code Changes

#### 1. Dependency Updates

```kotlin
// Before
@Inject lateinit var apiKeyManager: ApiKeyManager

// After
@Inject lateinit var enhancedApiKeyManager: EnhancedApiKeyManager
```

#### 2. API Key Operations

```kotlin
// Before - Single key
apiKeyManager.saveGeminiApiKey(key)
val key = apiKeyManager.getGeminiApiKey()
val hasKey = apiKeyManager.hasValidApiKey()

// After - Multiple keys with management
enhancedApiKeyManager.addApiKey(name, key)
val activeKey = enhancedApiKeyManager.getActiveApiKey()
enhancedApiKeyManager.setActiveKey(keyId)
enhancedApiKeyManager.deleteApiKey(keyId)
```

#### 3. Usage Tracking

```kotlin
// New feature - track API usage
val stats = enhancedApiKeyManager.getUsageStats()
enhancedApiKeyManager.updateKeyUsage(keyId)
```

## Testing the Migration

### Manual Testing

1. **Fresh Install**:
   - Install app with no previous data
   - Add API key through settings
   - Verify it appears in the new management UI

2. **Upgrade Scenario**:
   - Install old version with API key
   - Upgrade to new version
   - Verify key is automatically migrated
   - Check that old storage is cleaned up

3. **BuildConfig Migration**:
   - Set API key in BuildConfig
   - Install app
   - Verify key is imported on first launch

### Unit Tests

Run the migration tests:
```bash
./gradlew test --tests "com.example.sumup.utils.migration.ApiKeyMigrationTest"
```

## Rollback Plan

If issues occur:

1. **Immediate Rollback**:
   - The old `ApiKeyManager` is marked deprecated but still functional
   - Can temporarily revert NetworkModule to use old manager

2. **Data Recovery**:
   - Migration doesn't delete keys, only transfers them
   - BuildConfig keys remain as fallback

## Future Cleanup

After successful migration (recommended after 2-3 releases):

1. Remove `ApiKeyManager` class entirely
2. Remove migration code from MainActivity
3. Remove `ApiKeyMigration` class
4. Clean up any remaining references

## Common Issues & Solutions

### Issue: Keys not appearing after migration
**Solution**: Check logs for migration errors. Use `resetMigration()` in debug builds to retry.

### Issue: App crashes on startup
**Solution**: Migration has try-catch protection. Check for Hilt injection issues.

### Issue: Multiple duplicate keys after migration
**Solution**: The migration checks for existing keys before adding. This shouldn't happen in normal flow.

## API Reference

### EnhancedApiKeyManager

```kotlin
// Add a new API key
fun addApiKey(name: String, key: String): ApiKeyInfo

// Get the active API key
fun getActiveApiKey(): String?

// Set active key
fun setActiveKey(keyId: String)

// Delete a key
fun deleteApiKey(keyId: String)

// Get usage statistics
fun getUsageStats(): ApiUsageStats

// Export/Import
fun exportSettings(password: String): ExportData
fun importSettings(data: ExportData, password: String): Boolean
```

## Support

For issues or questions:
1. Check migration logs: Filter by "ApiKeyMigration" tag
2. Use debug menu to view current API key status
3. File issues with migration logs attached