package br.com.gabriel.project_portfolio_api.mapper;

import br.com.gabriel.project_portfolio_api.dto.request.MemberCreateRequest;
import br.com.gabriel.project_portfolio_api.dto.response.MemberResponse;
import br.com.gabriel.project_portfolio_api.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member toEntity(MemberCreateRequest request) {
        return Member.builder()
                .name(request.name())
                .role(request.role())
                .build();
    }

    public MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getRole(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
