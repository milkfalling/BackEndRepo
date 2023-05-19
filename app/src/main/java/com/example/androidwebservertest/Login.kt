package com.example.androidwebservertest

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Selection.selectAll
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.androidwebservertest.databinding.FragmentLoginBinding
import com.example.androidwebservertest.databinding.FragmentRegisterBinding
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.net.CookieHandler
import java.net.CookieManager
import java.net.HttpURLConnection
import java.net.URL

class Login : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: LoginViewModel by viewModels()
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = LoginViewModel()
        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            btRegisterLogin.setOnClickListener {
                Navigation.findNavController(it).navigate(R.id.register)
            }
            btLoginLogin.setOnClickListener {
                val username = viewModel?.userName?.value
                val password = viewModel?.password?.value
                if (loginTask(username!!, password!!) == true) {
                    val bundle = Bundle()
                    bundle.putString("username",username)
                    Navigation.findNavController(it).navigate(R.id.edit,bundle)
                } else {
                    tvResultLogin.text = "確認一下帳密好嗎??????????????????"
                }
            }
        }
    }

    fun login(username: String, password: String): Boolean? {
        val url = "http://10.0.2.2:8080/javaweb-exercise-01-2/member/$username/$password"
        var conn: HttpURLConnection? = null
        try {
            if (CookieHandler.getDefault() == null) {
                CookieHandler.setDefault(CookieManager())//紀錄cookie的值(實例化CookieManager取得資料)
            }
            conn = URL(url).openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "GET"
            conn.useCaches = false
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                conn.inputStream.reader().use {
                    return Gson().fromJson(it, Boolean::class.java)
                }
            }
        } catch (e: Exception) {
            conn?.disconnect()
            Log.e("main", e.toString())
        }
        return null
    }

    fun loginTask(username: String, password: String) = runBlocking {
        val result = async(Dispatchers.IO) {
            login(username, password)
        }.await()
        return@runBlocking result
    }

}