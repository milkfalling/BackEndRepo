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
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.*
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
        with(binding)
        {
            btRegisterLogin.setOnClickListener {
                Navigation.findNavController(it).navigate(R.id.register)
            }
            btLoginLogin.setOnClickListener {
                val username = viewModel?.userName?.value
                val password = viewModel?.password?.value

                data class MyData(
                    val username: String?,
                    val password: String?
                )
                val myData = MyData(username, password)
                val gson = Gson()
                val json = gson.toJson(myData)

                GlobalScope.launch(Dispatchers.Main) {
                    loginTask(json) { result ->
                        if (result != "null") {
                            activity?.runOnUiThread {
                                tvResultLogin.text = "歡迎回來，尊貴的$result\n自動跳轉中，請稍後"
                            }
                            GlobalScope.launch(Dispatchers.Main) {
                                delay(5000)
                                val bundle = Bundle()
                                bundle.putString("username", result)
                                Navigation.findNavController(it).navigate(R.id.edit, bundle)
                            }
                        } else {
                            tvResultLogin.text =
                                "確認一下帳密好嗎?????????????????????????????????????????????????????????????????????????"
                            return@loginTask
                        }
                    }
                }
            }
        }
    }

    fun login(json: String): JsonObject? {
        val url = "http://10.0.2.2:8080/javaweb-exercise-01-2/member"
        var conn: HttpURLConnection? = null
        try {
            if (CookieHandler.getDefault() == null) {
                CookieHandler.setDefault(CookieManager())
            }
            conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.useCaches = false
            conn.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            conn.outputStream.writer().use { it.write(json) }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = conn.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                return Gson().fromJson(
                    response,
                    JsonObject::class.javaObjectType
                )
            } else {
                Log.e("request2", "Response code: ${conn.responseCode}")
            }
        } catch (e: Exception) {
            conn?.disconnect()
            Log.e("main", e.toString())
        } finally {
            conn?.disconnect()
        }
        return null
    }

    fun loginTask(json: String, callback: (String?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = login(json)
            val jsonObject: JsonObject? = result
            val jsonElement: JsonElement? = jsonObject?.get("nickname")
            withContext(Dispatchers.Main) {
                callback(jsonElement.toString())
            }
        }
    }
}
