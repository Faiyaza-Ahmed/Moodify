package uk.ac.tees.mad.moodify.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import uk.ac.tees.mad.moodify.data.remote.models.SpotifySearchResponse

interface SpotifyApi {
    @GET("v1/search")
    suspend fun searchPlaylists(
        @Query("q") query: String,
        @Query("type") type: String = "playlist",
        @Query("limit") limit: Int = 1
    ): SpotifySearchResponse
}