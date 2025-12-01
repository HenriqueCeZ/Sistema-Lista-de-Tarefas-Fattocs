package com.example.tarefas.controller;


import com.example.tarefas.domain.Tarefa;
import com.example.tarefas.dto.TarefaCreateDTO;
import com.example.tarefas.dto.TarefaDTO;
import com.example.tarefas.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    private final TarefaService service;

    public TarefaController(TarefaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TarefaDTO>> listar() {
        List<Tarefa> lista = service.listarTodos();
        List<TarefaDTO> dtos = lista.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<TarefaDTO> criar(@Valid @RequestBody TarefaCreateDTO dto) {
        Tarefa criado = service.criar(dto);
        URI location = URI.create("/api/tarefas/" + criado.getId());
        return ResponseEntity.created(location).body(toDto(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaDTO> editar(@PathVariable Long id, @Valid @RequestBody TarefaCreateDTO dto) {
        Tarefa atualizado = service.editar(id, dto);
        return ResponseEntity.ok(toDto(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/up")
    public ResponseEntity<Void> subir(@PathVariable Long id) {
        service.moverParaCima(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/down")
    public ResponseEntity<Void> descer(@PathVariable Long id) {
        service.moverParaBaixo(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/total-custo")
    public ResponseEntity<String> totalCusto() {
        BigDecimal total = service.listarTodos().stream()
                .map(Tarefa::getCusto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(total.toString());
    }


    @PatchMapping("/{id}/ordem")
    public ResponseEntity<Void> atualizarOrdem(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer novaOrdem = body.get("ordem");
        service.atualizarOrdem(id, novaOrdem);
        return ResponseEntity.ok().build();
    }


    private TarefaDTO toDto(Tarefa t) {
        return TarefaDTO.builder()
                .id(t.getId())
                .nome(t.getNome())
                .custo(t.getCusto())
                .dataLimite(t.getDataLimite())
                .ordem(t.getOrdem())
                .build();
    }
}
