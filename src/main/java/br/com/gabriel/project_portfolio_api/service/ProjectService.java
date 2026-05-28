package br.com.gabriel.project_portfolio_api.service;

import br.com.gabriel.project_portfolio_api.dto.request.ProjectCreateRequest;
import br.com.gabriel.project_portfolio_api.dto.request.ProjectFilterRequest;
import br.com.gabriel.project_portfolio_api.dto.request.ProjectStatusUpdateRequest;
import br.com.gabriel.project_portfolio_api.dto.request.ProjectUpdateRequest;
import br.com.gabriel.project_portfolio_api.dto.response.ProjectResponse;
import br.com.gabriel.project_portfolio_api.entity.Member;
import br.com.gabriel.project_portfolio_api.entity.Project;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.exception.BusinessException;
import br.com.gabriel.project_portfolio_api.exception.ResourceNotFoundException;
import br.com.gabriel.project_portfolio_api.mapper.ProjectMapper;
import br.com.gabriel.project_portfolio_api.repository.ProjectRepository;
import br.com.gabriel.project_portfolio_api.specification.ProjectSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberExternalService memberExternalService;
    private final ProjectMapper projectMapper;

    public ProjectService(
            ProjectRepository projectRepository,
            MemberExternalService memberExternalService,
            ProjectMapper projectMapper
    ) {
        this.projectRepository = projectRepository;
        this.memberExternalService = memberExternalService;
        this.projectMapper = projectMapper;
    }

    @Transactional
    public ProjectResponse create(ProjectCreateRequest request) {
        validateProjectDates(request.startDate(), request.expectedEndDate(), request.actualEndDate());

        Member manager = memberExternalService.findEntityById(request.managerId());

        Project project = projectMapper.toEntity(request, manager);
        Project savedProject = projectRepository.save(project);

        return projectMapper.toResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        Project project = findEntityById(id);
        return projectMapper.toResponse(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> findAll(ProjectFilterRequest filter, Pageable pageable) {
        Page<Project> projects = projectRepository.findAll(
                ProjectSpecification.withFilters(filter),
                pageable
        );

        if (filter.riskClassification() == null) {
            return projects.map(projectMapper::toResponse);
        }

        List<ProjectResponse> filteredByRisk = projects.getContent()
                .stream()
                .filter(project -> ProjectSpecification.matchesRisk(project, filter.riskClassification()))
                .map(projectMapper::toResponse)
                .toList();

        return new org.springframework.data.domain.PageImpl<>(
                filteredByRisk,
                pageable,
                filteredByRisk.size()
        );
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectUpdateRequest request) {
        validateProjectDates(request.startDate(), request.expectedEndDate(), request.actualEndDate());

        Project project = findEntityById(id);
        Member manager = memberExternalService.findEntityById(request.managerId());

        projectMapper.updateEntity(project, request, manager);

        Project updatedProject = projectRepository.save(project);

        return projectMapper.toResponse(updatedProject);
    }

    @Transactional
    public ProjectResponse updateStatus(Long id, ProjectStatusUpdateRequest request) {
        Project project = findEntityById(id);
        ProjectStatus currentStatus = project.getStatus();
        ProjectStatus nextStatus = request.status();

        if (!currentStatus.canTransitionTo(nextStatus)) {
            throw new BusinessException(
                    "Transição de status inválida. Status atual: "
                            + currentStatus
                            + ", novo status: "
                            + nextStatus
            );
        }

        project.setStatus(nextStatus);

        Project updatedProject = projectRepository.save(project);

        return projectMapper.toResponse(updatedProject);
    }

    @Transactional
    public void delete(Long id) {
        Project project = findEntityById(id);

        if (project.getStatus().cannotBeDeleted()) {
            throw new BusinessException(
                    "Não é permitido excluir projeto com status: " + project.getStatus()
            );
        }

        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public Project findEntityById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com id: " + id));
    }

    private void validateProjectDates(
            java.time.LocalDate startDate,
            java.time.LocalDate expectedEndDate,
            java.time.LocalDate actualEndDate
    ) {
        if (expectedEndDate.isBefore(startDate)) {
            throw new BusinessException("A previsão de término não pode ser anterior à data de início");
        }

        if (actualEndDate != null && actualEndDate.isBefore(startDate)) {
            throw new BusinessException("A data real de término não pode ser anterior à data de início");
        }
    }
}