package com.example.tarefas.domain;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Tarefas", 
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "nome")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "nome", nullable = false, unique = true)
    private String nome;

    @Column(name = "custo", nullable = false, precision = 15, scale = 2)
    private BigDecimal custo;

    @Column(name = "data_limite", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataLimite;

    @Column(name = "ordem", nullable = false)
    private Integer ordem;

    public boolean custoMaiorOuIgualA1000() {
        return custo != null && custo.compareTo(new BigDecimal("1000.00")) >= 0;
    }
}
