package uk.ac.tees.mad.moodify.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MoodResultCard(mood: String) {
    val emoji = when (mood.lowercase()) {
        "positive" -> "😊"
        "negative" -> "😔"
        "neutral" -> "😐"
        else -> "🧠"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Detected Mood: $mood $emoji",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = when (mood.lowercase()) {
                    "positive" -> "Here's a playlist to boost your mood!"
                    "negative" -> "Try a relaxation track and deep breathing."
                    "neutral" -> "Keep journaling to find clarity."
                    else -> ""
                },
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}


