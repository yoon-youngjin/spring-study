package com.example.kotlintestcodewitharchitecture.user.service.port

interface MailSender {
    fun send(email: String, title: String, content: String)
}