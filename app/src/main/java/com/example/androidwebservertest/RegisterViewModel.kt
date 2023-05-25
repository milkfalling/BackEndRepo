package com.example.androidwebservertest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import java.net.PasswordAuthentication

class RegisterViewModel : ViewModel() {
    val userName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val password: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val confirmPassword: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val nickName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val result: MutableLiveData<String> by lazy { MutableLiveData<String>() }

}



