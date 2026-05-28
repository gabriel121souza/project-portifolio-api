package br.com.gabriel.project_portfolio_api.service;

import br.com.gabriel.project_portfolio_api.dto.request.ProjectMemberRequest;
import br.com.gabriel.project_portfolio_api.dto.response.ProjectMemberResponse;
import br.com.gabriel.project_portfolio_api.entity.Member;
import br.com.gabriel.project_portfolio_api.entity.Project;
import br.com.gabriel.project_portfolio_api.entity.ProjectMember;
import br.com.gabriel.project_portfolio_api.enums.MemberRole;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.exception.BusinessException;
import br.com.gabriel.project_portfolio_api.mapper.MemberMapper;
import br.com.gabriel.project_portfolio_api.mapper.ProjectMapper;
import br.com.gabriel.project_portfolio_api.repository.ProjectMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectMemberServiceTest {

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private MemberExternalService memberExternalService;

    @Spy
    private MemberMapper memberMapper;

    private ProjectMapper projectMapper;

    private ProjectMemberService projectMemberService;

    private Project project;
    private Member manager;
    private Member employee;
    private ProjectMember projectMember;

    @BeforeEach
    void setUp() {
        projectMapper = new ProjectMapper(memberMapper);

        projectMemberService = new ProjectMemberService(
                projectMemberRepository,
                projectService,
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

        employee = Member.builder()
                .id(2L)
                .name("Carlos Lima")
                .role(MemberRole.FUNCIONARIO)
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

        projectMember = ProjectMember.builder()
                .id(1L)
                .project(project)
                .member(employee)
                .allocatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldAddEmployeeToProjectSuccessfully() {
        ProjectMemberRequest request = new ProjectMemberRequest(2L);

        when(projectService.findEntityById(1L)).thenReturn(project);
        when(memberExternalService.findEntityById(2L)).thenReturn(employee);
        when(projectMemberRepository.existsByProjectIdAndMemberId(1L, 2L)).thenReturn(false);
        when(projectMemberRepository.countByProjectId(1L)).thenReturn(0L);
        when(projectMemberRepository.countActiveProjectsByMemberId(
                eq(2L),
                eq(List.of(ProjectStatus.ENCERRADO, ProjectStatus.CANCELADO))
        )).thenReturn(0L);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(projectMember);

        ProjectMemberResponse response = projectMemberService.addMember(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.projectId());
        assertEquals(2L, response.member().id());
        assertEquals(MemberRole.FUNCIONARIO, response.member().role());

        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    void shouldThrowExceptionWhenMemberIsNotEmployee() {
        ProjectMemberRequest request = new ProjectMemberRequest(1L);

        when(projectService.findEntityById(1L)).thenReturn(project);
        when(memberExternalService.findEntityById(1L)).thenReturn(manager);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> projectMemberService.addMember(1L, request)
        );

        assertEquals(
                "Somente membros com atribuição FUNCIONARIO podem ser associados ao projeto",
                exception.getMessage()
        );

        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    void shouldThrowExceptionWhenMemberAlreadyAssociatedToProject() {
        ProjectMemberRequest request = new ProjectMemberRequest(2L);

        when(projectService.findEntityById(1L)).thenReturn(project);
        when(memberExternalService.findEntityById(2L)).thenReturn(employee);
        when(projectMemberRepository.existsByProjectIdAndMemberId(1L, 2L)).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> projectMemberService.addMember(1L, request)
        );

        assertEquals("Este membro já está associado ao projeto", exception.getMessage());

        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    void shouldThrowExceptionWhenProjectAlreadyHasTenMembers() {
        ProjectMemberRequest request = new ProjectMemberRequest(2L);

        when(projectService.findEntityById(1L)).thenReturn(project);
        when(memberExternalService.findEntityById(2L)).thenReturn(employee);
        when(projectMemberRepository.existsByProjectIdAndMemberId(1L, 2L)).thenReturn(false);
        when(projectMemberRepository.countByProjectId(1L)).thenReturn(10L);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> projectMemberService.addMember(1L, request)
        );

        assertEquals("O projeto não pode ter mais de 10 membros associados", exception.getMessage());

        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    void shouldThrowExceptionWhenMemberHasThreeActiveProjects() {
        ProjectMemberRequest request = new ProjectMemberRequest(2L);

        when(projectService.findEntityById(1L)).thenReturn(project);
        when(memberExternalService.findEntityById(2L)).thenReturn(employee);
        when(projectMemberRepository.existsByProjectIdAndMemberId(1L, 2L)).thenReturn(false);
        when(projectMemberRepository.countByProjectId(1L)).thenReturn(2L);
        when(projectMemberRepository.countActiveProjectsByMemberId(
                eq(2L),
                eq(List.of(ProjectStatus.ENCERRADO, ProjectStatus.CANCELADO))
        )).thenReturn(3L);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> projectMemberService.addMember(1L, request)
        );

        assertEquals("O membro não pode estar associado a mais de 3 projetos ativos", exception.getMessage());

        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    void shouldFindMembersByProjectSuccessfully() {
        when(projectService.findEntityById(1L)).thenReturn(project);
        when(projectMemberRepository.findByProjectId(1L)).thenReturn(List.of(projectMember));

        List<ProjectMemberResponse> response = projectMemberService.findMembersByProject(1L);

        assertEquals(1, response.size());
        assertEquals(2L, response.get(0).member().id());
        assertEquals("Carlos Lima", response.get(0).member().name());

        verify(projectService).findEntityById(1L);
        verify(projectMemberRepository).findByProjectId(1L);
    }

    @Test
    void shouldRemoveMemberSuccessfully() {
        when(projectService.findEntityById(1L)).thenReturn(project);
        when(memberExternalService.findEntityById(2L)).thenReturn(employee);
        when(projectMemberRepository.existsByProjectIdAndMemberId(1L, 2L)).thenReturn(true);

        projectMemberService.removeMember(1L, 2L);

        verify(projectMemberRepository).deleteByProjectIdAndMemberId(1L, 2L);
    }

    @Test
    void shouldThrowExceptionWhenRemovingMemberNotAssociatedToProject() {
        when(projectService.findEntityById(1L)).thenReturn(project);
        when(memberExternalService.findEntityById(2L)).thenReturn(employee);
        when(projectMemberRepository.existsByProjectIdAndMemberId(1L, 2L)).thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> projectMemberService.removeMember(1L, 2L)
        );

        assertEquals("O membro informado não está associado a este projeto", exception.getMessage());

        verify(projectMemberRepository, never()).deleteByProjectIdAndMemberId(anyLong(), anyLong());
    }
}