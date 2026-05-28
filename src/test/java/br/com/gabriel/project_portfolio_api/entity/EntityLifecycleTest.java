package br.com.gabriel.project_portfolio_api.entity;

import br.com.gabriel.project_portfolio_api.enums.MemberRole;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityLifecycleTest {

    @Test
    void shouldSetMemberTimestampsOnPersistAndUpdate() {
        Member member = Member.builder()
                .name("Ana Souza")
                .role(MemberRole.GERENTE)
                .build();

        member.prePersist();

        assertNotNull(member.getCreatedAt());
        assertNotNull(member.getUpdatedAt());
        assertEquals(member.getCreatedAt(), member.getUpdatedAt());

        LocalDateTime firstUpdatedAt = member.getUpdatedAt();
        member.preUpdate();

        assertTrue(member.getUpdatedAt().isAfter(firstUpdatedAt) || member.getUpdatedAt().isEqual(firstUpdatedAt));
    }

    @Test
    void shouldSetProjectTimestampsAndDefaultStatusOnPersist() {
        Project project = Project.builder()
                .name("Sistema de Gestão")
                .startDate(LocalDate.of(2026, 6, 1))
                .expectedEndDate(LocalDate.of(2026, 9, 1))
                .totalBudget(new BigDecimal("95000.00"))
                .description("Projeto para gestão de portfólio")
                .manager(Member.builder().id(1L).build())
                .build();

        project.prePersist();

        assertNotNull(project.getCreatedAt());
        assertNotNull(project.getUpdatedAt());
        assertEquals(ProjectStatus.EM_ANALISE, project.getStatus());
    }

    @Test
    void shouldKeepProjectStatusWhenAlreadyDefinedOnPersist() {
        Project project = Project.builder()
                .status(ProjectStatus.INICIADO)
                .build();

        project.prePersist();

        assertEquals(ProjectStatus.INICIADO, project.getStatus());
    }

    @Test
    void shouldUpdateProjectTimestampOnUpdate() {
        Project project = Project.builder()
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
        LocalDateTime previousUpdatedAt = project.getUpdatedAt();

        project.preUpdate();

        assertTrue(project.getUpdatedAt().isAfter(previousUpdatedAt));
    }

    @Test
    void shouldSetProjectMemberAllocationDateOnPersist() {
        ProjectMember projectMember = ProjectMember.builder().build();

        projectMember.prePersist();

        assertNotNull(projectMember.getAllocatedAt());
    }
}
