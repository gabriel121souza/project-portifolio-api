package br.com.gabriel.project_portfolio_api.controller;

import br.com.gabriel.project_portfolio_api.dto.request.ProjectCreateRequest;
import br.com.gabriel.project_portfolio_api.dto.request.ProjectFilterRequest;
import br.com.gabriel.project_portfolio_api.dto.request.ProjectMemberRequest;
import br.com.gabriel.project_portfolio_api.dto.request.ProjectStatusUpdateRequest;
import br.com.gabriel.project_portfolio_api.dto.request.ProjectUpdateRequest;
import br.com.gabriel.project_portfolio_api.dto.response.MemberResponse;
import br.com.gabriel.project_portfolio_api.dto.response.ProjectMemberResponse;
import br.com.gabriel.project_portfolio_api.dto.response.ProjectResponse;
import br.com.gabriel.project_portfolio_api.enums.MemberRole;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.enums.RiskClassification;
import br.com.gabriel.project_portfolio_api.service.ProjectMemberService;
import br.com.gabriel.project_portfolio_api.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectControllerTest {

    private ProjectService projectService;
    private ProjectMemberService projectMemberService;
    private ProjectController controller;
    private ProjectResponse projectResponse;
    private ProjectMemberResponse projectMemberResponse;

    @BeforeEach
    void setUp() {
        projectService = mock(ProjectService.class);
        projectMemberService = mock(ProjectMemberService.class);
        controller = new ProjectController(projectService, projectMemberService);

        MemberResponse manager = new MemberResponse(
                1L,
                "Ana Souza",
                MemberRole.GERENTE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        projectResponse = new ProjectResponse(
                1L,
                "Sistema de Gestão",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 9, 1),
                null,
                new BigDecimal("95000.00"),
                "Projeto para gestão de portfólio",
                manager,
                ProjectStatus.EM_ANALISE,
                RiskClassification.MEDIO,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        projectMemberResponse = new ProjectMemberResponse(
                10L,
                1L,
                new MemberResponse(2L, "Carlos Lima", MemberRole.FUNCIONARIO, LocalDateTime.now(), LocalDateTime.now()),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateProject() {
        ProjectCreateRequest request = new ProjectCreateRequest(
                "Sistema de Gestão",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 9, 1),
                null,
                new BigDecimal("95000.00"),
                "Projeto para gestão de portfólio",
                1L
        );
        when(projectService.create(request)).thenReturn(projectResponse);

        ProjectResponse response = controller.create(request);

        assertEquals(projectResponse, response);
        verify(projectService).create(request);
    }

    @Test
    void shouldFindProjectById() {
        when(projectService.findById(1L)).thenReturn(projectResponse);

        ProjectResponse response = controller.findById(1L);

        assertEquals(projectResponse, response);
        verify(projectService).findById(1L);
    }

    @Test
    void shouldFindAllProjectsWithFilter() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ProjectResponse> expected = new PageImpl<>(List.of(projectResponse), pageable, 1);
        ArgumentCaptor<ProjectFilterRequest> filterCaptor = ArgumentCaptor.forClass(ProjectFilterRequest.class);

        when(projectService.findAll(filterCaptor.capture(), org.mockito.Mockito.eq(pageable))).thenReturn(expected);

        Page<ProjectResponse> response = controller.findAll(
                "Sistema",
                ProjectStatus.EM_ANALISE,
                1L,
                LocalDate.of(2026, 6, 1),
                RiskClassification.MEDIO,
                pageable
        );

        assertEquals(expected, response);
        ProjectFilterRequest filter = filterCaptor.getValue();
        assertEquals("Sistema", filter.name());
        assertEquals(ProjectStatus.EM_ANALISE, filter.status());
        assertEquals(1L, filter.managerId());
        assertEquals(LocalDate.of(2026, 6, 1), filter.startDate());
        assertEquals(RiskClassification.MEDIO, filter.riskClassification());
    }

    @Test
    void shouldUpdateProject() {
        ProjectUpdateRequest request = new ProjectUpdateRequest(
                "Sistema atualizado",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 12, 1),
                null,
                new BigDecimal("120000.00"),
                "Descrição atualizada",
                1L
        );
        when(projectService.update(1L, request)).thenReturn(projectResponse);

        ProjectResponse response = controller.update(1L, request);

        assertEquals(projectResponse, response);
        verify(projectService).update(1L, request);
    }

    @Test
    void shouldUpdateProjectStatus() {
        ProjectStatusUpdateRequest request = new ProjectStatusUpdateRequest(ProjectStatus.ANALISE_REALIZADA);
        when(projectService.updateStatus(1L, request)).thenReturn(projectResponse);

        ProjectResponse response = controller.updateStatus(1L, request);

        assertEquals(projectResponse, response);
        verify(projectService).updateStatus(1L, request);
    }

    @Test
    void shouldDeleteProject() {
        controller.delete(1L);

        verify(projectService).delete(1L);
    }

    @Test
    void shouldAddMemberToProject() {
        ProjectMemberRequest request = new ProjectMemberRequest(2L);
        when(projectMemberService.addMember(1L, request)).thenReturn(projectMemberResponse);

        ProjectMemberResponse response = controller.addMember(1L, request);

        assertEquals(projectMemberResponse, response);
        verify(projectMemberService).addMember(1L, request);
    }

    @Test
    void shouldFindMembersByProject() {
        when(projectMemberService.findMembersByProject(1L)).thenReturn(List.of(projectMemberResponse));

        List<ProjectMemberResponse> response = controller.findMembersByProject(1L);

        assertEquals(List.of(projectMemberResponse), response);
        verify(projectMemberService).findMembersByProject(1L);
    }

    @Test
    void shouldRemoveMemberFromProject() {
        controller.removeMember(1L, 2L);

        verify(projectMemberService).removeMember(1L, 2L);
    }
}
