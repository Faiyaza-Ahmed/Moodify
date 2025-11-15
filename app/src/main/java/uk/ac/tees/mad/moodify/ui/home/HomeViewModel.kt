package uk.ac.tees.mad.moodify.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    suspend fun detectSentiment(text: String): String = withContext(Dispatchers.IO) {
        val apiUrl = URL("https://api-inference.huggingface.co/models/distilbert/distilbert-base-uncased-finetuned-sst-2-english")
        val connection = apiUrl.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer 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")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val body = """{"inputs": "$text"}"""
            connection.outputStream.use { it.write(body.toByteArray()) }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val jsonArray = JSONArray(response)
                // Find the label with the highest score
                var highestScore = 0.0
                var selectedLabel = "neutral"
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val label = jsonObject.getString("label")
                    val score = jsonObject.getDouble("score")
                    if (score > highestScore) {
                        highestScore = score
                        selectedLabel = label.lowercase() // Convert to lowercase for UI consistency
                    }
                }
                selectedLabel
            } else {
                // Log error for debugging
                val error = connection.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
                println("API Error: ${connection.responseCode} - $error")
                "neutral"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "neutral"
        } finally {
            connection.disconnect()
        }
    }
}
