package uk.ac.tees.mad.moodify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MoodEntries::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun moodDao(): MoodDao
}