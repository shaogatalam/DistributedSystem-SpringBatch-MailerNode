package BasePack.service;

import BasePack.Model.EmailObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class EmailProcessingService {

    @Autowired
    private JavaMailSender mailSender;

    @Retryable(retryFor = { MailSendException.class }, maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void processEmail(EmailObject emailObject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailObject.getEmail());
        message.setSubject("Personalized Message");
        message.setText(emailObject.getTemplate());
        mailSender.send(message);
        System.out.println("Email sent successfully to: " + emailObject.getEmail());
    }
}
