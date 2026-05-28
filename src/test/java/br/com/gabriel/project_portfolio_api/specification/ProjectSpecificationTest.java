package br.com.gabriel.project_portfolio_api.specification;

import br.com.gabriel.project_portfolio_api.entity.Project;
import br.com.gabriel.project_portfolio_api.enums.RiskClassification;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectSpecificationTest {

    @Test
    void shouldMatchAnyRiskWhenRiskClassificationIsNull() {
        assertTrue(ProjectSpecification.matchesRisk(buildProject("1.00", 30), null));
    }

    @Test
    void shouldMatchLowRiskProject() {
        assertTrue(ProjectSpecification.matchesRisk(buildProject("100000.00", 90), RiskClassification.BAIXO));
    }

    @Test
    void shouldMatchMediumRiskProjectByBudget() {
        assertTrue(ProjectSpecification.matchesRisk(buildProject("100000.01", 90), RiskClassification.MEDIO));
    }

    @Test
    void shouldMatchMediumRiskProjectByDuration() {
        assertTrue(ProjectSpecification.matchesRisk(buildProject("100000.00", 91), RiskClassification.MEDIO));
    }

    @Test
    void shouldMatchHighRiskProjectByBudget() {
        assertTrue(ProjectSpecification.matchesRisk(buildProject("500000.01", 90), RiskClassification.ALTO));
    }

    @Test
    void shouldMatchHighRiskProjectByDuration() {
        assertTrue(ProjectSpecification.matchesRisk(buildProject("100000.00", 181), RiskClassification.ALTO));
    }

    private Project buildProject(String totalBudget, int durationInDays) {
        LocalDate startDate = LocalDate.of(2026, 1, 1);

        return Project.builder()
                .startDate(startDate)
                .expectedEndDate(startDate.plusDays(durationInDays))
                .totalBudget(new BigDecimal(totalBudget))
                .build();
    }
}
