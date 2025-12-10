package uk.ac.tees.mad.moodify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {

    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getMoodEntries(): Flow<List<MoodEntries>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(moodEntry: List<MoodEntries>)

    @Query("DELETE FROM mood_entries")
    suspend fun deleteAllMoodEntries()




}