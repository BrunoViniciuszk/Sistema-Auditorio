package senai.sp.cotia.auditorio.model;


import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;



import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import senai.sp.cotia.auditorio.type.StatusEvent;
@Data
@Entity
public class Reservation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String titulo;
	private String descricao;
	@Column(unique = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private Calendar dataInicio;
	@Column(unique = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private Calendar dataTermino;
	private boolean repetir;
	private String participantes;
	@ManyToOne
	private Usuario usuario;
	@Enumerated(EnumType.STRING)
	private StatusEvent status;
	
	
	

}
