package br.com.gabriel.project_portfolio_api.dto.request;

import br.com.gabriel.project_portfolio_api.enums.MemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemberCreateRequest(

        @NotBlank(message = "O nome do membro é obrigatório")
        @Size(max = 120, message = "O nome do membro deve ter no máximo 120 caracteres")
        String name,

        @NotNull(message = "A atribuição do membro é obrigatória")
        MemberRole role
) {
}