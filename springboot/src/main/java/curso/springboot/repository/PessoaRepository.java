package curso.springboot.repository;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import curso.springboot.model.Pessoa;

@Repository
@Transactional
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    
    @Query("SELECT p FROM Pessoa p WHERE LOWER(p.nome) LIKE %:nome%")
    List<Pessoa> findPessoaByName(String nome);

    @Query("SELECT p FROM Pessoa p WHERE p.sexopessoa = :sexopessoa")
    List<Pessoa> findPessoabySexo(String sexopessoa);

    @Query("SELECT p FROM Pessoa p WHERE LOWER(p.nome) LIKE %:nome% AND p.sexopessoa = :sexopessoa")
    List<Pessoa> findPessoaByNameSexo(String nome, String sexopessoa);

    /*
    default Page<Pessoa> findPessoaByNamePage(String nome, Pageable pageable) {

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);

        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Example<Pessoa> example = Example.of(pessoa, exampleMatcher);

        return findAll(example, pageable);
    }
    */
    
    @Query("SELECT p FROM Pessoa p WHERE LOWER(p.nome) LIKE %:nome%")
    Page<Pessoa> findPessoaByNamePage(String nome, Pageable pageable);
    
    @Query("SELECT p FROM Pessoa p WHERE LOWER(p.nome) LIKE %:nome% AND p.sexopessoa = :sexopessoa")
    Page<Pessoa> findPessoaBySexoPage(String nome, String sexopessoa, Pageable pageable);
   

}
