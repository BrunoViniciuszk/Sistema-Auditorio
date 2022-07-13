 package senai.sp.cotia.auditorio.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import senai.sp.cotia.auditorio.type.Types;
import senai.sp.cotia.auditorio.util.HashUtil;
@Data
@Entity
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String nif;
	private String nome;
	@Column(unique = true)
	private String email;
	@JsonProperty(access = Access.WRITE_ONLY)
	private String senha;
	@Enumerated(EnumType.STRING)
	private Types type;
	private int contLogin;
	
	public void setSenha(String senha) {
    this.senha = HashUtil.hash256(senha);
 }
	
	public void setSenhaComHash(String hash) {
        // seta o hash na senha
        this.senha = hash;
	}
	
}


