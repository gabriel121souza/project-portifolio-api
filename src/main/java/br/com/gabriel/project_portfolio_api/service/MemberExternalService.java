package br.com.gabriel.project_portfolio_api.service;

import br.com.gabriel.project_portfolio_api.dto.request.MemberCreateRequest;
import br.com.gabriel.project_portfolio_api.dto.response.MemberResponse;
import br.com.gabriel.project_portfolio_api.entity.Member;
import br.com.gabriel.project_portfolio_api.exception.ResourceNotFoundException;
import br.com.gabriel.project_portfolio_api.mapper.MemberMapper;
import br.com.gabriel.project_portfolio_api.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberExternalService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public MemberExternalService(
            MemberRepository memberRepository,
            MemberMapper memberMapper
    ) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
    }

    @Transactional
    public MemberResponse create(MemberCreateRequest request) {
        Member member = memberMapper.toEntity(request);
        Member savedMember = memberRepository.save(member);

        return memberMapper.toResponse(savedMember);
    }

    @Transactional(readOnly = true)
    public MemberResponse findById(Long id) {
        Member member = findEntityById(id);
        return memberMapper.toResponse(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        return memberRepository.findAll()
                .stream()
                .map(memberMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Member findEntityById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado com id: " + id));
    }
}