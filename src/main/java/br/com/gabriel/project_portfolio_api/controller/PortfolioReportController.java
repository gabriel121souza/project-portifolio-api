package br.com.gabriel.project_portfolio_api.controller;

import br.com.gabriel.project_portfolio_api.dto.response.PortfolioReportResponse;
import br.com.gabriel.project_portfolio_api.service.PortfolioReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Relatorios", description = "Endpoints para consolidacao de informacoes do portfolio")
@SecurityRequirement(name = "basicAuth")
public class PortfolioReportController {

    private final PortfolioReportService portfolioReportService;

    public PortfolioReportController(PortfolioReportService portfolioReportService) {
        this.portfolioReportService = portfolioReportService;
    }

    @GetMapping("/portfolio-summary")
    @Operation(
            summary = "Gerar resumo do portfolio",
            description = "Retorna quantidade de projetos por status, total orcado por status, media de duracao dos projetos encerrados e total de membros unicos alocados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatorio gerado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Autenticacao obrigatoria")
    })
    public PortfolioReportResponse generateSummary() {
        return portfolioReportService.generateSummary();
    }
}
