package com.example.androidwebservertest

class check {

    fun userNameIsInvalid(userName:String?):String{
        val usernameLess = Regex("^.{0,4}\$") //少於5
        val usernameTooMuch = Regex("^.{51,}\$") //多於50
        val usernameNotValid = Regex("^[a-zA-Z0-9]*\$") //是正確字元
        if(userName?.isEmpty()!!|| userName?.isBlank()!!){//空值空字串
            return "不可為空"
        }
        if(userName?.matches(usernameLess)!!){
            return "不可少於5"
        }

        if(userName?.matches(usernameTooMuch)!!){
            return "不可多於50"
        }

        if(!(userName?.matches(usernameNotValid)!!)){
            return "只能為英數字"
        }
        return "過關"
    }
    fun passwordIsInvalid(password:String?):String{
        val passwordLess = Regex("^.{0,5}\$") //少於6
        val passwordTooMuch = Regex("^.{13,}\$") //多於12
        val passwordNotValid = Regex("^[a-zA-Z0-9]*\$") //是正確字元
        if(password?.isEmpty()!!|| password?.isBlank()!!){//空值空字串
            return "不可為空"
        }
        if(password?.matches(passwordLess)!!){
            return "不可少於6"
        }

        if(password?.matches(passwordTooMuch)!!){
            return "不可多於12"
        }

        if(!(password?.matches(passwordNotValid)!!)){
            return "只能為英數字"
        }
        return "過關"
    }
    fun nickNameIsInvalid(nickName:String?):String{
        val nickNameTooMuch = Regex("^.{21,}\$") //多於20
        if(nickName?.isEmpty()!!|| nickName?.isBlank()!!){//空值空字串
            return "不可為空"
        }
        if(nickName?.matches(nickNameTooMuch)!!){
            return "不可多於20"
        }
        return "過關"
    }

    fun nullcheck(input:String):Boolean{
        if(input.isBlank()||input.isNullOrEmpty()){
            return true
        }
        return false
    }
}
