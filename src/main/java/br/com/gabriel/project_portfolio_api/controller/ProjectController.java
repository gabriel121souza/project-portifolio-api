package br.com.gabriel.project_portfolio_api.controller;

import br.com.gabriel.project_portfolio_api.dto.request.*;
import br.com.gabriel.project_portfolio_api.dto.response.ProjectMemberResponse;
import br.com.gabriel.project_portfolio_api.dto.response.ProjectResponse;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.enums.RiskClassification;
import br.com.gabriel.project_portfolio_api.service.ProjectMemberService;
import br.com.gabriel.project_portfolio_api.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@AllArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    private final ProjectMemberService projectMemberService;



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@RequestBody @Valid ProjectCreateRequest request) {
        return projectService.create(request);
    }

    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable Long id) {
        return projectService.findById(id);
    }

    @GetMapping
    public Page<ProjectResponse> findAll(
            @RequestParam(required = false) String name,

            @RequestParam(required = false) ProjectStatus status,

            @RequestParam(required = false) Long managerId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false) RiskClassification riskClassification,

            Pageable pageable
    ) {
        ProjectFilterRequest filter = new ProjectFilterRequest(
                name,
                status,
                managerId,
                startDate,
                riskClassification
        );

        return projectService.findAll(filter, pageable);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(
            @PathVariable Long id,
            @RequestBody @Valid ProjectUpdateRequest request
    ) {
        return projectService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public ProjectResponse updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid ProjectStatusUpdateRequest request
    ) {
        return projectService.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }
    @PostMapping("/{projectId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectMemberResponse addMember(
            @PathVariable Long projectId,
            @RequestBody @Valid ProjectMemberRequest request
    ) {
        return projectMemberService.addMember(projectId, request);
    }

    @GetMapping("/{projectId}/members")
    public List<ProjectMemberResponse> findMembersByProject(@PathVariable Long projectId) {
        return projectMemberService.findMembersByProject(projectId);
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId
    ) {
        projectMemberService.removeMember(projectId, memberId);
    }
}