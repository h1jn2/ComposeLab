package com.example.tripapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.tripapp.ui.about.AboutScreen
import com.example.tripapp.ui.home.HomeScreen
import com.example.tripapp.ui.myinfo.MyInfoScreen
import com.example.tripapp.ui.myinfo.MyInfoViewModel

// 화면 단위 composable 에서 이용할 viewModel 을 준비해서 매개 변수로 전달
// 하나의 composable 만을 위한 viewModel 일 수도 있고
// 여러 composable 에 공유되는 viewModel 일 수도 있음
// navhost 의 stack 정보에서 특정 composable 의 부모가 이용하던 viewModel 이 있는 지를 판단
// 있으면 공유하고 없으면 새로 만듬

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavHostController): T {
    // 현재의 라우팅 정보 - composable 이 stack 에 쌓여 있는 정보
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)   // stack 정보에서 뒤지고 있으면 있는 것을 이용, 없으면 새로 생성
}

// MainActivity 에 의해 출력될 composable 을 등록하여 stack 으로 관리
@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    navActions: MainNavigation = MainNavigation(navController)
) {
    // composable 을 stack 정보로 유지
    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = modifier
    ) {
        // 이 곳에서 composable 로 각 화면을 등록해도 가능하긴 하지만
        // 화면이 많다면 각 composable 을 구조화 시켜서 등록 시킬 수도 있음
        navigation(startDestination = TripDestination.HOME_ROUTE, route = "main") {
            composable(TripDestination.HOME_ROUTE) {
                val viewModel = it.sharedViewModel<MyInfoViewModel>(navController)
                HomeScreen(
                    navigate = { navActions.navigate(it) },
                    viewModel = viewModel
                )
            }
            composable(TripDestination.ABOUT_ROUTE) {
                AboutScreen()
            }
            composable(TripDestination.MYINFO_ROUTE) {
                val viewModel = it.sharedViewModel<MyInfoViewModel>(navController)
                MyInfoScreen(
                    pop = { navActions.pop() },
                    viewModel = viewModel
                )
            }
        }
    }
}