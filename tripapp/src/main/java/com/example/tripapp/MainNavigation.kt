package com.example.tripapp

import androidx.navigation.NavController

// navigation 을 위한 각 composable 의 이름을 상수 변수로 선언하여 사용
object TripDestination {
    const val HOME_ROUTE = "home"
}

// 화면 전환 시 호출할 함수
class MainNavigation(private val navController: NavController) {
    fun navigate(destination: String) {
        navController.navigate(destination)
    }
    fun pop() {
        navController.popBackStack()
    }
}