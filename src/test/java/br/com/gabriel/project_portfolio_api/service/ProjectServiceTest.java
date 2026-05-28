package br.com.gabriel.project_portfolio_api.service;

import br.com.gabriel.project_portfolio_api.dto.request.ProjectCreateRequest;
import br.com.gabriel.project_portfolio_api.dto.request.ProjectFilterRequest;
import br.com.gabriel.project_portfolio_api.dto.request.ProjectStatusUpdateRequest;
import br.com.gabriel.project_portfolio_api.dto.request.ProjectUpdateRequest;
import br.com.gabriel.project_portfolio_api.dto.response.ProjectResponse;
import br.com.gabriel.project_portfolio_api.entity.Member;
import br.com.gabriel.project_portfolio_api.entity.Project;
import br.com.gabriel.project_portfolio_api.enums.MemberRole;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.enums.RiskClassification;
import br.com.gabriel.project_portfolio_api.exception.BusinessException;
import br.com.gabriel.project_portfolio_api.exception.ResourceNotFoundException;
import br.com.gabriel.project_portfolio_api.mapper.MemberMapper;
import br.com.gabriel.project_portfolio_api.mapper.ProjectMapper;
import br.com.gabriel.project_portfolio_api.repository.ProjectRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberExternalService memberExternalService;

    @Spy
    private MemberMapper memberMapper;

    private ProjectMapper projectMapper;

    private ProjectService projectService;

    private Member manager;
    private Project project;

    @BeforeEach
    void setUp() {
        projectMapper = new ProjectMapper(memberMapper);

        projectService = new ProjectService(
                projectRepository,
                memberExternalService,
                projectMapper
        );

        manager = Member.builder()
                .id(1L)
                .name("Ana Souza")
                .role(MemberRole.GERENTE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        project = Project.builder()
                .id(1L)
                .name("Sistema de Gestão")
                .startDate(LocalDate.of(2026, 6, 1))
                .expectedEndDate(LocalDate.of(2026, 9, 1))
                .actualEndDate(null)
                .totalBudget(new BigDecimal("95000.00"))
                .description("Projeto para gestão de portfólio")
                .manager(manager)
                .status(ProjectStatus.EM_ANALISE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateProjectSuccessfully() {
        ProjectCreateRequest request = new ProjectCreateRequest(
                "Sistema de Gestão",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 9, 1),
                null,
                new BigDecimal("95000.00"),
                "Projeto para gestão de portfólio",
                1L
        );

        when(memberExternalService.findEntityById(1L)).thenReturn(manager);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project savedProject = invocation.getArgument(0);
            savedProject.setId(1L);
            savedProject.setCreatedAt(LocalDateTime.now());
            savedProject.setUpdatedAt(LocalDateTime.now());
            return savedProject;
        });

        ProjectResponse response = projectService.create(request);

        assertNotNull(response);
        assertEquals("Sistema de Gestão", response.name());
        assertEquals(ProjectStatus.EM_ANALISE, response.status());
        assertEquals(1L, response.manager().id());

        verify(memberExternalService).findEntityById(1L);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void shouldThrowExceptionWhenExpectedEndDateIsBeforeStartDate() {
        ProjectCreateRequest request = new ProjectCreateRequest(
                "Projeto inválido",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 5, 1),
                null,
                new BigDecimal("95000.00"),
                "Projeto com datas inválidas",
                1L
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> projectService.create(request)
        );

        assertEquals("A previsão de término não pode ser anterior à data de início", exception.getMessage());

        verifyNoInteractions(projectRepository);
    }

    @Test
    void shouldThrowExceptionWhenActualEndDateIsBeforeStartDate() {
        ProjectCreateRequest request = new ProjectCreateRequest(
                "Projeto invÃ¡lido",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 5, 31),
                new BigDecimal("95000.00"),
                "Projeto com data real invÃ¡lida",
                1L
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> projectService.create(request)
        );

        assertEquals("A data real de término não pode ser anterior à data de início", exception.getMessage());

        verifyNoInteractions(memberExternalService);
        verifyNoInteractions(projectRepository);
    }

    @Test
    void shouldFindProjectByIdSuccessfully() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.findById(1L);

        assertEquals(1L, response.id());
        assertEquals(project.getName(), response.name());
        assertEquals(RiskClassification.MEDIO, response.riskClassification());
    }

    @Test
    void shouldThrowExceptionWhenProjectDoesNotExist() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> projectService.findEntityById(99L)
        );

        assertEquals("Projeto não encontrado com id: 99", exception.getMessage());
    }

    @Test
    void shouldFindAllProjectsWithoutRiskFilter() {
        ProjectFilterRequest filter = new ProjectFilterRequest(null, null, null, null, null);
        PageRequest pageable = PageRequest.of(0, 10);

        when(projectRepository.findAll(anySpecification(), eq(pageable))).thenReturn(new PageImpl<>(List.of(project)));

        Page<ProjectResponse> response = projectService.findAll(filter, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(project.getName(), response.getContent().get(0).name());
    }

    @Test
    void shouldFindAllProjectsFilteringByRisk() {
        Project lowRiskProject = Project.builder()
                .id(2L)
                .name("Projeto pequeno")
                .startDate(LocalDate.of(2026, 6, 1))
                .expectedEndDate(LocalDate.of(2026, 7, 1))
                .totalBudget(new BigDecimal("50000.00"))
                .description("Projeto de baixo risco")
                .manager(manager)
                .status(ProjectStatus.EM_ANALISE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ProjectFilterRequest filter = new ProjectFilterRequest(null, null, null, null, RiskClassification.BAIXO);
        PageRequest pageable = PageRequest.of(0, 10);

        when(projectRepository.findAll(anySpecification(), eq(pageable))).thenReturn(new PageImpl<>(List.of(project, lowRiskProject)));

        Page<ProjectResponse> response = projectService.findAll(filter, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("Projeto pequeno", response.getContent().get(0).name());
        assertEquals(RiskClassification.BAIXO, response.getContent().get(0).riskClassification());
    }

    @Test
    void shouldUpdateProjectSuccessfully() {
        ProjectUpdateRequest request = new ProjectUpdateRequest(
                "Sistema atualizado",
                LocalDate.of(2026, 6, 2),
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 12, 20),
                new BigDecimal("250000.00"),
                "DescriÃ§Ã£o atualizada",
                1L
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberExternalService.findEntityById(1L)).thenReturn(manager);
        when(projectRepository.save(project)).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse response = projectService.update(1L, request);

        assertEquals("Sistema atualizado", response.name());
        assertEquals(new BigDecimal("250000.00"), response.totalBudget());
        assertEquals(LocalDate.of(2026, 12, 20), response.actualEndDate());

        verify(projectRepository).save(project);
    }

    @Test
    void shouldUpdateStatusToNextStepSuccessfully() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectStatusUpdateRequest request = new ProjectStatusUpdateRequest(ProjectStatus.ANALISE_REALIZADA);

        ProjectResponse response = projectService.updateStatus(1L, request);

        assertEquals(ProjectStatus.ANALISE_REALIZADA, response.status());

        verify(projectRepository).findById(1L);
        verify(projectRepository).save(project);
    }

    @Test
    void shouldThrowExceptionWhenSkippingStatusStep() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectStatusUpdateRequest request = new ProjectStatusUpdateRequest(ProjectStatus.INICIADO);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> projectService.updateStatus(1L, request)
        );

        assertTrue(exception.getMessage().contains("Transição de status inválida"));

        verify(projectRepository).findById(1L);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void shouldAllowCancelAtAnyTime() {
        project.setStatus(ProjectStatus.EM_ANDAMENTO);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectStatusUpdateRequest request = new ProjectStatusUpdateRequest(ProjectStatus.CANCELADO);

        ProjectResponse response = projectService.updateStatus(1L, request);

        assertEquals(ProjectStatus.CANCELADO, response.status());

        verify(projectRepository).save(project);
    }

    @Test
    void shouldThrowExceptionWhenDeletingStartedProject() {
        project.setStatus(ProjectStatus.INICIADO);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> projectService.delete(1L)
        );

        assertEquals("Não é permitido excluir projeto com status: INICIADO", exception.getMessage());

        verify(projectRepository).findById(1L);
        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    void shouldDeleteProjectWhenStatusAllowsDeletion() {
        project.setStatus(ProjectStatus.EM_ANALISE);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.delete(1L);

        verify(projectRepository).findById(1L);
        verify(projectRepository).delete(project);
    }

    private Specification<Project> anySpecification() {
        return any();
    }
}
