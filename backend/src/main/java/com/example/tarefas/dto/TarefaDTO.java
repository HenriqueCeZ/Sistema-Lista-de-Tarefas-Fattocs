package com.example.tarefas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarefaDTO {
    private Long id;
    private String nome;
    private BigDecimal custo;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataLimite;

    private Integer ordem;
}
