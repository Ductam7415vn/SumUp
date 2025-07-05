# CHƯƠNG 5: THIẾT KẾ CƠ SỞ DỮ LIỆU

## 5.1. Tổng quan thiết kế cơ sở dữ liệu

### 5.1.1. Lựa chọn công nghệ
SumUp sử dụng **Room Database** - một abstraction layer trên SQLite, được Google khuyến nghị cho Android development vì:

- **Type Safety**: Compile-time verification của SQL queries
- **Convenience**: Giảm boilerplate code đáng kể
- **Migration Support**: Hỗ trợ database migration mạnh mẽ
- **Reactive**: Tích hợp tốt với LiveData và Flow
- **Performance**: Optimized cho Android

### 5.1.2. Yêu cầu lưu trữ
1. **Lưu trữ lịch sử tóm tắt**: Mọi kết quả tóm tắt cần được lưu lại
2. **Tìm kiếm nhanh**: Hỗ trợ full-text search trong lịch sử
3. **Quản lý dung lượng**: Tự động cleanup khi database quá lớn
4. **Offline access**: Dữ liệu phải accessible khi không có mạng
5. **Data integrity**: Đảm bảo toàn vẹn dữ liệu

## 5.2. Entity Relationship Diagram (ERD)

### 5.2.1. Mô hình quan hệ

```
┌─────────────────────────────────────────────────────────────────┐
│                          SUMMARIES                              │
├─────────────────────────────────────────────────────────────────┤
│ PK │ id                  │ TEXT      │ UUID                     │
│    │ original_text       │ TEXT      │ NOT NULL                 │
│    │ summarized_text     │ TEXT      │ NOT NULL                 │
│    │ persona             │ TEXT      │ NOT NULL                 │
│    │ timestamp           │ INTEGER   │ NOT NULL                 │
│    │ word_count_original │ INTEGER   │ NOT NULL                 │
│    │ word_count_summary  │ INTEGER   │ NOT NULL                 │
│    │ language            │ TEXT      │ DEFAULT 'vi'             │
│    │ input_type          │ TEXT      │ DEFAULT 'TEXT'           │
│    │ source_info         │ TEXT      │ NULL                     │
│    │ processing_time_ms  │ INTEGER   │ NULL                     │
│    │ is_favorite         │ INTEGER   │ DEFAULT 0                │
│    │ tags                │ TEXT      │ NULL                     │
└─────────────────────────────────────────────────────────────────┘
                                │
                                │ 1
                                │
                                │ *
┌─────────────────────────────────────────────────────────────────┐
│                      SUMMARY_METADATA                            │
├─────────────────────────────────────────────────────────────────┤
│ PK │ id                  │ INTEGER   │ AUTOINCREMENT            │
│ FK │ summary_id          │ TEXT      │ REFERENCES summaries(id) │
│    │ key                 │ TEXT      │ NOT NULL                 │
│    │ value               │ TEXT      │ NOT NULL                 │
│    │ created_at          │ INTEGER   │ NOT NULL                 │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                           DRAFTS                                 │
├─────────────────────────────────────────────────────────────────┤
│ PK │ id                  │ TEXT      │ UUID                     │
│    │ content             │ TEXT      │ NOT NULL                 │
│    │ input_type          │ TEXT      │ NOT NULL                 │
│    │ last_modified       │ INTEGER   │ NOT NULL                 │
│    │ source_uri          │ TEXT      │ NULL                     │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                        USER_PREFERENCES                          │
├─────────────────────────────────────────────────────────────────┤
│ PK │ key                 │ TEXT      │ NOT NULL                 │
│    │ value               │ TEXT      │ NOT NULL                 │
│    │ updated_at          │ INTEGER   │ NOT NULL                 │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    AI_QUALITY_METRICS (NEW v1.0.3)              │
├─────────────────────────────────────────────────────────────────┤
│ PK │ id                  │ TEXT      │ UUID                     │
│ FK │ summary_id          │ TEXT      │ REFERENCES summaries(id) │
│    │ coherence_score     │ REAL      │ NOT NULL                 │
│    │ readability_level   │ TEXT      │ NOT NULL                 │
│    │ information_density │ REAL      │ NOT NULL                 │
│    │ clarity_score       │ REAL      │ NOT NULL                 │
│    │ overall_confidence  │ REAL      │ NOT NULL                 │
│    │ metrics_json        │ TEXT      │ NOT NULL                 │
│    │ created_at          │ INTEGER   │ NOT NULL                 │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    API_USAGE_LOGS (NEW v1.0.3)                  │
├─────────────────────────────────────────────────────────────────┤
│ PK │ id                  │ INTEGER   │ AUTOINCREMENT            │
│    │ timestamp           │ INTEGER   │ NOT NULL                 │
│    │ endpoint            │ TEXT      │ NOT NULL                 │
│    │ request_tokens      │ INTEGER   │ NOT NULL                 │
│    │ response_tokens     │ INTEGER   │ NOT NULL                 │
│    │ model               │ TEXT      │ NOT NULL                 │
│    │ success             │ INTEGER   │ NOT NULL                 │
│    │ error_message       │ TEXT      │ NULL                     │
│    │ processing_time_ms  │ INTEGER   │ NOT NULL                 │
└─────────────────────────────────────────────────────────────────┘
```

### 5.2.2. Quan hệ giữa các bảng
- **SUMMARIES ↔ SUMMARY_METADATA**: One-to-Many (1 summary có nhiều metadata)
- **DRAFTS**: Standalone (không có quan hệ)
- **USER_PREFERENCES**: Standalone (key-value store)

## 5.3. Chi tiết thiết kế bảng

### 5.3.1. Bảng SUMMARIES

**Mục đích**: Lưu trữ tất cả kết quả tóm tắt

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | TEXT | PRIMARY KEY | UUID duy nhất cho mỗi summary |
| original_text | TEXT | NOT NULL | Văn bản gốc |
| summarized_text | TEXT | NOT NULL | Văn bản đã tóm tắt |
| persona | TEXT | NOT NULL | Loại persona sử dụng |
| timestamp | INTEGER | NOT NULL | Unix timestamp khi tạo |
| word_count_original | INTEGER | NOT NULL | Số từ văn bản gốc |
| word_count_summary | INTEGER | NOT NULL | Số từ văn bản tóm tắt |
| language | TEXT | DEFAULT 'vi' | Ngôn ngữ (vi/en) |
| input_type | TEXT | DEFAULT 'TEXT' | TEXT/PDF/OCR |
| source_info | TEXT | NULL | Thông tin nguồn (file name, etc) |
| processing_time_ms | INTEGER | NULL | Thời gian xử lý (ms) |
| is_favorite | INTEGER | DEFAULT 0 | Đánh dấu yêu thích (0/1) |
| tags | TEXT | NULL | Tags phân loại (JSON array) |

**Indexes**:
```sql
CREATE INDEX idx_summaries_timestamp ON summaries(timestamp DESC);
CREATE INDEX idx_summaries_persona ON summaries(persona);
CREATE INDEX idx_summaries_favorite ON summaries(is_favorite);
CREATE VIRTUAL TABLE summaries_fts USING fts5(
    original_text, 
    summarized_text, 
    content=summaries
);
```

### 5.3.2. Bảng SUMMARY_METADATA

**Mục đích**: Lưu trữ metadata mở rộng cho summaries

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | ID tự tăng |
| summary_id | TEXT | FOREIGN KEY | Reference đến summaries.id |
| key | TEXT | NOT NULL | Metadata key |
| value | TEXT | NOT NULL | Metadata value |
| created_at | INTEGER | NOT NULL | Timestamp tạo |

**Use cases**:
- Lưu PDF page numbers
- OCR confidence scores
- Custom user notes
- API response metadata

### 5.3.3. Bảng DRAFTS

**Mục đích**: Auto-save drafts để không mất dữ liệu

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | TEXT | PRIMARY KEY | UUID của draft |
| content | TEXT | NOT NULL | Nội dung draft |
| input_type | TEXT | NOT NULL | TEXT/PDF/OCR |
| last_modified | INTEGER | NOT NULL | Timestamp sửa đổi cuối |
| source_uri | TEXT | NULL | URI của file (PDF/Image) |

**Business Rules**:
- Chỉ giữ 1 draft per input_type
- Auto-delete sau 7 ngày không sử dụng

### 5.3.4. Bảng USER_PREFERENCES

**Mục đích**: Key-value store cho user settings

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| key | TEXT | PRIMARY KEY | Setting key |
| value | TEXT | NOT NULL | Setting value (JSON) |
| updated_at | INTEGER | NOT NULL | Timestamp cập nhật |

**Common keys**:
- `theme`: "light"/"dark"/"system"
- `language`: "vi"/"en"
- `default_persona`: "DEFAULT"/"STUDENT"/etc
- `api_key`: Encrypted API key
- `onboarding_completed`: "true"/"false"

## 5.4. Room Database Implementation

### 5.4.1. Entity Classes

```kotlin
@Entity(tableName = "summaries")
@Parcelize
data class SummaryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),
    
    @ColumnInfo(name = "original_text")
    val originalText: String,
    
    @ColumnInfo(name = "summarized_text")
    val summarizedText: String,
    
    @ColumnInfo(name = "persona")
    val persona: String,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "word_count_original")
    val wordCountOriginal: Int,
    
    @ColumnInfo(name = "word_count_summary")
    val wordCountSummary: Int,
    
    @ColumnInfo(name = "language", defaultValue = "vi")
    val language: String = "vi",
    
    @ColumnInfo(name = "input_type", defaultValue = "TEXT")
    val inputType: String = "TEXT",
    
    @ColumnInfo(name = "source_info")
    val sourceInfo: String? = null,
    
    @ColumnInfo(name = "processing_time_ms")
    val processingTimeMs: Long? = null,
    
    @ColumnInfo(name = "is_favorite", defaultValue = "0")
    val isFavorite: Boolean = false,
    
    @ColumnInfo(name = "tags")
    val tags: String? = null // JSON array string
) : Parcelable

// NEW v1.0.3
@Entity(
    tableName = "ai_quality_metrics",
    foreignKeys = [
        ForeignKey(
            entity = SummaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["summary_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["summary_id"])]
)
data class AiQualityMetricsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),
    
    @ColumnInfo(name = "summary_id")
    val summaryId: String,
    
    @ColumnInfo(name = "coherence_score")
    val coherenceScore: Float,
    
    @ColumnInfo(name = "readability_level")
    val readabilityLevel: String,
    
    @ColumnInfo(name = "information_density")
    val informationDensity: Float,
    
    @ColumnInfo(name = "clarity_score")
    val clarityScore: Float,
    
    @ColumnInfo(name = "overall_confidence")
    val overallConfidence: Float,
    
    @ColumnInfo(name = "metrics_json")
    val metricsJson: String, // Full metrics as JSON
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

// NEW v1.0.3
@Entity(
    tableName = "api_usage_logs",
    indices = [Index(value = ["timestamp"])]
)
data class ApiUsageLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    
    @ColumnInfo(name = "endpoint")
    val endpoint: String,
    
    @ColumnInfo(name = "request_tokens")
    val requestTokens: Int,
    
    @ColumnInfo(name = "response_tokens")
    val responseTokens: Int,
    
    @ColumnInfo(name = "model")
    val model: String,
    
    @ColumnInfo(name = "success")
    val success: Boolean,
    
    @ColumnInfo(name = "error_message")
    val errorMessage: String? = null,
    
    @ColumnInfo(name = "processing_time_ms")
    val processingTimeMs: Long
)
```

### 5.4.2. Type Converters

```kotlin
@TypeConverters
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        return value?.let {
            Gson().fromJson(it, object : TypeToken<List<String>>() {}.type)
        }
    }
    
    @TypeConverter
    fun fromListString(list: List<String>?): String? {
        return list?.let { Gson().toJson(it) }
    }
}
```

### 5.4.3. DAO (Data Access Object)

```kotlin
@Dao
interface SummaryDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(summary: SummaryEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(summaries: List<SummaryEntity>)
    
    // READ
    @Query("SELECT * FROM summaries ORDER BY timestamp DESC")
    fun getAllSummaries(): Flow<List<SummaryEntity>>
    
    @Query("SELECT * FROM summaries WHERE id = :id LIMIT 1")
    suspend fun getSummaryById(id: String): SummaryEntity?
    
    @Query("SELECT * FROM summaries WHERE is_favorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteSummaries(): Flow<List<SummaryEntity>>
    
    @Query("""
        SELECT * FROM summaries 
        WHERE original_text LIKE '%' || :query || '%' 
        OR summarized_text LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun searchSummaries(query: String): Flow<List<SummaryEntity>>
    
    @Query("SELECT * FROM summaries WHERE persona = :persona ORDER BY timestamp DESC")
    fun getSummariesByPersona(persona: String): Flow<List<SummaryEntity>>
    
    // UPDATE
    @Update
    suspend fun update(summary: SummaryEntity)
    
    @Query("UPDATE summaries SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)
    
    // DELETE
    @Delete
    suspend fun delete(summary: SummaryEntity)
    
    @Query("DELETE FROM summaries")
    suspend fun deleteAll()
    
    @Query("DELETE FROM summaries WHERE timestamp < :timestamp")
    suspend fun deleteOldSummaries(timestamp: Long)
    
    // STATISTICS
    @Query("SELECT COUNT(*) FROM summaries")
    suspend fun getTotalCount(): Int
    
    @Query("SELECT SUM(word_count_original) FROM summaries")
    suspend fun getTotalWordsProcessed(): Long
    
    @Query("SELECT AVG(processing_time_ms) FROM summaries WHERE processing_time_ms IS NOT NULL")
    suspend fun getAverageProcessingTime(): Double?
}

// NEW v1.0.3
@Dao
interface AiQualityMetricsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metrics: AiQualityMetricsEntity)
    
    @Query("SELECT * FROM ai_quality_metrics WHERE summary_id = :summaryId")
    suspend fun getMetricsBySummaryId(summaryId: String): AiQualityMetricsEntity?
    
    @Query("SELECT AVG(coherence_score) FROM ai_quality_metrics")
    suspend fun getAverageCoherenceScore(): Float?
    
    @Query("SELECT AVG(overall_confidence) FROM ai_quality_metrics")
    suspend fun getAverageConfidenceScore(): Float?
    
    @Delete
    suspend fun delete(metrics: AiQualityMetricsEntity)
}

// NEW v1.0.3
@Dao
interface ApiUsageLogDao {
    @Insert
    suspend fun insert(log: ApiUsageLogEntity)
    
    @Query("SELECT * FROM api_usage_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 100): Flow<List<ApiUsageLogEntity>>
    
    @Query("""
        SELECT SUM(request_tokens + response_tokens) as total 
        FROM api_usage_logs 
        WHERE timestamp >= :startTime
    """)
    suspend fun getTotalTokensSince(startTime: Long): Long?
    
    @Query("""
        SELECT COUNT(*) FROM api_usage_logs 
        WHERE timestamp >= :startTime AND success = 1
    """)
    suspend fun getSuccessfulRequestsSince(startTime: Long): Int
    
    @Query("DELETE FROM api_usage_logs WHERE timestamp < :timestamp")
    suspend fun deleteOldLogs(timestamp: Long)
}
```

## 5.5. Database Migrations

### 5.5.1. Migration Strategy

```kotlin
object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add new columns for version 2
            database.execSQL("""
                ALTER TABLE summaries 
                ADD COLUMN input_type TEXT NOT NULL DEFAULT 'TEXT'
            """)
            
            database.execSQL("""
                ALTER TABLE summaries 
                ADD COLUMN source_info TEXT
            """)
        }
    }
    
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add metadata table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS summary_metadata (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    summary_id TEXT NOT NULL,
                    key TEXT NOT NULL,
                    value TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    FOREIGN KEY(summary_id) REFERENCES summaries(id) ON DELETE CASCADE
                )
            """)
            
            // Add index
            database.execSQL("""
                CREATE INDEX index_summary_metadata_summary_id 
                ON summary_metadata(summary_id)
            """)
        }
    }
}
```

### 5.5.2. Database Builder

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SumUpDatabase {
        return Room.databaseBuilder(
            context,
            SumUpDatabase::class.java,
            "sumup_database"
        )
        .addMigrations(
            DatabaseMigrations.MIGRATION_1_2,
            DatabaseMigrations.MIGRATION_2_3,
            DatabaseMigrations.MIGRATION_3_4 // NEW v1.0.3
        )
        .addCallback(DatabaseCallback())
        .fallbackToDestructiveMigration() // Only for development
        .build()
    }
    
    @Provides
    fun provideSummaryDao(database: SumUpDatabase): SummaryDao {
        return database.summaryDao()
    }
    
    // NEW v1.0.3
    @Provides
    fun provideAiQualityMetricsDao(database: SumUpDatabase): AiQualityMetricsDao {
        return database.aiQualityMetricsDao()
    }
    
    // NEW v1.0.3
    @Provides
    fun provideApiUsageLogDao(database: SumUpDatabase): ApiUsageLogDao {
        return database.apiUsageLogDao()
    }
}

// NEW v1.0.3 Migration
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add AI Quality Metrics table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS ai_quality_metrics (
                id TEXT PRIMARY KEY NOT NULL,
                summary_id TEXT NOT NULL,
                coherence_score REAL NOT NULL,
                readability_level TEXT NOT NULL,
                information_density REAL NOT NULL,
                clarity_score REAL NOT NULL,
                overall_confidence REAL NOT NULL,
                metrics_json TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                FOREIGN KEY(summary_id) REFERENCES summaries(id) ON DELETE CASCADE
            )
        """)
        
        database.execSQL("""
            CREATE INDEX index_ai_quality_metrics_summary_id 
            ON ai_quality_metrics(summary_id)
        """)
        
        // Add API Usage Logs table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS api_usage_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                timestamp INTEGER NOT NULL,
                endpoint TEXT NOT NULL,
                request_tokens INTEGER NOT NULL,
                response_tokens INTEGER NOT NULL,
                model TEXT NOT NULL,
                success INTEGER NOT NULL,
                error_message TEXT,
                processing_time_ms INTEGER NOT NULL
            )
        """)
        
        database.execSQL("""
            CREATE INDEX index_api_usage_logs_timestamp 
            ON api_usage_logs(timestamp)
        """)
    }
}
```

## 5.6. Database Optimization

### 5.6.1. Performance Tuning

1. **Indexes**: Tạo indexes cho các columns thường query
   ```sql
   CREATE INDEX idx_timestamp ON summaries(timestamp DESC);
   CREATE INDEX idx_persona ON summaries(persona);
   CREATE INDEX idx_input_type ON summaries(input_type);
   ```

2. **Query Optimization**:
   - Sử dụng LIMIT cho pagination
   - Avoid N+1 queries với proper JOINs
   - Use projections để chỉ lấy columns cần thiết

3. **Database Maintenance**:
   ```kotlin
   class DatabaseMaintenanceWorker : CoroutineWorker() {
       override suspend fun doWork(): Result {
           // Clean old summaries (>90 days)
           val cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(90)
           database.summaryDao().deleteOldSummaries(cutoffTime)
           
           // Vacuum database
           database.openHelper.writableDatabase.execSQL("VACUUM")
           
           return Result.success()
       }
   }
   ```

### 5.6.2. Storage Management

```kotlin
class StorageManager @Inject constructor(
    private val context: Context,
    private val database: SumUpDatabase
) {
    suspend fun getDatabaseSize(): Long {
        val dbFile = context.getDatabasePath("sumup_database")
        return dbFile.length()
    }
    
    suspend fun cleanupIfNeeded(maxSizeBytes: Long = 100 * 1024 * 1024) {
        if (getDatabaseSize() > maxSizeBytes) {
            // Delete oldest 20% of summaries
            val totalCount = database.summaryDao().getTotalCount()
            val toDelete = (totalCount * 0.2).toInt()
            
            database.withTransaction {
                // Delete oldest summaries
                database.openHelper.writableDatabase.execSQL("""
                    DELETE FROM summaries 
                    WHERE id IN (
                        SELECT id FROM summaries 
                        ORDER BY timestamp ASC 
                        LIMIT $toDelete
                    )
                """)
            }
        }
    }
}
```

## 5.7. Data Security

### 5.7.1. Encryption

```kotlin
// SQLCipher integration for encrypted database
dependencies {
    implementation "net.zetetic:android-database-sqlcipher:4.5.0"
}

// Usage
val passphrase = SQLiteDatabase.getBytes("your_secure_passphrase".toCharArray())
val factory = SupportFactory(passphrase)

Room.databaseBuilder(context, SumUpDatabase::class.java, "sumup_encrypted.db")
    .openHelperFactory(factory)
    .build()
```

### 5.7.2. Sensitive Data Handling

1. **API Keys**: Stored in EncryptedSharedPreferences, not in database
2. **User Data**: No PII stored in summaries
3. **Cleanup**: Secure deletion when user requests
4. **Backup**: Exclude from Android Auto Backup

## 5.8. Backup và Recovery

### 5.8.1. Auto Backup Configuration

```xml
<!-- backup_rules.xml -->
<full-backup-content>
    <include domain="database" path="sumup_database"/>
    <exclude domain="database" path="sumup_database-wal"/>
    <exclude domain="database" path="sumup_database-shm"/>
    <exclude domain="sharedpref" path="encrypted_prefs.xml"/>
</full-backup-content>
```

### 5.8.2. Manual Export/Import

```kotlin
class BackupManager @Inject constructor(
    private val database: SumUpDatabase,
    private val gson: Gson
) {
    suspend fun exportToJson(): String {
        val summaries = database.summaryDao().getAllSummaries().first()
        return gson.toJson(BackupData(
            version = DATABASE_VERSION,
            exportDate = System.currentTimeMillis(),
            summaries = summaries
        ))
    }
    
    suspend fun importFromJson(json: String): Result<Int> {
        return try {
            val backupData = gson.fromJson(json, BackupData::class.java)
            database.summaryDao().insertAll(backupData.summaries)
            Result.success(backupData.summaries.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## 5.9. Testing Database

### 5.9.1. Unit Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class SummaryDaoTest {
    private lateinit var database: SumUpDatabase
    private lateinit var summaryDao: SummaryDao
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            SumUpDatabase::class.java
        ).build()
        summaryDao = database.summaryDao()
    }
    
    @Test
    fun insertAndRetrieveSummary() = runTest {
        // Given
        val summary = createTestSummary()
        
        // When
        summaryDao.insert(summary)
        val retrieved = summaryDao.getSummaryById(summary.id)
        
        // Then
        assertThat(retrieved).isEqualTo(summary)
    }
    
    @Test
    fun searchSummaries() = runTest {
        // Given
        val summaries = listOf(
            createTestSummary(originalText = "Android development"),
            createTestSummary(originalText = "iOS programming"),
            createTestSummary(originalText = "Web development")
        )
        summaryDao.insertAll(summaries)
        
        // When
        val results = summaryDao.searchSummaries("development").first()
        
        // Then
        assertThat(results).hasSize(2)
        assertThat(results.map { it.originalText }).containsExactly(
            "Android development",
            "Web development"
        )
    }
    
    @After
    fun tearDown() {
        database.close()
    }
}
```

## 5.10. Tóm tắt chương

Chương này đã trình bày chi tiết thiết kế cơ sở dữ liệu cho ứng dụng SumUp:

1. **Room Database** được chọn làm giải pháp lưu trữ local
2. **6 bảng chính (v1.0.3 updated)**: 
   - Summaries (core data)
   - Summary_metadata (extensible metadata)
   - Drafts (auto-save functionality)
   - User_preferences (settings)
   - **AI_quality_metrics (NEW v1.0.3)** - Lưu trữ 20+ metrics phân tích chất lượng
   - **API_usage_logs (NEW v1.0.3)** - Theo dõi API usage cho dashboard
3. **Full-text search** được implement cho tìm kiếm nhanh
4. **Migration strategy** đảm bảo smooth updates (đã có Migration 3→4 cho v1.0.3)
5. **Performance optimization** với indexes và maintenance
6. **Security measures** bảo vệ dữ liệu người dùng
7. **Backup/Recovery** cho data safety
8. **Comprehensive testing** đảm bảo reliability

Với v1.0.3, database layer đã được nâng cấp đáng kể:
- **AI Quality Tracking**: Lưu trữ chi tiết metrics cho mỗi summary
- **Usage Analytics**: Theo dõi API usage patterns
- **Enhanced Performance**: Optimized indexes cho new tables
- **Future-proof**: Sẵn sàng cho analytics và ML features

Thiết kế này đảm bảo:
- **Scalability**: Có thể lưu trữ hàng nghìn summaries với metrics
- **Performance**: Query nhanh với proper indexing
- **Reliability**: Data integrity và cascading deletes
- **Security**: Encryption ready khi cần
- **Maintainability**: Clear structure và migrations
- **Analytics Ready**: Foundation cho business intelligence

Database layer này là foundation vững chắc cho các features của ứng dụng, đặc biệt là các tính năng AI quality và analytics mới trong v1.0.3.