package br.com.gabriel.project_portfolio_api.dto.response;

import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.enums.RiskClassification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate expectedEndDate,
        LocalDate actualEndDate,
        BigDecimal totalBudget,
        String description,
        MemberResponse manager,
        ProjectStatus status,
        RiskClassification riskClassification,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}