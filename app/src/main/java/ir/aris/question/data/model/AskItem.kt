package ir.aris.question.data.model

data class AskItem(
    val answer: String,
    val category: String,
    val choices: List<String>,
    val question: String
)