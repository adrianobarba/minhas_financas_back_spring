package com.adrianobarbosa.minhasfinancas.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrianobarbosa.minhasfinancas.api.dto.LancamentoDTO;
import com.adrianobarbosa.minhasfinancas.exception.RegraNegocioException;
import com.adrianobarbosa.minhasfinancas.model.entity.Lancamento;
import com.adrianobarbosa.minhasfinancas.model.entity.Usuario;
import com.adrianobarbosa.minhasfinancas.model.enums.StatusLancamento;
import com.adrianobarbosa.minhasfinancas.model.enums.TipoLancamento;
import com.adrianobarbosa.minhasfinancas.service.LancamentoService;
import com.adrianobarbosa.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {
	
	private LancamentoService service;
	private UsuarioService usuarioService;
	
	public LancamentoResource(LancamentoService service) {
		this.service = service;
	}
	

	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamento entidade = converter(dto);
			entidade = service.salvar(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);
		}catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return service.obterPorId(id).map( entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
 			}catch(RegraNegocioException e) { 
 				
			return ResponseEntity.badRequest().body(e.getMessage());
 			}
			
		}).orElseGet( () 
				-> new ResponseEntity("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST) );
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService
		.obterPorId(dto.getUsuario())
		.orElseThrow( () -> new RegraNegocioException("Usuário não encontrado para o Id informado."));
		
		lancamento.setUsuario(usuario);
		lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		
		return lancamento;
	}

}