package br.com.gabriel.project_portfolio_api.controller;

import br.com.gabriel.project_portfolio_api.dto.request.MemberCreateRequest;
import br.com.gabriel.project_portfolio_api.dto.response.MemberResponse;
import br.com.gabriel.project_portfolio_api.service.MemberExternalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/external-api/members")
@Tag(name = "Membros - API Externa Mockada", description = "Endpoints publicos que simulam uma API externa para cadastro e consulta de membros")
public class MemberExternalController {

    private final MemberExternalService memberExternalService;

    public MemberExternalController(MemberExternalService memberExternalService) {
        this.memberExternalService = memberExternalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar membro", description = "Cria um membro na API externa mockada informando nome e atribuicao.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Membro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos")
    })
    public MemberResponse create(@RequestBody @Valid MemberCreateRequest request) {
        return memberExternalService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar membro por id", description = "Consulta um membro cadastrado na API externa mockada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membro encontrado"),
            @ApiResponse(responseCode = "404", description = "Membro nao encontrado")
    })
    public MemberResponse findById(
            @Parameter(description = "Identificador do membro", example = "1")
            @PathVariable Long id
    ) {
        return memberExternalService.findById(id);
    }

    @GetMapping
    @Operation(summary = "Listar membros", description = "Retorna todos os membros cadastrados na API externa mockada.")
    @ApiResponse(responseCode = "200", description = "Membros listados com sucesso")
    public List<MemberResponse> findAll() {
        return memberExternalService.findAll();
    }
}
