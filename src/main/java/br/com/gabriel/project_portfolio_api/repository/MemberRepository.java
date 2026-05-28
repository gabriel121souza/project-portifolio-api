package br.com.gabriel.project_portfolio_api.repository;

import br.com.gabriel.project_portfolio_api.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}