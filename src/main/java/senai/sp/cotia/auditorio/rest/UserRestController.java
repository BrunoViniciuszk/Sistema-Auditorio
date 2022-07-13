package senai.sp.cotia.auditorio.rest;

import java.net.URI;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;




import senai.sp.cotia.auditorio.annotation.Privado;
import senai.sp.cotia.auditorio.annotation.Publico;
import senai.sp.cotia.auditorio.model.Erro;
import senai.sp.cotia.auditorio.model.Reservation;
import senai.sp.cotia.auditorio.model.TokenJWT;
import senai.sp.cotia.auditorio.model.Usuario;
import senai.sp.cotia.auditorio.repository.UserRepository;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserRestController {
		
		// constantes para gerar o token
		public static final String EMISSOR = "Sen@i";
		public static final String SECRET = "@uditorium";
		
		@Autowired
		private UserRepository repository;
				
			//cadasta reserva
			@Publico
			@RequestMapping(value="cadastrar", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
			public Object criarUsuario(@RequestBody Usuario usuario) {
					try {
						// salvar o usuário no banco de dados
						repository.save(usuario);
						// retorna code 201 com a url para acesso no location e usuario inserido no corpo da resposta
						return ResponseEntity.ok(HttpStatus.CREATED);
				} catch (DataIntegrityViolationException e) {
				e.printStackTrace();
				Erro erro = new Erro();
				erro.setStatusCode(500);
				erro.setMensagem("Erro de Constraint: Registro Duplicado"); 
				erro.setException(e.getClass().getName());
				return new ResponseEntity<Object>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
			}catch (Exception e) {
				e.printStackTrace();
				Erro erro = new Erro();
				erro.setStatusCode(500);
				erro.setMensagem("Erro: "+e.getMessage());
				erro.setException(e.getClass().getName());
				return new ResponseEntity<Object>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		@Privado
		@RequestMapping(value="/{id}", method = RequestMethod.GET)
		public ResponseEntity<Usuario> findUsuario(@PathVariable("id") Long idUsuario) {
			// busca o usuario
			 Optional<Usuario> user = repository.findById(idUsuario);
			 if(user.isPresent()) {
				 return ResponseEntity.ok(user.get());
			 }else {
				 return ResponseEntity.notFound().build();
			 }
		}
		
		//atualiza o usuario recebendo o id
		@Privado
		@RequestMapping(value="/{id}", method = RequestMethod.PUT)
		public ResponseEntity<Void> atualizarUsuario(@RequestBody Usuario usuario, @PathVariable("id") Long id) {
			// valida o ID
			if(id != usuario.getId()) {
				throw new RuntimeException("ID Inválido");
			}
			// salva o usuario no BD
			repository.save(usuario);
			// criar um cabeçalho HTTP
			HttpHeaders header = new HttpHeaders();
			header.setLocation(URI.create("/api/usuario"));
			return new ResponseEntity<Void>(header, HttpStatus.OK);
			
		}
		
		@Privado
		@RequestMapping(value="/{id}", method = RequestMethod.DELETE)
		public ResponseEntity<Void> excluirUsuario(@PathVariable("id") Long idUsuario) {
			repository.deleteById(idUsuario);
			return ResponseEntity.noContent().build();
		}
		
		
		
		@Publico
		@RequestMapping(value = "login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<TokenJWT> logar(@RequestBody Usuario usuario) {
			// buscar o usuario no BD
			usuario = repository.findByNifAndSenha(usuario.getNif(), usuario.getSenha());
			// verifica se existe o usuario
			if(usuario != null) {
				int contar = usuario.getContLogin();
                usuario.setContLogin(contar+1);
                repository.save(usuario);
				Map<String, Object> payload = new HashMap<String, Object>();
				payload.put("usuario_id", usuario.getId());
				payload.put("usuario_nif", usuario.getNif());
				payload.put("usuario_tipo", usuario.getType().toString());
				//payload.put("usuario_nome", usuario.getNome()); 
				// definir a data de expiração
				Calendar expiracao = Calendar.getInstance();
				expiracao.add(Calendar.HOUR, 1);
				// algoritmo para assinar o token
				Algorithm algoritmo = Algorithm.HMAC256(SECRET);
				// gerar o token
				TokenJWT tokenJwt = new TokenJWT();
				tokenJwt.setToken(JWT.create().withPayload(payload).withIssuer(EMISSOR).withExpiresAt(expiracao.getTime()).sign(algoritmo));
				return ResponseEntity.ok(tokenJwt);
			}else {
				return new ResponseEntity<TokenJWT>(HttpStatus.UNAUTHORIZED);
			}
		}
		
		//metodo que lista os usuários
		@Publico
		@RequestMapping(value = "lista", method = RequestMethod.GET)
		public Iterable<Usuario> listaUsuario(){
			return repository.findAll();
		}
		
		//busca no banco de dados os usuarios do tipo comum
		@Privado
		@RequestMapping(value = "verifica", method = RequestMethod.GET)
		public List<Usuario> listaComuns() {
			return repository.findAllByCommuns() ;
		}
		//busca no banco de dados os usuarios do tipo adm
		@Privado
        @RequestMapping(value = "verificaAdmin", method = RequestMethod.GET)
        public List<Usuario> listaAdmins() {
            return repository.findAllByAdministrador() ;
        }
		
		
		@Privado
		@RequestMapping(value = "decodaToken", method = RequestMethod.GET)
		public ResponseEntity<String> decode(HttpServletRequest request,
				HttpServletResponse response) {
			String token = null;
			// obtem o token da request
			token = request.getHeader("Authorization");
			// algoritimo para descriptografar
			Algorithm algoritimo = Algorithm.HMAC256(UserRestController.SECRET);
			// objeto para verificar o token
			JWTVerifier verifier = JWT.require(algoritimo).withIssuer(UserRestController.EMISSOR).build();
			// validar o token
			DecodedJWT decoded = verifier.verify(token);
			// extrair os dados do payload
			Map<String, Claim> payload = decoded.getClaims();
			String tipo = payload.get("usuario_tipo").toString();
			return ResponseEntity.ok(tipo);
		}		
		
		
		//decoda o token para pegar o id do usuário que está logado na sessão
		@Privado
		@RequestMapping(value = "sendId", method = RequestMethod.GET)
		public ResponseEntity<Long> decoda(HttpServletRequest request,
				HttpServletResponse response) {
			String token = null;
			// obtem o token da request
			token = request.getHeader("Authorization");
			// algoritimo para descriptografar
			Algorithm algoritimo = Algorithm.HMAC256(UserRestController.SECRET);
			// objeto para verificar o token
			JWTVerifier verifier = JWT.require(algoritimo).withIssuer(UserRestController.EMISSOR).build();
			// validar o token
			DecodedJWT decoded = verifier.verify(token);
			// extrair os dados do payload
			Map<String, Claim> payload = decoded.getClaims();
			String id = payload.get("usuario_id").toString();
			Long idl = Long.parseLong(id);
			return ResponseEntity.ok(idl);
		}
		
		// chamando o método que buscar um usuário por qualquer coisa
		@RequestMapping(value = "/findusuario/{p}")
		public Iterable<Usuario> findByAll(@PathVariable("p") String param) {
			return repository.procurarUsuario(param);
		}
}		