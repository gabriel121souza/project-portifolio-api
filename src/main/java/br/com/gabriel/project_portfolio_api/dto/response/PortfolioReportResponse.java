package br.com.gabriel.project_portfolio_api.dto.response;

import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;

import java.math.BigDecimal;
import java.util.Map;

public record PortfolioReportResponse(
        Map<ProjectStatus, Long> projectsByStatus,
        Map<ProjectStatus, BigDecimal> budgetByStatus,
        Double averageClosedProjectDurationInDays,
        Long totalUniqueAllocatedMembers
) {
}