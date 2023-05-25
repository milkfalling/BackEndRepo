package com.example.androidwebservertest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditViewModel : ViewModel() {
    val userName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val newPassword: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val confirmPassword: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val nickName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val result: MutableLiveData<String> by lazy { MutableLiveData<String>() }

}