package br.com.gabriel.project_portfolio_api.mapper;

import br.com.gabriel.project_portfolio_api.dto.request.ProjectCreateRequest;
import br.com.gabriel.project_portfolio_api.dto.request.ProjectUpdateRequest;
import br.com.gabriel.project_portfolio_api.dto.response.ProjectMemberResponse;
import br.com.gabriel.project_portfolio_api.dto.response.ProjectResponse;
import br.com.gabriel.project_portfolio_api.entity.Member;
import br.com.gabriel.project_portfolio_api.entity.Project;
import br.com.gabriel.project_portfolio_api.entity.ProjectMember;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.enums.RiskClassification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Component
public class ProjectMapper {

    private final MemberMapper memberMapper;

    public ProjectMapper(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    public Project toEntity(ProjectCreateRequest request, Member manager) {
        return Project.builder()
                .name(request.name())
                .startDate(request.startDate())
                .expectedEndDate(request.expectedEndDate())
                .actualEndDate(request.actualEndDate())
                .totalBudget(request.totalBudget())
                .description(request.description())
                .manager(manager)
                .status(ProjectStatus.EM_ANALISE)
                .build();
    }

    public void updateEntity(Project project, ProjectUpdateRequest request, Member manager) {
        project.setName(request.name());
        project.setStartDate(request.startDate());
        project.setExpectedEndDate(request.expectedEndDate());
        project.setActualEndDate(request.actualEndDate());
        project.setTotalBudget(request.totalBudget());
        project.setDescription(request.description());
        project.setManager(manager);
    }

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getStartDate(),
                project.getExpectedEndDate(),
                project.getActualEndDate(),
                project.getTotalBudget(),
                project.getDescription(),
                memberMapper.toResponse(project.getManager()),
                project.getStatus(),
                calculateRisk(project),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    public ProjectMemberResponse toProjectMemberResponse(ProjectMember projectMember) {
        return new ProjectMemberResponse(
                projectMember.getId(),
                projectMember.getProject().getId(),
                memberMapper.toResponse(projectMember.getMember()),
                projectMember.getAllocatedAt()
        );
    }

    private RiskClassification calculateRisk(Project project) {
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