package artgallery.hsnotifications.mail;

import artgallery.hsnotifications.model.EmailDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String senderMail;

    @Value("${art-mail.mail-subject}")
    private String subjectMail;

    @Value("${art-mail.mail-text}")
    private String textMail;

    @KafkaListener(topics = "email", groupId = "notifications")
    public void sendSimpleMessage(EmailDTO emailDTO) throws JsonProcessingException {
        SimpleMailMessage message = new SimpleMailMessage();
        String mail = extractMailFromMessageDTO(emailDTO);
        message.setFrom(senderMail);
        message.setTo(mail);
        message.setSubject(subjectMail);
        message.setText(textMail);
        try {
            emailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private String extractMailFromMessageDTO(EmailDTO emailDTO) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(emailDTO.getEmail());
        return jsonNode.get("email").asText();
    }
}
