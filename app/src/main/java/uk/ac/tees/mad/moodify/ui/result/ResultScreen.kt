package uk.ac.tees.mad.moodify.ui.result

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ResultScreen(mood: String?) {
    Text(mood!!)
}

@Preview
@Composable
fun previewResult (){
    ResultScreen("Sad")
}