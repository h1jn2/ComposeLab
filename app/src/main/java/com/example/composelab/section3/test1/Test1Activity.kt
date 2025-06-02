package com.example.composelab.section3.test1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composelab.section3.test1.ui.theme.ComposeLabTheme

class Test1Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeLabTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting2(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// 개발자에 의해 선언되는 컴포저블도 modifier 를 매개 변수로 선언해 놓는 것이 좋음
@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        val myModifier = Modifier
            .border(width = 10.dp, color = Color.Red)
            .padding(all = 30.dp)
            .background(Color.Yellow)

        val myModifier2 = Modifier.background(Color.Blue)

        // then 에 의해 modifier 조합하다보면 같은 설정 중복 가능성이 있지만 덮어쓰기 개념이기 때문에 문제 없음
        Text(
            text = "Hello World",
            fontSize = 30.sp,
            modifier = modifier
                .then(myModifier)
                .then(myModifier2)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // compose 에서 activity 의 context 정보를 이용
        val context = LocalContext.current

        Card(
            modifier = Modifier
                .background(Color.Blue)
                .size(200.dp, 100.dp)
                .clickable {
                    Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
                }
                .padding(vertical = 24.dp, horizontal = 24.dp))
        {
            Text("Hello")
        }

    }
}