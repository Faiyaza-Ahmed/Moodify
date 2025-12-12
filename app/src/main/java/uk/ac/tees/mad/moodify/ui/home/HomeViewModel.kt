package uk.ac.tees.mad.moodify.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import uk.ac.tees.mad.moodify.data.remote.Gemini
import uk.ac.tees.mad.moodify.data.remote.HuggingFaceApi
import uk.ac.tees.mad.moodify.data.remote.models.SentimentResponse
import java.net.HttpURLConnection
import java.net.URL

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: HuggingFaceApi,
    private val gemini: Gemini
) : ViewModel() {

    private val gson = Gson()
    suspend fun detectSentiment(text: String): String = withContext(Dispatchers.IO) {
        try {
            val rawJson = gemini.detectMood(text)

            val result = gson.fromJson(rawJson, SentimentResponse::class.java)

            result.label.lowercase()
        } catch (e: Exception) {
            e.printStackTrace()
            "neutral"
        }
    }
}
