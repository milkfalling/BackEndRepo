package com.example.androidwebservertest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.androiddemo.NetworkThread
import com.example.androidwebservertest.databinding.FragmentRegisterBinding
import java.util.concurrent.FutureTask
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.io.OutputStreamWriter
import java.net.CookieHandler
import java.net.CookieManager
import java.net.HttpURLConnection
import java.net.URL


class Register : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: RegisterViewModel by viewModels()
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.viewModel = RegisterViewModel()
        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            btSubmit.setOnClickListener {
                val userName = viewModel?.userName?.value
                val password = viewModel?.password?.value
                val confirmPassword = viewModel?.confirmPassword?.value
                val nickName = viewModel?.nickName?.value
                with(check()) {
                    when (userNameIsInvalid(userName)) {
                        "不可為空" -> etUsername.error="不可為空"
                        "不可少於5" -> etUsername.error= "不可少於5"
                        "不可多於50" -> etUsername.error="不可多於50"
                        "只能為英數字" -> etUsername.error="只能為英數字"
                    }
                    when (passwordIsInvalid(password)) {
                        "不可為空" -> etPassword.error="不可為空"
                        "不可少於6" -> etPassword.error= "不可少於6"
                        "不可多於12" -> etPassword.error="不可多於12"
                        "只能為英數字" -> etPassword.error="只能為英數字"
                    }
                    when (nickNameIsInvalid(nickName)) {
                        "不可為空" -> etNickname.error="不可為空"
                        "不可多於20" -> etNickname.error="不可多於20"
                    }
                    if(confirmPassword != password){
                        etConfirmPassword.error = "與密碼不一致"
                        return@setOnClickListener
                    }
                }

                //這邊開始寫請求本體
                data class MyData(
                    val username: String?,
                    val password: String?,
                    val nickname: String?
                )
                val myData = MyData(userName, password, nickName)
                val gson = Gson()
                val json = gson.toJson(myData)

                registerTask(json) { JsonElement ->
                    tvResult.text = JsonElement
                }
                btLoginRegister.setOnClickListener {
                    Navigation.findNavController(it).navigate(R.id.login)
                }
            }
        }
    }

    fun register(json: String): JsonObject? {
        val url = "http://10.0.2.2:8080/javaweb-exercise-01-2/member"
        var conn: HttpURLConnection? = null
        try {
            if (CookieHandler.getDefault() == null) {
                CookieHandler.setDefault(CookieManager())//紀錄cookie的值(實例化CookieManager取得資料)
            }
            conn = URL(url).openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "POST"
            conn.useCaches = false
            conn.setRequestProperty(
                "Content-Type",
                "application/json"
            ) // 设置请求头中的 Content-Type 为 application/json

            conn.outputStream.writer().use { it.write(json) } // 将 JSON 写入请求正文中

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = conn.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                return Gson().fromJson(response, JsonObject::class.java) // 解析服务器响应的 JSON 数据为 JsonArray 对象
            } else {
                Log.e("request2", "Response code: ${conn.responseCode}")
            }
        } catch (e: Exception) {
            conn?.disconnect()
            Log.e("main", e.toString())
        } finally {
            conn?.disconnect() // 在 finally 块中确保连接被断开
        }
        return null
    }

    fun registerTask(json: String, callback: (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = register(json)
            val jsonObject: JsonObject? = result
            val jsonElement: JsonElement? = jsonObject?.get("message")
            withContext(Dispatchers.Main) {
                callback(jsonElement.toString())
            }
        }
    }
}



