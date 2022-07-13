package senai.sp.cotia.auditorio.rest;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

 


import senai.sp.cotia.auditorio.annotation.Publico;
import senai.sp.cotia.auditorio.type.Types;


@RestController
@RequestMapping("/api/types")
public class TypesRestController {
    
	//metodo para pegar os valores da enum
    @Publico
    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    public Types[] getTypes() {
        Types[] types = Types.values();
        return types;
    }
}