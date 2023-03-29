package ir.aris.question.repository

import ir.aris.question.data.network.InterfaceApi
import javax.inject.Inject

class QuestionRepository @Inject constructor(
    private val api: InterfaceApi,
) {

    suspend fun getAllQuestion() = api.getAllQuestion()

}