package ir.aris.question.data.network

import ir.aris.question.data.model.AskItem
import retrofit2.Response
import retrofit2.http.GET

interface InterfaceApi {

    @GET("master/world.json")
    suspend fun getAllQuestion():Response<List<AskItem>>

}