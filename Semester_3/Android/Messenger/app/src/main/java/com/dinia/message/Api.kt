package com.dinia.message

import com.squareup.moshi.Json
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

data class MessageEntity (
    @field:Json(name = "id") val id: Int?,
    @field:Json(name = "from") val from: String,
    @field:Json(name = "to") val to: String?,
    @field:Json(name = "data") val data: Data,
    @field:Json(name = "time") val time: Long?
)

data class Data (
    @field:Json(name = "Text") val Text: Content?,
    @field:Json(name = "Image") val Image: Content?
)

data class Content (
    @field:Json(name = "text") val text: String?,
    @field:Json(name = "link") val link: String?
)

interface Api {
    @GET("1ch")
    suspend fun getMessagesList(
        @Query("lastKnownId") id: Int,
        @Query("reverse") reverse: Boolean
    ): Response<List<MessageEntity>>

    @GET("/channels")
    suspend fun getChatList(): Response<List<String>>

    @GET("/channel/{channelName}")
    suspend fun getChatMessages(
        @Path("channelName") name: String,
        @Query("lastKnownId") id: Int
    ): Response<List<MessageEntity>>

    @GET("/inbox/DINIA")
    suspend fun getInboxMessages(
        @Query("lastKnownId") id: Int
    ): Response<List<MessageEntity>>

    @Headers("Content-Type: application/json")
    @POST("1ch")
    fun sendMessage(
        @Body message: MessageEntity
    ): Call<MessageEntity>

    @Multipart
    @POST("1ch")
    fun sendImage(
        @Part ("msg") msg : RequestBody,
        @Part file : MultipartBody.Part
    ): Call<ResponseBody>
}