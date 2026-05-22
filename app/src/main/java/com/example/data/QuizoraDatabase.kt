package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UserProgress::class, CBSEQuestion::class, CBSENotes::class],
    version = 1,
    exportSchema = false
)
abstract class QuizoraDatabase : RoomDatabase() {

    abstract fun quizoraDao(): QuizoraDao

    companion object {
        @Volatile
        private var INSTANCE: QuizoraDatabase? = null

        fun getInstance(context: Context): QuizoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizoraDatabase::class.java,
                    "quizora_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
