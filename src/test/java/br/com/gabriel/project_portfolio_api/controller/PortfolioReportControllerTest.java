package br.com.gabriel.project_portfolio_api.controller;

import br.com.gabriel.project_portfolio_api.dto.response.PortfolioReportResponse;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import br.com.gabriel.project_portfolio_api.service.PortfolioReportService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PortfolioReportControllerTest {

    @Test
    void shouldGeneratePortfolioSummary() {
        PortfolioReportService portfolioReportService = mock(PortfolioReportService.class);
        PortfolioReportController controller = new PortfolioReportController(portfolioReportService);
        PortfolioReportResponse expected = new PortfolioReportResponse(
                Map.of(ProjectStatus.EM_ANALISE, 1L),
                Map.of(ProjectStatus.EM_ANALISE, BigDecimal.TEN),
                10.0,
                2L
        );

        when(portfolioReportService.generateSummary()).thenReturn(expected);

        PortfolioReportResponse response = controller.generateSummary();

        assertEquals(expected, response);
        verify(portfolioReportService).generateSummary();
    }
}
