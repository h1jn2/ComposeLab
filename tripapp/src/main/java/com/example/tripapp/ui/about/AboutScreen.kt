package com.example.tripapp.ui.about

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.example.tripapp.ui.about.content.LandscapeContent
import com.example.tripapp.ui.about.content.PortraitContent

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    // 화면 회전의 상태
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }

    // view 로 개발 시 layout 폴더 명에 등록만 하면 회전을 코드에서 감지할 필요가 없음
    // compose 로 개발 시 화면 회전에 따른 적절한 composable 출력

    // 화면 회전을 포함한 현 기기의 다양한 현재 상태
    val configuration = LocalConfiguration.current

    // 이 composable 이 화면에 나올 때 최초로 한 번 실행시켜야 하는 업무 (network, dbms)
    // 특정 상황에서만 다시 업무가 실행되어야 할 때 configuration 이 변경 될 때 마다
    // { } 부분이 compose 최초 초기화 될 때 한 번 실행, 그 이후 key1 부분이 변경될 때마다 실행

    // true 로 지정했으므로 절대 변경은 없고 최초에 한번만 실행
//    LaunchedEffect(true) { }

    // 최초에 한번 실행되고 myFlag 값이 변경될 때 마다 실행
    // LaunchedEffect() 에 지정한 값이 변경되는 순간마다
//    var myFlag = true
//    LaunchedEffect(myFlag) {}

    LaunchedEffect(configuration) {
        // orientation 이라는 상태 값을 직접 변경
        // 상태 값을 flow 발행해도 됨
        snapshotFlow { configuration.orientation }
            .collect {orientation = it}
    }

    Scaffold { innerPadding ->
        when(orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                LandscapeContent(modifier = Modifier.padding(innerPadding))
            }
            else -> {
                PortraitContent(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}