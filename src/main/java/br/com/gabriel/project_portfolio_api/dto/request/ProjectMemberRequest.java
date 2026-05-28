package br.com.gabriel.project_portfolio_api.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProjectMemberRequest(

        @NotNull(message = "O membro é obrigatório")
        Long memberId
) {
}