package com.example.dailytaskforbetty.model

// 个人信息数据类
data class UserInfo(
    val name: String = "Betty", // 默认姓名
    val birthday: String = "2001-09-16", // 默认生日
    val gender: String = "女", // 默认性别
    val signature: String = "今天也要加油呀！" // 个性签名
)