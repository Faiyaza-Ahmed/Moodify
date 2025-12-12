package uk.ac.tees.mad.moodify.data.remote

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject

class Gemini @Inject constructor() {

    suspend fun detectMood(mood : String): String {
        val apiKey = "AIzaSyB2IvsmpaVzIzqQ94IIiktm0-sWOsnNHJo"

        val model = GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey,

        )

        val res = model.generateContent(sentimentPrompt(mood))
        Log.d("Gemini", res.text.toString())
        return res.text ?: error("Empty Gemini response")
    }
    private fun sentimentPrompt(text: String): String {
        return """
You are a sentiment analysis engine.

Classify the sentiment of the given text strictly as one of:
positive, negative, neutral.

Respond ONLY in valid JSON.
NO explanations.
NO markdown.
NO extra text.

JSON format:
{
  "label": "positive | negative | neutral",
  "score": number between 0 and 1
}

Text:
"$text"
""".trimIndent()
    }

}

