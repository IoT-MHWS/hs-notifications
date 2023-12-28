package artgallery.hsnotifications.mail;

import artgallery.hsnotifications.model.MessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
@RequiredArgsConstructor
public class MainSender {
    private final JavaMailSender emailSender;

    @Value("${art-mail.mail-sender}")
    private String senderMail;

    @Value("${art-mail.mail-subject}")
    private String subjectMail;

    @Value("${art-mail.mail-text}")
    private String textMail;

    @KafkaListener(topics = "ATopic", groupId = "artgallery")
    public void sendSimpleMessage(MessageDTO messageDTO) throws JsonProcessingException {
        SimpleMailMessage message = new SimpleMailMessage();
        String mail = extractMailFromMessageDTO(messageDTO);
        try {
            checkMail(mail);
        } catch (IllegalArgumentException e) {
            System.out.println("Incorrect email address: " + mail);
            return;
        }
        message.setFrom(senderMail);
        message.setTo(mail);
        message.setSubject(subjectMail);
        message.setText(textMail);
        emailSender.send(message);
        System.out.println("The mail has sent successfully");
    }

    private String extractMailFromMessageDTO(MessageDTO messageDTO) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(messageDTO.getMail());
        return jsonNode.get("mail").asText();
    }

    private void checkMail(String mail) {
        String emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(mail);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Incorrect email address: " + mail);
        }
    }
}
