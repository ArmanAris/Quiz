package ir.aris.question

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ir.aris.question.data.model.AskItem
import ir.aris.question.ui.theme.AppColors
import ir.aris.question.ui.theme.QuestionTheme
import ir.aris.question.viewmodel.ViewModelQuestion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuestionTheme {

                if (startTime.value) {
                    lifecycleScope.launch {
                        repeat(7200) {
                            delay(1000)
                            second.value--
                        }
                    }
                }

            }
            Home()
        }
    }
}

var number = mutableStateOf(0)
var reward = mutableStateOf(0)
var second = mutableStateOf(7200)
var startTime = mutableStateOf(false)
var isOnOrOff = mutableStateOf(true)


@Composable
fun Home(
    viewModel: ViewModelQuestion = hiltViewModel(),
) {

    var questionList by remember { mutableStateOf<List<AskItem>>(emptyList()) }
    var choicesList by remember { mutableStateOf<List<String>>(emptyList()) }
    var answer by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    val numberItem by remember { number }
    var question by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        viewModel.getAllPostsRequest()
    }


    val questionResult by viewModel.questionList.collectAsState()
    questionList = questionResult


    try {
        question = questionList[numberItem].question
        choicesList = questionList[numberItem].choices
        answer = questionList[numberItem].answer
        loading = false
        startTime.value = true
    } catch (e: Exception) {
        Log.e("7171", e.message.toString())
    }

    if (loading) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .background(Color(0xff262c49)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(Modifier.size(70.dp))
        }

    } else {
        Box(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize()) {
                Column(modifier = Modifier
                    .fillMaxSize()) {
                    Show(numberItem = numberItem + 1,
                        questionList = questionList.size,
                        question = question,
                        radioList = choicesList,
                        answer = answer)
                }
            }
            Box(Modifier.fillMaxSize(0.90f), contentAlignment = Alignment.BottomEnd) {
                Timer()
            }
        }
    }

}

@Composable
fun Show(
    numberItem: Int,
    questionList: Int,
    question: String,
    radioList: List<String>,
    answer: String,
) {
    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(4.dp),
        color = Color(0xff262c49)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {

            ShowProgress(reward.value)
            QuestionTracker(numberItem, questionList)
            DrawDottedLine()
            Text(text = question,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start)

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
                horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
                CheckTest(radioList, answer)

            }
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    /*
                    // back
                    Button(onClick = {
                         isOnOrOff.value = false
                         number.value--
                         if (number.value < 0) {
                             number.value = questionList - 1

                         }
                     }) {
                         Text(text = "Back")
                     }
                     */
                    /*
                    // question number
                    Text(
                        modifier = Modifier.padding(horizontal = 25.dp),
                        text = numberItem.toString(), color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    */
                    Button(onClick = {
                        isOnOrOff.value = true
                        number.value++
                        if (number.value > questionList - 1) {
                            number.value = 0
                        }
                    },
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        Text(text = "Next")
                    }
                }
            }

        }

    }
}

@Composable
fun ShowProgress(score: Int) {
    val progressFactor by remember(score) { mutableStateOf(score * 0.005f) }

    Row(modifier = Modifier
        .padding(3.dp)
        .fillMaxWidth()
        .height(45.dp)
        .border(width = 4.dp,
            color = Color(0xff545a75),
            shape = RoundedCornerShape(34.dp))
        .clip(RoundedCornerShape(50))
        .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically) {
        Box() {
            Button(
                contentPadding = PaddingValues(1.dp),
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth(progressFactor)
                    .background(brush = Brush.linearGradient(listOf(Color(0xFFF95075),
                        Color(0xFFBE6BE5)))),
                enabled = false,
                elevation = null,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    disabledBackgroundColor = Color.Transparent
                )) {}

            Text(text = (score * 10).toString(),
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(23.dp))
                    .fillMaxHeight(0.87f)
                    .fillMaxWidth()
                    .padding(start = 30.dp, top = 12.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start)
        }

    }
}

@Composable
fun QuestionTracker(counter: Int = 10, outOf: Int = 100) {
    Text(text = buildAnnotatedString {
        withStyle(style = ParagraphStyle(textIndent = TextIndent.None)) {
            withStyle(style = SpanStyle(color = AppColors.mLightGray,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp)) {
                append("Question $counter/")
                withStyle(style = SpanStyle(color = AppColors.mLightGray,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp)) {
                    append("$outOf")
                }

            }
        }
    },
        modifier = Modifier.padding(20.dp))
}

@Composable
fun DrawDottedLine() {
    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp),
    ) {
        drawLine(color = AppColors.mLightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, y = 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )
    }
}

@Composable
fun CheckTest(radioList: List<String>, answer: String) {
    val (selectedOptions, onOptionSelected) = remember { mutableStateOf("") }

    Column() {
        radioList.forEach { text ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .border(4.dp, color = Color.White, shape = RoundedCornerShape(15.dp))
                    .background(
                        if (text == selectedOptions) {
                            if (text == answer) Color.Green else Color.Red
                        } else {
                            Color.Transparent
                        }
                    )
                    .selectable(selected = (text == selectedOptions),
                        onClick = {
                            if (isOnOrOff.value) {
                                if (text == answer) reward.value++
                            }
                            isOnOrOff.value = false
                            onOptionSelected(text)
                        })) {
                RadioButton(selected = (text == selectedOptions),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.White,
                        unselectedColor = Color.White,
                    ),
                    onClick = {
                        if (isOnOrOff.value) {
                            if (text == answer) reward.value++
                        }
                        isOnOrOff.value = false
                        onOptionSelected(text)
                    })
                Text(
                    text = text, color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

        }
    }

}

@Composable
fun Timer() {
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = "${(second.value) / 3600} : ${((second.value) % 3600) / 60} : ${(((second.value) % 3600) % 60)}",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }

}

