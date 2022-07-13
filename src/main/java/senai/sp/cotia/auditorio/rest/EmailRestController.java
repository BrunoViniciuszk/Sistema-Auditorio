package senai.sp.cotia.auditorio.rest;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import senai.sp.cotia.auditorio.model.EmailModel;
import senai.sp.cotia.auditorio.services.EmailService;

 
@RestController
@RequestMapping("api/email")
public class EmailRestController {

 

    @Autowired
    EmailService emailService;
    

 
    //método que envia o email 
    @RequestMapping(value="sending-email", method = RequestMethod.POST)
    public ResponseEntity<EmailModel> sendingEmail(@RequestBody @Valid EmailModel emailModel) {
        emailService.sendEmail(emailModel);
        return new ResponseEntity<>(emailModel, HttpStatus.CREATED);
    }
    
}    