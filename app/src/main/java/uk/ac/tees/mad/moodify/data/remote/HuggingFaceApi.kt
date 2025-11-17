package uk.ac.tees.mad.moodify.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import uk.ac.tees.mad.moodify.data.remote.models.SentimentResponse

interface HuggingFaceApi {
    @POST("models/distilbert/distilbert-base-uncased-finetuned-sst-2-english")
    suspend fun detectSentiment(
        @Header("Authorization") auth: String,
        @Body request: Map<String, String>
    ): List<List<SentimentResponse>>
}