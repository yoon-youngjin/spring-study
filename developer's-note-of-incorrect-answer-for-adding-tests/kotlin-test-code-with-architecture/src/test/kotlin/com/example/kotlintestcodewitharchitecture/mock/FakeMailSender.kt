package com.example.kotlintestcodewitharchitecture.mock

import com.example.kotlintestcodewitharchitecture.user.service.port.MailSender

class FakeMailSender : MailSender {
    private val sendMails = mutableMapOf<String, MutableList<Pair<String, String>>>()

    fun getSendMails(
        email: String,
    ): List<Pair<String, String>> {
        return sendMails[email] ?: mutableListOf()
    }

    override fun send(email: String, title: String, content: String) {
        if (!sendMails.containsKey(email)) {
            sendMails[email] = mutableListOf()
        }
        sendMails[email]!!.add(Pair(title, content))
    }
}