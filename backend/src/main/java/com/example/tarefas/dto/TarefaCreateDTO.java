package com.example.tarefas.dto;


import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarefaCreateDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Custo é obrigatório")
    @DecimalMin(value = "0.00", inclusive = true, message = "Custo deve ser >= 0")
    private BigDecimal custo;

    @NotNull(message = "Data limite é obrigatória")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataLimite;
}
