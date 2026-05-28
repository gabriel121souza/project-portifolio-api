package br.com.gabriel.project_portfolio_api.dto.request;

import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.enums.RiskClassification;

import java.time.LocalDate;

public record ProjectFilterRequest(
        String name,
        ProjectStatus status,
        Long managerId,
        LocalDate startDate,
        RiskClassification riskClassification
) {
}