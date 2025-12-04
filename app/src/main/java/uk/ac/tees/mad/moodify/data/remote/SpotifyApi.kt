package uk.ac.tees.mad.moodify.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import uk.ac.tees.mad.moodify.data.remote.models.SpotifySearchResponse

interface SpotifyApi {
    @GET("v1/search")
    suspend fun searchPlaylists(
        @Header("Authorization") auth: String = "Bearer BQAVYND-nPsFwyfpoU8PQrOr1FRIHx8YV1KiN9IvAGu_57BYWjCo60wKRvxwK4Ab2VQX-akXNZPEhSd7JvDz2PS_wKEDvqTFUcuN4xRVSzzILxUpRhcKpHzVAIGMW01PCqeSAMIWtBsM4za_e8nTAyluKKaNrMtEQNUcJi5IuUqgDR0VUbxH6uGJHixFRDLli6VRfyIbFxZZzlwlmiM3tk22vQX1zKaFnmPH4IJKcJSpERhmbFMFhficQGQvB8aZk5V3ZTKR48eYHwV0M_FC-wdJK0zRjRqaDBCx4jZEqMIre2U04rEITE2BwWACpG0on_rQ",
        @Query("q") query: String,
        @Query("type") type: String = "playlist",
        @Query("limit") limit: Int = 1
    ): SpotifySearchResponse
}