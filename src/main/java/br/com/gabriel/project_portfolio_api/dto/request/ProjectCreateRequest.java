package br.com.gabriel.project_portfolio_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectCreateRequest(

        @NotBlank(message = "O nome do projeto é obrigatório")
        @Size(max = 160, message = "O nome do projeto deve ter no máximo 160 caracteres")
        String name,

        @NotNull(message = "A data de início é obrigatória")
        LocalDate startDate,

        @NotNull(message = "A previsão de término é obrigatória")
        LocalDate expectedEndDate,

        LocalDate actualEndDate,

        @NotNull(message = "O orçamento total é obrigatório")
        @DecimalMin(value = "0.01", message = "O orçamento total deve ser maior que zero")
        BigDecimal totalBudget,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "O gerente responsável é obrigatório")
        Long managerId
) {
}