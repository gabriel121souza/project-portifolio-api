package br.com.gabriel.project_portfolio_api.dto.response;


import java.time.LocalDateTime;

public record ProjectMemberResponse(
        Long id,
        Long projectId,
        MemberResponse member,
        LocalDateTime allocatedAt
) {
}