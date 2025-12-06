package uk.ac.tees.mad.moodify.ui.result


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import uk.ac.tees.mad.moodify.data.remote.SpotifyApi
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val spotifyApi: SpotifyApi
) : ViewModel() {

    suspend fun fetchSpotifyPlaylist(mood: String): String = withContext(Dispatchers.IO) {
        return@withContext try {
            when (mood.lowercase()) {
                "positive" -> {
                    "https://open.spotify.com/playlist/37i9dQZF1DXdPec7aLTmlC"
                }
                "negative" -> {
                    "https://open.spotify.com/playlist/37i9dQZF1DX3rxVfibe1L0"
                }
                "neutral" -> {
                    "https://open.spotify.com/playlist/37i9dQZF1DX4sWSpwq3LiO"
                }
                "angry" -> {
                    "https://open.spotify.com/playlist/37i9dQZF1DWZIOAPKUdaKS"
                }
                else -> {
                    "https://open.spotify.com/playlist/37i9dQZF1DWU0ScTcjJBdj"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "https://open.spotify.com/playlist/37i9dQZF1DX3rxVfibe1L0"
        }
    }


    fun suggestActivity(mood: String): String {
        return when (mood.lowercase()) {
            "positive" -> "Keep your energy high — go for a short walk!"
            "negative" -> "Try 5-minute breathing or write another reflection."
            "neutral" -> "Do a quick gratitude journaling session."
            else -> "Take a mindful pause and relax."
        }
    }

    fun saveMoodEntry(mood: String) {
        val userId = auth.currentUser?.uid ?: return
        val entry = mapOf(
            "mood" to mood,
            "timestamp" to System.currentTimeMillis(),
            "userId" to userId
        )

        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("users").document(userId)
                .collection("moodEntries")
                .add(entry)
                .addOnSuccessListener {
                    Log.d("ResultViewModel", "Mood entry saved successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("ResultViewModel", "Error saving mood entry", e)
                }
        }
    }
}
