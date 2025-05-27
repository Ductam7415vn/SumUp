package com.example.sumup.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.sumup.data.local.converter.StringListConverter
import com.example.sumup.data.local.dao.SummaryDao
import com.example.sumup.data.local.entity.SummaryEntity

@Database(
    entities = [SummaryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class SumUpDatabase : RoomDatabase() {
    abstract fun summaryDao(): SummaryDao

    companion object {
        @Volatile
        private var INSTANCE: SumUpDatabase? = null

        fun getDatabase(context: Context): SumUpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SumUpDatabase::class.java,
                    "sumup_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}