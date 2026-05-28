package br.com.gabriel.project_portfolio_api.specification;

import br.com.gabriel.project_portfolio_api.dto.request.ProjectFilterRequest;
import br.com.gabriel.project_portfolio_api.entity.Project;
import br.com.gabriel.project_portfolio_api.enums.RiskClassification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ProjectSpecification {

    private ProjectSpecification() {
    }

    public static Specification<Project> withFilters(ProjectFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.name() != null && !filter.name().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + filter.name().toLowerCase() + "%"
                        )
                );
            }

            if (filter.status() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("status"), filter.status())
                );
            }

            if (filter.managerId() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("manager").get("id"), filter.managerId())
                );
            }

            if (filter.startDate() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("startDate"), filter.startDate())
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static boolean matchesRisk(Project project, RiskClassification riskClassification) {
        if (riskClassification == null) {
            return true;
        }

        return calculateRisk(project) == riskClassification;
    }

    private static RiskClassification calculateRisk(Project project) {
        BigDecimal budget = project.getTotalBudget();

        long projectDurationInDays = ChronoUnit.DAYS.between(
                project.getStartDate(),
                project.getExpectedEndDate()
        );

        if (budget.compareTo(new BigDecimal("500000")) > 0 || projectDurationInDays > 180) {
            return RiskClassification.ALTO;
        }

        if (budget.compareTo(new BigDecimal("100000")) > 0 || projectDurationInDays > 90) {
            return RiskClassification.MEDIO;
        }

        return RiskClassification.BAIXO;
    }
}