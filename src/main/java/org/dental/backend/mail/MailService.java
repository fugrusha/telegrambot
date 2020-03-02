package org.dental.backend.mail;

import org.dental.backend.domain.AppUser;
import org.dental.backend.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PropertySource("classpath:telegram.properties")
public class MailService {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private JavaMailSender emailSender;

    @Value("${bot.email.subject}")
    private String emailSubject;

    @Value("${bot.email.from}")
    private String emailFrom;

    @Value("${bot.email.to}")
    private String emailTo;

    public void sendNotification() {
        // TODO

        List<AppUser> users = appUserService.findAllUsers();
        if (users.size() == 0) return;

        StringBuilder sb = new StringBuilder();

        users.forEach(user ->
                sb.append("Phone: ")
                .append(user.getPhone())
                .append("\r\n")
                .append("Email: ")
                .append(user.getEmail())
                .append("\r\n\r\n"));

        sendEmail(sb.toString());
    }

    public void sendEmail(String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(emailTo);
        message.setFrom(emailFrom);
        message.setSubject(emailSubject);
        message.setText(text);

        emailSender.send(message);
    }
}
