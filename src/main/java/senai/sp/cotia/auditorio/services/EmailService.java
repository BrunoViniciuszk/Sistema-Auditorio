package senai.sp.cotia.auditorio.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import senai.sp.cotia.auditorio.model.EmailModel;
import senai.sp.cotia.auditorio.repository.EmailRepository;
import senai.sp.cotia.auditorio.type.StatusEmail;


@Service
public class EmailService {
 

    @Autowired
    EmailRepository emailRepository;

 

    @Autowired
    private JavaMailSender emailSender;

 

    public EmailModel sendEmail(EmailModel emailModel) {
        emailModel.setSendDateEmail(LocalDateTime.now());
        try{
        	//criando e instanciando um email com os atributos
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailModel.getEmailFrom());
            message.setTo(emailModel.getEmailTo());
            message.setSubject(emailModel.getSubject());
            message.setText(emailModel.getText());
            emailSender.send(message);

 
            //setando o status do email como enviado, se caso algo impedisse, avisa o erro
            emailModel.setStatusEmail(StatusEmail.SENT);
        } catch (MailException e){
        	System.out.println(e.getMessage());
            e.printStackTrace();            
            emailModel.setStatusEmail(StatusEmail.ERROR);
        }
        return emailRepository.save(emailModel);
    }
    
}    