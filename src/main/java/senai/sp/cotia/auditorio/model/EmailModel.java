package senai.sp.cotia.auditorio.model;

 


import lombok.Data;
import senai.sp.cotia.auditorio.type.StatusEmail;

 

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

 

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "EMAIL")
public class EmailModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long emailId;
    @NotBlank
    private String ownerRef;
    @NotBlank
    @Email
    private String emailFrom;
    @NotBlank
    @Email
    private String emailTo;
    @NotBlank
    private String subject;
    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String text;
    private LocalDateTime sendDateEmail;
    private StatusEmail statusEmail;
}