package br.com.gabriel.project_portfolio_api.dto.request;

import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;

public record ProjectStatusUpdateRequest(

        @NotNull(message = "O novo status é obrigatório")
        ProjectStatus status
) {
}