package br.com.gabriel.project_portfolio_api.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectStatusTest {

    @Test
    void shouldAllowOnlyNextStatusTransition() {
        assertTrue(ProjectStatus.EM_ANALISE.canTransitionTo(ProjectStatus.ANALISE_REALIZADA));
        assertFalse(ProjectStatus.EM_ANALISE.canTransitionTo(ProjectStatus.INICIADO));
    }

    @Test
    void shouldAllowCancelTransitionFromAnyNonFinalStatus() {
        assertTrue(ProjectStatus.EM_ANDAMENTO.canTransitionTo(ProjectStatus.CANCELADO));
    }

    @Test
    void shouldNotAllowTransitionsFromFinalStatusesExceptCancelRule() {
        assertFalse(ProjectStatus.ENCERRADO.canTransitionTo(ProjectStatus.EM_ANDAMENTO));
        assertFalse(ProjectStatus.CANCELADO.canTransitionTo(ProjectStatus.EM_ANALISE));
    }

    @Test
    void shouldIdentifyStatusesThatCannotBeDeleted() {
        assertTrue(ProjectStatus.INICIADO.cannotBeDeleted());
        assertTrue(ProjectStatus.EM_ANDAMENTO.cannotBeDeleted());
        assertTrue(ProjectStatus.ENCERRADO.cannotBeDeleted());
        assertFalse(ProjectStatus.EM_ANALISE.cannotBeDeleted());
    }

    @Test
    void shouldIdentifyActiveStatuses() {
        assertTrue(ProjectStatus.EM_ANDAMENTO.isActive());
        assertFalse(ProjectStatus.ENCERRADO.isActive());
        assertFalse(ProjectStatus.CANCELADO.isActive());
    }
}
