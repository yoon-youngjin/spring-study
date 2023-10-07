package com.example.kotlintestcodewitharchitecture.user.infrastructure

import com.example.kotlintestcodewitharchitecture.user.service.port.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class MailSenderImpl(
    private val javaMailSender: JavaMailSender,
) : MailSender {
    override fun send(email: String, title: String, content: String) {
        val message = SimpleMailMessage()
        message.setTo(email)
        message.subject = title
        message.text = content
        javaMailSender.send(message)
    }
}