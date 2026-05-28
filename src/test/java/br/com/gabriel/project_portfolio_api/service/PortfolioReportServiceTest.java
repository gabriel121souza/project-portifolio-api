package br.com.gabriel.project_portfolio_api.service;

import br.com.gabriel.project_portfolio_api.dto.response.PortfolioReportResponse;
import br.com.gabriel.project_portfolio_api.entity.Member;
import br.com.gabriel.project_portfolio_api.entity.Project;
import br.com.gabriel.project_portfolio_api.enums.MemberRole;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.repository.ProjectMemberRepository;
import br.com.gabriel.project_portfolio_api.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioReportServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    private PortfolioReportService portfolioReportService;

    private Member manager;

    @BeforeEach
    void setUp() {
        portfolioReportService = new PortfolioReportService(
                projectRepository,
                projectMemberRepository
        );

        manager = Member.builder()
                .id(1L)
                .name("Ana Souza")
                .role(MemberRole.GERENTE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldGeneratePortfolioSummarySuccessfully() {
        Project projectInAnalysis = buildProject(
                1L,
                ProjectStatus.EM_ANALISE,
                new BigDecimal("100000.00"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 3, 1),
                null
        );

        Project closedProject = buildProject(
                2L,
                ProjectStatus.ENCERRADO,
                new BigDecimal("300000.00"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 10)
        );

        mockCountByStatus();
        mockFindByStatusWithProjects(projectInAnalysis, closedProject);

        when(projectMemberRepository.countDistinctAllocatedMembers()).thenReturn(5L);

        PortfolioReportResponse response = portfolioReportService.generateSummary();

        assertNotNull(response);

        assertEquals(1L, response.projectsByStatus().get(ProjectStatus.EM_ANALISE));
        assertEquals(1L, response.projectsByStatus().get(ProjectStatus.ENCERRADO));

        assertEquals(
                new BigDecimal("100000.00"),
                response.budgetByStatus().get(ProjectStatus.EM_ANALISE)
        );

        assertEquals(
                new BigDecimal("300000.00"),
                response.budgetByStatus().get(ProjectStatus.ENCERRADO)
        );

        assertEquals(99.0, response.averageClosedProjectDurationInDays());
        assertEquals(5L, response.totalUniqueAllocatedMembers());

        verify(projectMemberRepository).countDistinctAllocatedMembers();
    }

    @Test
    void shouldReturnZeroAverageWhenThereAreNoClosedProjects() {
        mockCountByStatus();

        for (ProjectStatus status : ProjectStatus.values()) {
            when(projectRepository.findByStatus(status)).thenReturn(List.of());
        }

        when(projectMemberRepository.countDistinctAllocatedMembers()).thenReturn(0L);

        PortfolioReportResponse response = portfolioReportService.generateSummary();

        assertNotNull(response);
        assertEquals(0.0, response.averageClosedProjectDurationInDays());
        assertEquals(0L, response.totalUniqueAllocatedMembers());
    }

    private void mockCountByStatus() {
        for (ProjectStatus status : ProjectStatus.values()) {
            when(projectRepository.countByStatus(status)).thenReturn(0L);
        }

        when(projectRepository.countByStatus(ProjectStatus.EM_ANALISE)).thenReturn(1L);
        when(projectRepository.countByStatus(ProjectStatus.ENCERRADO)).thenReturn(1L);
    }

    private void mockFindByStatusWithProjects(Project projectInAnalysis, Project closedProject) {
        for (ProjectStatus status : ProjectStatus.values()) {
            when(projectRepository.findByStatus(status)).thenReturn(List.of());
        }

        when(projectRepository.findByStatus(ProjectStatus.EM_ANALISE))
                .thenReturn(List.of(projectInAnalysis));

        when(projectRepository.findByStatus(ProjectStatus.ENCERRADO))
                .thenReturn(List.of(closedProject));
    }

    private Project buildProject(
            Long id,
            ProjectStatus status,
            BigDecimal budget,
            LocalDate startDate,
            LocalDate expectedEndDate,
            LocalDate actualEndDate
    ) {
        return Project.builder()
                .id(id)
                .name("Projeto " + id)
                .startDate(startDate)
                .expectedEndDate(expectedEndDate)
                .actualEndDate(actualEndDate)
                .totalBudget(budget)
                .description("Descrição do projeto")
                .manager(manager)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}