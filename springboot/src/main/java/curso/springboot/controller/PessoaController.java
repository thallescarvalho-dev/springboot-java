package curso.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.ProfissaoRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ReportUtil reportUtil;
	
	@Autowired
	private ProfissaoRepository profissaoRepository;
	
	@RequestMapping(method = RequestMethod.GET, value="**/cadastropessoa")
	public ModelAndView inicio() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 3, Sort.by("nome"))));
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}
	
	
	
	@GetMapping("**/pessoaspag")
	public ModelAndView carregaPessoaPorPaginacao(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesqsexo") String pesqsexo,
			ModelAndView model,
			@PageableDefault(size = 3, sort = {"nome"}) Pageable pageable) {
		
		Page<Pessoa> pagePessoa = null;
		if(pesqsexo != null && !pesqsexo.isEmpty()) {
			pagePessoa = pessoaRepository.findPessoaBySexoPage(nomepesquisa, pesqsexo, pageable);
		}else {
			pagePessoa = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		}
		
		model.addObject("pessoas", pagePessoa);
		model.addObject("pessoaobj", new Pessoa());
		model.addObject("pesqsexo", pesqsexo);
		model.addObject("nomepesquisa", nomepesquisa);
		model.setViewName("cadastro/cadastropessoa");
		model.addObject("profissoes", profissaoRepository.findAll());
		
		return model;
	}
	
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, 
			value = {"**/salvarpessoa"}, consumes = {"multipart/form-data"})
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult, final MultipartFile file) throws IOException{
		
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		if(bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
			modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 3, Sort.by("nome"))));
			modelAndView.addObject("pessoaobj", pessoa);
			
			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage());// vem das anotações @NotEmpty e outras
			}
			
			modelAndView.addObject("msg", msg);
			modelAndView.addObject("profissoes", profissaoRepository.findAll());
			return modelAndView;
		}
		
		if(file.getSize() > 0) {/*Cadatrando um curriculo*/
			pessoa.setCurriculo(file.getBytes());
			pessoa.setTipoFileCurriculo(file.getContentType());
			pessoa.setNomeFileCurriculo(file.getOriginalFilename());
			
		}else {
			if(pessoa.getId() != null && pessoa.getId() > 0){//editando
				
				Pessoa pessoaTemp = pessoaRepository.
						findById(pessoa.getId()).get();
				
				pessoa.setCurriculo(pessoaTemp.getCurriculo());
				pessoa.setTipoFileCurriculo(pessoaTemp.getTipoFileCurriculo());
				pessoa.setNomeFileCurriculo(pessoaTemp.getNomeFileCurriculo());
				
			}
		}
		
		pessoaRepository.save(pessoa);
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 3, Sort.by("nome"))));
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 3, Sort.by("nome"))));
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}
	
	@GetMapping("**/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 3, Sort.by("nome"))));
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}
	
	@GetMapping("**/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		
		pessoaRepository.deleteById(idpessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 3, Sort.by("nome"))));
		modelAndView.addObject("pessoa", pessoaRepository.findAll());
		modelAndView.addObject("pessoaobj", new Pessoa());
		return modelAndView;
	}
	
	@PostMapping({"**/pesquisarpessoa"})
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesqsexo") String pesqsexo,
			@PageableDefault(size = 3, sort = {"nome"}) Pageable pageable) {
		
		Page<Pessoa> pessoas = null;
		if(pesqsexo != null && !pesqsexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaBySexoPage(nomepesquisa, pesqsexo, pageable);
		}else {
			pessoas = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		}
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("nomepesquisa", nomepesquisa);
		modelAndView.addObject("pesqsexo", pesqsexo);
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}
	
	@GetMapping("**/baixarcurriculo/{idpessoa}")
	public void baixarCurriculo(@PathVariable("idpessoa") Long idpessoa,
			HttpServletResponse response) throws IOException {
		
		/*Consultar o objto pessoa no banco de dados*/
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		if(pessoa.getCurriculo() != null) {
			
			/*Setar tamanho da resposta*/
			response.setContentLength(pessoa.getCurriculo().length);
			
			/*Tipo da arquivo para download ou pode ser generica application/octet-stream*/
			response.setContentType(pessoa.getTipoFileCurriculo());
			
			/*Define o cabecalho da resposta*/
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNomeFileCurriculo());
			response.setHeader(headerKey, headerValue);
			
			/*Finaliza a resposta passando o arquivo*/
			response.getOutputStream().write(pessoa.getCurriculo());
				
		}
	}
	
	
	@GetMapping({"**/pesquisarpessoa"})
	public void imprime (@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesqsexo") String pesqsexo,
			@PageableDefault(size = 3, sort = {"nome"}) Pageable pageable,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
		if(pesqsexo != null && !pesqsexo.isEmpty()
				&& nomepesquisa != null && !nomepesquisa.isEmpty()) {/*Busca por nome e sexo*/
			pessoas = pessoaRepository.findPessoaByNameSexo(nomepesquisa, pesqsexo);
			
		}else if(nomepesquisa != null && !nomepesquisa.isEmpty()) {/*Busca somente pelo nome*/
			pessoas =  pessoaRepository.findPessoaByName(nomepesquisa);
			System.out.println("AAQQQQQQQQQQqqqqqqqqqqqqiiiiiiiiii");
			System.out.println(pessoas);
			System.out.println(nomepesquisa);
			
		}else if(pesqsexo != null && !pesqsexo.isEmpty()) {/*Busca somente pelo sexo*/
			pessoas =  pessoaRepository.findPessoabySexo(pesqsexo);
			
		}else {/*Busca por todos*/
			pessoas =  pessoaRepository.findPessoaByName(nomepesquisa);
		}
		
		/*Chamando o serviços que faz a geração do relatório*/
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		
		/*Tamanho da resposta*/
		response.setContentLength(pdf.length);
		
		/*Definir na resposta o tipo de arquivo*/
		response.setContentType("application/octet-stream");
		
		/*Definir o cabeçalho da resposta*/
		String headerkey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerkey, headerValue);
		
		/*Finaliza a resposta para o navegador*/
		response.getOutputStream().write(pdf);
		
	}
	
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		return modelAndView;
	}
	
	@PostMapping("/addfonePessoa/{pessoaid}")
	public ModelAndView addfonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		
		
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		
		if(telefone != null && telefone.getNumero().isEmpty() 
				|| telefone.getTipo().isEmpty()) {
			
			ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
			modelAndView.addObject("pessoaobj", pessoa);
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			
			List<String> msg = new ArrayList<String>();
			
			if(telefone.getNumero().isEmpty()) {
				msg.add("Numero deve ser informao");
			}
			
			if(telefone.getTipo().isEmpty()) {
				msg.add("Tipo deve ser informao");
			}
			
			modelAndView.addObject("msg", msg);
			
			return modelAndView;
		}
		
		
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
		return modelAndView;
	}
	
	@GetMapping("/removertelefone/{idtelefone}")
	public ModelAndView removertelefone(@PathVariable("idtelefone") Long idtelefone) {
		
		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
		
		telefoneRepository.deleteById(idtelefone);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		return modelAndView;
	}
	
}
