package com.example.sumup.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.example.sumup.data.local.converter.StringListConverter
import com.example.sumup.data.local.dao.SummaryDao
import com.example.sumup.data.local.entity.SummaryEntity

@Database(
    entities = [SummaryEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class SumUpDatabase : RoomDatabase() {
    abstract fun summaryDao(): SummaryDao

    companion object {
        @Volatile
        private var INSTANCE: SumUpDatabase? = null

        // Migration from version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add persona column with default value
                database.execSQL("ALTER TABLE summaries ADD COLUMN persona TEXT NOT NULL DEFAULT 'GENERAL'")
            }
        }

        // Migration from version 2 to 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns for enhanced summary features
                database.execSQL("ALTER TABLE summaries ADD COLUMN confidence REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE summaries ADD COLUMN briefOverview TEXT")
                database.execSQL("ALTER TABLE summaries ADD COLUMN detailedSummary TEXT")
                database.execSQL("ALTER TABLE summaries ADD COLUMN keyInsights TEXT")
                database.execSQL("ALTER TABLE summaries ADD COLUMN actionItems TEXT")
                database.execSQL("ALTER TABLE summaries ADD COLUMN keywords TEXT")
            }
        }

        fun getDatabase(context: Context): SumUpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SumUpDatabase::class.java,
                    "sumup_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    // Only use destructive migration as last resort for corrupted databases
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}