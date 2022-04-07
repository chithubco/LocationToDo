package com.echithub.locationtodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.echithub.locationtodo.utils.FireBaseUserLiverData

class LoginViewModel:ViewModel() {

    enum class AuthenticationState{
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FireBaseUserLiverData().map { user ->
        if (user != null){
            AuthenticationState.AUTHENTICATED
        }else{
            AuthenticationState.UNAUTHENTICATED
        }
    }
}