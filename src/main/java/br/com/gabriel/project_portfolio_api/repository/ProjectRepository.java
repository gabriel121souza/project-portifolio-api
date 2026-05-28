package br.com.gabriel.project_portfolio_api.repository;

import br.com.gabriel.project_portfolio_api.entity.Project;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    long countByStatus(ProjectStatus status);

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByStatusIn(List<ProjectStatus> statuses);
}