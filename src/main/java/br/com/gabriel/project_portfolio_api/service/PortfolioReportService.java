package br.com.gabriel.project_portfolio_api.service;

import br.com.gabriel.project_portfolio_api.dto.response.PortfolioReportResponse;
import br.com.gabriel.project_portfolio_api.entity.Project;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.repository.ProjectMemberRepository;
import br.com.gabriel.project_portfolio_api.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioReportService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public PortfolioReportService(
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Transactional(readOnly = true)
    public PortfolioReportResponse generateSummary() {
        Map<ProjectStatus, Long> projectsByStatus = buildProjectsByStatus();
        Map<ProjectStatus, BigDecimal> budgetByStatus = buildBudgetByStatus();
        Double averageClosedProjectDuration = calculateAverageClosedProjectDuration();
        Long totalUniqueAllocatedMembers = projectMemberRepository.countDistinctAllocatedMembers();

        return new PortfolioReportResponse(
                projectsByStatus,
                budgetByStatus,
                averageClosedProjectDuration,
                totalUniqueAllocatedMembers
        );
    }

    private Map<ProjectStatus, Long> buildProjectsByStatus() {
        Map<ProjectStatus, Long> result = new EnumMap<>(ProjectStatus.class);

        Arrays.stream(ProjectStatus.values())
                .forEach(status -> result.put(status, projectRepository.countByStatus(status)));

        return result;
    }

    private Map<ProjectStatus, BigDecimal> buildBudgetByStatus() {
        Map<ProjectStatus, BigDecimal> result = new EnumMap<>(ProjectStatus.class);

        Arrays.stream(ProjectStatus.values())
                .forEach(status -> {
                    BigDecimal total = projectRepository.findByStatus(status)
                            .stream()
                            .map(Project::getTotalBudget)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    result.put(status, total);
                });

        return result;
    }

    private Double calculateAverageClosedProjectDuration() {
        List<Project> closedProjects = projectRepository.findByStatus(ProjectStatus.ENCERRADO);

        if (closedProjects.isEmpty()) {
            return 0.0;
        }

        return closedProjects.stream()
                .filter(project -> project.getActualEndDate() != null)
                .mapToLong(project -> ChronoUnit.DAYS.between(
                        project.getStartDate(),
                        project.getActualEndDate()
                ))
                .average()
                .orElse(0.0);
    }
}