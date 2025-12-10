package uk.ac.tees.mad.moodify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "mood_entries")
data class MoodEntries(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mood : String = "",
    val timestamp: Long = 0,
    val user_id : String = ""
)
