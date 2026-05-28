package br.com.gabriel.project_portfolio_api.controller;

import br.com.gabriel.project_portfolio_api.dto.request.*;
import br.com.gabriel.project_portfolio_api.dto.response.ProjectMemberResponse;
import br.com.gabriel.project_portfolio_api.dto.response.ProjectResponse;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.enums.RiskClassification;
import br.com.gabriel.project_portfolio_api.service.ProjectMemberService;
import br.com.gabriel.project_portfolio_api.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Projetos", description = "Operacoes para cadastro, consulta, atualizacao, exclusao e alocacao de membros em projetos")
@SecurityRequirement(name = "basicAuth")
public class ProjectController {

    private final ProjectService projectService;

    private final ProjectMemberService projectMemberService;



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar projeto",
            description = "Cria um projeto em status inicial EM_ANALISE. A classificacao de risco e calculada dinamicamente na resposta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos ou regra de negocio violada"),
            @ApiResponse(responseCode = "401", description = "Autenticacao obrigatoria"),
            @ApiResponse(responseCode = "404", description = "Gerente informado nao encontrado")
    })
    public ProjectResponse create(@RequestBody @Valid ProjectCreateRequest request) {
        return projectService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar projeto por id", description = "Retorna os dados completos do projeto, incluindo gerente e classificacao de risco.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projeto encontrado"),
            @ApiResponse(responseCode = "401", description = "Autenticacao obrigatoria"),
            @ApiResponse(responseCode = "404", description = "Projeto nao encontrado")
    })
    public ProjectResponse findById(
            @Parameter(description = "Identificador do projeto", example = "1")
            @PathVariable Long id
    ) {
        return projectService.findById(id);
    }

    @GetMapping
    @Operation(
            summary = "Listar projetos",
            description = "Lista projetos com paginacao e filtros opcionais por nome, status, gerente, data de inicio e classificacao de risco."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projetos listados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Autenticacao obrigatoria")
    })
    public Page<ProjectResponse> findAll(
            @Parameter(description = "Filtro parcial por nome do projeto", example = "Sistema")
            @RequestParam(required = false) String name,

            @Parameter(description = "Filtro por status atual do projeto", example = "EM_ANALISE")
            @RequestParam(required = false) ProjectStatus status,

            @Parameter(description = "Filtro pelo id do gerente responsavel", example = "1")
            @RequestParam(required = false) Long managerId,

            @Parameter(description = "Filtro pela data de inicio no formato ISO-8601", example = "2026-06-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "Filtro pela classificacao de risco calculada", example = "MEDIO")
            @RequestParam(required = false) RiskClassification riskClassification,

            @Parameter(description = "Parametros de paginacao e ordenacao")
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
    @Operation(summary = "Atualizar projeto", description = "Atualiza os dados cadastrais do projeto e recalcula o risco na resposta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projeto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos ou regra de negocio violada"),
            @ApiResponse(responseCode = "401", description = "Autenticacao obrigatoria"),
            @ApiResponse(responseCode = "404", description = "Projeto ou gerente nao encontrado")
    })
    public ProjectResponse update(
            @Parameter(description = "Identificador do projeto", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid ProjectUpdateRequest request
    ) {
        return projectService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Atualizar status do projeto",
            description = "Atualiza o status respeitando a sequencia logica. CANCELADO pode ser aplicado a qualquer momento."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Transicao de status invalida"),
            @ApiResponse(responseCode = "401", description = "Autenticacao obrigatoria"),
            @ApiResponse(responseCode = "404", description = "Projeto nao encontrado")
    })
    public ProjectResponse updateStatus(
            @Parameter(description = "Identificador do projeto", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid ProjectStatusUpdateRequest request
    ) {
        return projectService.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir projeto", description = "Exclui o projeto quando o status permite. Projetos INICIADO, EM_ANDAMENTO ou ENCERRADO nao podem ser excluidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Projeto excluido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Projeto nao pode ser excluido no status atual"),
            @ApiResponse(responseCode = "401", description = "Autenticacao obrigatoria"),
            @ApiResponse(responseCode = "404", description = "Projeto nao encontrado")
    })
    public void delete(
            @Parameter(description = "Identificador do projeto", example = "1")
            @PathVariable Long id
    ) {
        projectService.delete(id);
    }

    @PostMapping("/{projectId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Associar membro ao projeto", description = "Associa um membro ao projeto. Apenas membros com atribuicao FUNCIONARIO podem ser alocados.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Membro associado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Membro nao pode ser associado ou limite de alocacao atingido"),
            @ApiResponse(responseCode = "401", description = "Autenticacao obrigatoria"),
            @ApiResponse(responseCode = "404", description = "Projeto ou membro nao encontrado")
    })
    public ProjectMemberResponse addMember(
            @Parameter(description = "Identificador do projeto", example = "1")
            @PathVariable Long projectId,
            @RequestBody @Valid ProjectMemberRequest request
    ) {
        return projectMemberService.addMember(projectId, request);
    }

    @GetMapping("/{projectId}/members")
    @Operation(summary = "Listar membros do projeto", description = "Retorna todos os membros associados ao projeto informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membros listados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Autenticacao obrigatoria"),
            @ApiResponse(responseCode = "404", description = "Projeto nao encontrado")
    })
    public List<ProjectMemberResponse> findMembersByProject(
            @Parameter(description = "Identificador do projeto", example = "1")
            @PathVariable Long projectId
    ) {
        return projectMemberService.findMembersByProject(projectId);
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover membro do projeto", description = "Remove a associacao entre um membro e um projeto.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Membro removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Membro nao esta associado ao projeto"),
            @ApiResponse(responseCode = "401", description = "Autenticacao obrigatoria"),
            @ApiResponse(responseCode = "404", description = "Projeto ou membro nao encontrado")
    })
    public void removeMember(
            @Parameter(description = "Identificador do projeto", example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "Identificador do membro", example = "2")
            @PathVariable Long memberId
    ) {
        projectMemberService.removeMember(projectId, memberId);
    }
}
