package uk.ac.tees.mad.moodify.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import uk.ac.tees.mad.moodify.data.remote.HuggingFaceApi
import java.net.HttpURLConnection
import java.net.URL

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: HuggingFaceApi
) : ViewModel() {

    suspend fun detectSentiment(text: String): String = withContext(Dispatchers.IO) {
        try {
            val response = api.detectSentiment(
                auth = "Bearer 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",
                request = mapOf("inputs" to text)
            )
            response.firstOrNull()?.maxByOrNull { it.score }?.label?.lowercase() ?: "neutral"
        } catch (e: Exception) {
            e.printStackTrace()
            "neutral"
        }
    }
}
