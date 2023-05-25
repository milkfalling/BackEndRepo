package com.example.androidwebservertest

class NetworkSettings {
    companion object {
        private const val PORT = "8080"
        private const val HOST = "10.0.0.2"
        const val SIGN_IN = "http://$HOST:$PORT/javaweb-exercise-01-2/member/register"
        const val SIGN_UP = "http://$HOST:$PORT/demo/sign/up"
    }
}