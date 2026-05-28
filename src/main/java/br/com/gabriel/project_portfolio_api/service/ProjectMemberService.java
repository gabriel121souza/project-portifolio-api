package br.com.gabriel.project_portfolio_api.service;

import br.com.gabriel.project_portfolio_api.dto.request.ProjectMemberRequest;
import br.com.gabriel.project_portfolio_api.dto.response.ProjectMemberResponse;
import br.com.gabriel.project_portfolio_api.entity.Member;
import br.com.gabriel.project_portfolio_api.entity.Project;
import br.com.gabriel.project_portfolio_api.entity.ProjectMember;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.exception.BusinessException;
import br.com.gabriel.project_portfolio_api.mapper.ProjectMapper;
import br.com.gabriel.project_portfolio_api.repository.ProjectMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectMemberService {

    private static final int MAX_MEMBERS_PER_PROJECT = 10;
    private static final int MAX_ACTIVE_PROJECTS_PER_MEMBER = 3;

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectService projectService;
    private final MemberExternalService memberExternalService;
    private final ProjectMapper projectMapper;

    public ProjectMemberService(
            ProjectMemberRepository projectMemberRepository,
            ProjectService projectService,
            MemberExternalService memberExternalService,
            ProjectMapper projectMapper
    ) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectService = projectService;
        this.memberExternalService = memberExternalService;
        this.projectMapper = projectMapper;
    }

    @Transactional
    public ProjectMemberResponse addMember(Long projectId, ProjectMemberRequest request) {
        Project project = projectService.findEntityById(projectId);
        Member member = memberExternalService.findEntityById(request.memberId());

        validateMemberCanBeAllocated(project, member);

        ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .member(member)
                .build();

        ProjectMember savedProjectMember = projectMemberRepository.save(projectMember);

        return projectMapper.toProjectMemberResponse(savedProjectMember);
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> findMembersByProject(Long projectId) {
        projectService.findEntityById(projectId);

        return projectMemberRepository.findByProjectId(projectId)
                .stream()
                .map(projectMapper::toProjectMemberResponse)
                .toList();
    }

    @Transactional
    public void removeMember(Long projectId, Long memberId) {
        projectService.findEntityById(projectId);
        memberExternalService.findEntityById(memberId);

        boolean exists = projectMemberRepository.existsByProjectIdAndMemberId(projectId, memberId);

        if (!exists) {
            throw new BusinessException("O membro informado não está associado a este projeto");
        }

        projectMemberRepository.deleteByProjectIdAndMemberId(projectId, memberId);
    }

    private void validateMemberCanBeAllocated(Project project, Member member) {
        if (!member.getRole().canBeAllocatedToProject()) {
            throw new BusinessException("Somente membros com atribuição FUNCIONARIO podem ser associados ao projeto");
        }

        if (projectMemberRepository.existsByProjectIdAndMemberId(project.getId(), member.getId())) {
            throw new BusinessException("Este membro já está associado ao projeto");
        }

        long totalProjectMembers = projectMemberRepository.countByProjectId(project.getId());

        if (totalProjectMembers >= MAX_MEMBERS_PER_PROJECT) {
            throw new BusinessException("O projeto não pode ter mais de 10 membros associados");
        }

        long activeProjects = projectMemberRepository.countActiveProjectsByMemberId(
                member.getId(),
                List.of(ProjectStatus.ENCERRADO, ProjectStatus.CANCELADO)
        );

        if (activeProjects >= MAX_ACTIVE_PROJECTS_PER_MEMBER) {
            throw new BusinessException("O membro não pode estar associado a mais de 3 projetos ativos");
        }
    }
}