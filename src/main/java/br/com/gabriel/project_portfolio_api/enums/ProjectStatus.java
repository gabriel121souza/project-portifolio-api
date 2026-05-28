package br.com.gabriel.project_portfolio_api.enums;

public enum ProjectStatus {
    EM_ANALISE,
    ANALISE_REALIZADA,
    ANALISE_APROVADA,
    INICIADO,
    PLANEJADO,
    EM_ANDAMENTO,
    ENCERRADO,
    CANCELADO;

    public boolean canTransitionTo(ProjectStatus nextStatus) {
        if (nextStatus == CANCELADO) {
            return true;
        }

        if (this == CANCELADO || this == ENCERRADO) {
            return false;
        }

        return nextStatus.ordinal() == this.ordinal() + 1;
    }

    public boolean cannotBeDeleted() {
        return this == INICIADO
                || this == EM_ANDAMENTO
                || this == ENCERRADO;
    }

    public boolean isActive() {
        return this != ENCERRADO && this != CANCELADO;
    }
}
