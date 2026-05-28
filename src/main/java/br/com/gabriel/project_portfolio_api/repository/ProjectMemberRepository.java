package br.com.gabriel.project_portfolio_api.repository;

import br.com.gabriel.project_portfolio_api.entity.ProjectMember;
import br.com.gabriel.project_portfolio_api.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectIdAndMemberId(Long projectId, Long memberId);

    long countByProjectId(Long projectId);

    List<ProjectMember> findByProjectId(Long projectId);

    void deleteByProjectIdAndMemberId(Long projectId, Long memberId);

    @Query("""
            select count(pm)
            from ProjectMember pm
            where pm.member.id = :memberId
              and pm.project.status not in :inactiveStatuses
            """)
    long countActiveProjectsByMemberId(Long memberId, Collection<ProjectStatus> inactiveStatuses);

    @Query("""
            select count(distinct pm.member.id)
            from ProjectMember pm
            """)
    long countDistinctAllocatedMembers();
}