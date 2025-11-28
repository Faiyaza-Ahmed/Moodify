package uk.ac.tees.mad.moodify.ui.result


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
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    suspend fun fetchSpotifyPlaylist(mood: String): String = withContext(Dispatchers.IO) {
        try {
            val query = when (mood.lowercase()) {
                "positive" -> "happy mood"
                "negative" -> "calm meditation"
                "neutral" -> "focus work"
                else -> "relaxation"
            }

            val url = URL("https://api.spotify.com/v1/search?q=$query&type=playlist&limit=1")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer YOUR_SPOTIFY_ACCESS_TOKEN")

            val response = connection.inputStream.bufferedReader().readText()
            val playlist = JSONObject(response)
                .getJSONObject("playlists")
                .getJSONArray("items")
                .getJSONObject(0)
                .getString("external_urls")
            val spotifyUrl = JSONObject(playlist).getString("spotify")

            spotifyUrl
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
            firestore.collection("mood_entries").add(entry)
        }
    }
}
