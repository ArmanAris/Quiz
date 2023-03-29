package ir.aris.question.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.aris.question.data.model.AskItem
import ir.aris.question.repository.QuestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ViewModelQuestion @Inject constructor(
    private val repository: QuestionRepository,
) : ViewModel() {

    val questionList = MutableStateFlow<List<AskItem>>(emptyList())
    val questionListError = MutableStateFlow<String?>("")
    val loading = MutableStateFlow<Boolean>(true)

    fun getAllPostsRequest() {
        loading.value = true

        viewModelScope.launch(Dispatchers.IO) {

            val response = repository.getAllQuestion()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let { allQuestion ->

                        questionList.emit(allQuestion)
                        questionListError.emit(null)
                        loading.emit(false)

                    }
                } else {
                    questionListError.emit(response.message())
                    loading.emit(false)
                }
            }
        }
    }
}
