package com.example.tarefas.repository;

import com.example.tarefas.domain.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    Optional<Tarefa> findByNome(String nome);
    boolean existsByNome(String nome);
    boolean existsByNomeAndIdNot(String nome, Long id);
    Optional<Tarefa> findTopByOrderByOrdemDesc();
    Optional<Tarefa> findByOrdem(Integer ordem);
    List<Tarefa> findAllByOrdemBetween(Integer startInclusive, Integer endInclusive);

}
