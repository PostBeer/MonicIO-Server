package com.example.monicio.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Email service for sending mails.
 *
 * @author Nikita Zhiznevskiy
 * @see JavaMailSender
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    /**
     * Send simple message.
     *
     * @param To   email receiver
     * @param text email text for activation
     * @throws MessagingException the messaging exception
     */
    @Async
    public void sendSimpleMessage(String To, String text) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setTo(To);
        helper.setText(text, true);
        helper.setSubject("Сообщение от команды разработчиков");
        mailSender.send(mimeMessage);

    }
}
