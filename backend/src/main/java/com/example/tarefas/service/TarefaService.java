package com.example.tarefas.service;

import com.example.tarefas.domain.Tarefa;
import com.example.tarefas.dto.TarefaCreateDTO;
import com.example.tarefas.exception.BadRequestException;
import com.example.tarefas.exception.ResourceNotFoundException;
import com.example.tarefas.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;


@Service
public class TarefaService {

    private final TarefaRepository repo;

    public TarefaService(TarefaRepository repo) {
        this.repo = repo;
    }

    public List<Tarefa> listarTodos() {
        return repo.findAll().stream()
                .sorted((a,b) -> a.getOrdem().compareTo(b.getOrdem()))
                .toList();
    }

    @Transactional
    public Tarefa criar(TarefaCreateDTO dto) {
        validaDadosObrigatorios(dto.getNome(), dto.getCusto(), dto.getDataLimite());

        if (repo.existsByNome(dto.getNome())) {
            throw new BadRequestException("Já existe tarefa com esse nome");
        }

        Integer novaOrdem = repo.findTopByOrderByOrdemDesc()
                .map(t -> t.getOrdem() + 1)
                .orElse(1);

        Tarefa t = Tarefa.builder()
                .nome(dto.getNome().trim())
                .custo(dto.getCusto())
                .dataLimite(dto.getDataLimite())
                .ordem(novaOrdem)
                .build();

        return repo.save(t);
    }

    @Transactional
    public Tarefa editar(Long id, TarefaCreateDTO dto) {
        validaDadosObrigatorios(dto.getNome(), dto.getCusto(), dto.getDataLimite());

        Tarefa existente = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        if (repo.existsByNomeAndIdNot(dto.getNome(), id)) {
            throw new BadRequestException("Já existe outra tarefa com esse nome");
        }

        existente.setNome(dto.getNome().trim());
        existente.setCusto(dto.getCusto());
        existente.setDataLimite(dto.getDataLimite());

        return repo.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Tarefa não encontrada");
        repo.deleteById(id);
    }

    private void validaDadosObrigatorios(String nome, BigDecimal custo, java.time.LocalDate dataLimite) {
        if (nome == null || nome.trim().isEmpty()) throw new BadRequestException("Nome é obrigatório");
        if (custo == null) throw new BadRequestException("Custo é obrigatório");
        if (custo.compareTo(BigDecimal.ZERO) < 0) throw new BadRequestException("Custo deve ser >= 0");
        if (dataLimite == null) throw new BadRequestException("Data limite é obrigatória");
    }
    @Transactional
    public void moverParaCima(Long id) {
        Tarefa t = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
        if (t.getOrdem() <= 1) return;
        atualizarOrdem(id, t.getOrdem() - 1);
    }

    @Transactional
    public void moverParaBaixo(Long id) {
        Tarefa t = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
        Integer max = repo.findTopByOrderByOrdemDesc()
                .map(Tarefa::getOrdem)
                .orElse(t.getOrdem());
        if (t.getOrdem() >= max) return;
        atualizarOrdem(id, t.getOrdem() + 1);
    }


    @Transactional
    public void atualizarOrdem(Long id, Integer novaOrdem) {

        Tarefa atual = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        int ordemAtual = atual.getOrdem();

        if (ordemAtual == novaOrdem) return;

        Tarefa outra = repo.findByOrdem(novaOrdem).orElse(null);


        atual.setOrdem(999999);
        repo.save(atual);

        if (outra != null) {
            outra.setOrdem(ordemAtual);
            repo.save(outra);
        }

        atual.setOrdem(novaOrdem);
        repo.save(atual);
    }


}