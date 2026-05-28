package br.com.gabriel.project_portfolio_api.enums;

public enum MemberRole {
    FUNCIONARIO,
    GERENTE,
    COORDENADOR,
    TERCEIRIZADO;

    public boolean canBeAllocatedToProject() {
        return this == FUNCIONARIO;
    }
}