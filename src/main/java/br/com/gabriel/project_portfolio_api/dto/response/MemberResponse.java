package br.com.gabriel.project_portfolio_api.dto.response;


import br.com.gabriel.project_portfolio_api.enums.MemberRole;

import java.time.LocalDateTime;

public record MemberResponse(
        Long id,
        String name,
        MemberRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}