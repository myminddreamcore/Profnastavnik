import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val BgGradientStart = Color(0xFF388EAB)
val BgGradientEnd = Color(0xFFD1E4E9)
val CardBg = Color(0xFFFFFFFF).copy(alpha = 0.2f)
val SearchCardColor = Color(0xFF4FA9D5)
val ProfileCardColor = Color(0xFF5038E4)

@Composable
fun MainScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToReg: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(BgGradientStart, BgGradientEnd)))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(60.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ПРОФНаставник", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                TextButton(onClick = onNavigateToLogin) {
                    Text("Войти", color = Color.White, fontSize = 14.sp)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                    .background(CardBg, RoundedCornerShape(32.dp))
                    .padding(vertical = 40.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(


                            text = "Найди\nстажировку\nили стажера",

                            color = Color.White,

                            fontSize = 32.sp,

                            lineHeight = 38.sp,

                            textAlign = TextAlign.Center,

                            fontWeight = FontWeight.Medium,

                            modifier = Modifier.padding(bottom = 32.dp)

                        )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SelectionCard(
                            title = "найти стажировку",
                            icon = Icons.Default.Search,
                            backgroundColor = SearchCardColor,
                            modifier = Modifier.weight(1f).aspectRatio(0.8f),
                            onClick = onNavigateToReg
                        )
                        SelectionCard(
                            title = "найти стажера",
                            icon = Icons.Default.Person,
                            backgroundColor = ProfileCardColor,
                            modifier = Modifier.weight(1f).aspectRatio(0.8f),
                            onClick = onNavigateToReg
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun SelectionCard(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp
            )
        }
    }
}