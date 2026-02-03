package com.liquordb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderAddress;

    // TODO Retry
    @Async
    public void sendMail(String to, String subject, String text) {

        log.info("비밀번호 재설정 메일 발송 시작: {}", to);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom(senderAddress);
            mailSender.send(message);
            log.info("비밀번호 재설정 메일 발송 성공: {}", to);
        } catch (MailException e) {
            log.error("비밀번호 재설정 메일 발송 실패: {}", to, e);
        }
    }

}
