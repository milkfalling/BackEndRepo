package com.example.androidwebservertest

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.androidwebservertest.databinding.FragmentEditBinding
import com.example.androidwebservertest.databinding.FragmentLoginBinding
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.net.CookieHandler
import java.net.CookieManager
import java.net.HttpURLConnection
import java.net.URL

class Edit : Fragment() {
    private lateinit var binding: FragmentEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: LoginViewModel by viewModels()
        binding = FragmentEditBinding.inflate(inflater, container, false)
        binding.viewModel = EditViewModel()
        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            val username = arguments?.getString("username")
            viewModel?.userName?.value = username
            btEdit.setOnClickListener {
                val password = viewModel?.newPassword?.value
                val confirmpassword = viewModel?.confirmPassword?.value
                val nickname = viewModel?.nickName?.value
                //取得前面username的值
                if (confirmpassword == password) {
                    data class MyData(
                        val username: String?,
                        val password: String?,
                        val nickname: String?
                    )

                    val myData = MyData(username, password, nickname)
                    val gson = Gson()
                    val json = gson.toJson(myData)
                    editTask(json) { JsonElement ->
                        if (JsonElement == "1") {
                            tvResultEdit.text = "編輯成功!"
                        }

                    }
                } else {
                    tvResultEdit.text = "確認密碼跟密碼不相符"
                    return@setOnClickListener
                }
            }
            btLogout.setOnClickListener {
                android.app.AlertDialog.Builder(view.context)
                    .setTitle("注意")
                    .setIcon(R.drawable.alert)
                    .setMessage("感謝你的注意，登出?")
                    .setPositiveButton("好") { dialog, _ ->
                        logoutTask()
                        dialog.dismiss()
                    }
                    .setNegativeButton("不好", null)
                    .setNeutralButton("取消", null)
                    .setCancelable(false)
                    .show()
            }
        }
    }

    fun edit(json: String): Int? {
        val url = "http://10.0.2.2:8080/javaweb-exercise-01-2/member"
        var conn: HttpURLConnection? = null
        try {
            if (CookieHandler.getDefault() == null) {
                CookieHandler.setDefault(CookieManager())//紀錄cookie的值(實例化CookieManager取得資料)
            }
            conn = URL(url).openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "PUT"
            conn.useCaches = false
            conn.setRequestProperty(
                "Content-Type",
                "application/json"
            ) // 设置请求头中的 Content-Type 为 application/json

            conn.outputStream.writer().use { it.write(json) } // 将 JSON 写入请求正文中

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = conn.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                return Gson().fromJson(
                    response,
                    Int::class.java
                ) // 解析服务器响应的 JSON 数据为 JsonArray 对象
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

    fun editTask(json: String, callback: (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = edit(json)
            withContext(Dispatchers.Main) {
                callback(result.toString())
            }
        }
    }

    fun logout(): Boolean {
        val url = "http://10.0.2.2:8080/javaweb-exercise-01-2/member"
        var conn: HttpURLConnection? = null
        try {
            if (CookieHandler.getDefault() == null) {
                CookieHandler.setDefault(CookieManager())
            }
            conn = URL(url).openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "DELETE"
            conn.useCaches = false

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                // 登出成功
                return true
            } else {
                Log.e("logout", "回應碼：${conn.responseCode}")
            }
        } catch (e: Exception) {
            conn?.disconnect()
            Log.e("logout", e.toString())
        } finally {
            conn?.disconnect()
        }
        return false
    }

    fun logoutTask() {
        GlobalScope.launch(Dispatchers.IO) {
            val result = logout()
            withContext(Dispatchers.Main) {
                if (result) {
                    // 登出成功
                    view?.let { Navigation.findNavController(it).navigate(R.id.login) }
                } else {
                    // 登出失敗
                    return@withContext
                }
            }
        }
    }
}