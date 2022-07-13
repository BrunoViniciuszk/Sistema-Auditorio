package senai.sp.cotia.auditorio.repository;

 
import org.springframework.data.repository.PagingAndSortingRepository;

 

import senai.sp.cotia.auditorio.model.EmailModel;


public interface EmailRepository extends PagingAndSortingRepository<EmailModel, Long> {
	
}
 