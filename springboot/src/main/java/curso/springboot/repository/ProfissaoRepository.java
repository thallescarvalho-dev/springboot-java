package curso.springboot.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Profissao;

@Transactional
@Repository
public interface ProfissaoRepository extends CrudRepository<Profissao, Long>{
	
	@Query("select p from Profissao p where p.nome like %?1%")
	List<Profissao> findPessoaByName(String nome);
}
