package com.example.tarefas.repository;

import com.example.tarefas.domain.Tarefa;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    Optional<Tarefa> findByNome(String nome);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    Optional<Tarefa> findTopByOrderByOrdemDesc();

    Optional<Tarefa> findByOrdem(Integer ordem);

    List<Tarefa> findAllByOrdemBetween(Integer startInclusive, Integer endInclusive);

    // ---------- Métodos com lock (usados na reordenação) ----------

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Tarefa t where t.id = :id")
    Optional<Tarefa> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Tarefa t where t.ordem = (select max(t2.ordem) from Tarefa t2)")
    Optional<Tarefa> findTopByOrderByOrdemDescForUpdate();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Tarefa t where t.ordem between :start and :end order by t.ordem")
    List<Tarefa> findAllByOrdemBetweenForUpdate(
            @Param("start") Integer startInclusive,
            @Param("end") Integer endInclusive
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t.id from Tarefa t")
    List<Long> lockTableForUpdate();
}
