package artgallery.hsnotifications.email;

import artgallery.hsnotifications.model.EmailDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender emailSender;
    static private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${art-mail.mail-subject}")
    private String subject;

    @KafkaListener(topics = "email", groupId = "notifications")
    public void sendSimpleMessage(String msg) throws MailSendException, JsonProcessingException {
        SimpleMailMessage message = new SimpleMailMessage();
        EmailDTO email = objectMapper.readValue(msg, EmailDTO.class);
        message.setFrom(sender);
        message.setTo(email.getEmail());
        message.setSubject(subject);
        message.setText(String.format(
                "Привет, зарегистрирован аккаунт на почту %s в сервисе.\n Можно зайти с логином: %s\n используя пароль: %s",
                email.getEmail(), email.getLogin(), email.getPassword()));
        emailSender.send(message);
    }
}
