package br.com.gabriel.project_portfolio_api.service;

import br.com.gabriel.project_portfolio_api.dto.request.MemberCreateRequest;
import br.com.gabriel.project_portfolio_api.dto.response.MemberResponse;
import br.com.gabriel.project_portfolio_api.entity.Member;
import br.com.gabriel.project_portfolio_api.enums.MemberRole;
import br.com.gabriel.project_portfolio_api.exception.ResourceNotFoundException;
import br.com.gabriel.project_portfolio_api.mapper.MemberMapper;
import br.com.gabriel.project_portfolio_api.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberExternalServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private MemberMapper memberMapper;

    private MemberExternalService memberExternalService;

    private Member member;

    @BeforeEach
    void setUp() {
        memberExternalService = new MemberExternalService(memberRepository, memberMapper);

        member = Member.builder()
                .id(1L)
                .name("Ana Souza")
                .role(MemberRole.GERENTE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateMemberSuccessfully() {
        MemberCreateRequest request = new MemberCreateRequest("Ana Souza", MemberRole.GERENTE);

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberResponse response = memberExternalService.create(request);

        assertEquals(1L, response.id());
        assertEquals("Ana Souza", response.name());
        assertEquals(MemberRole.GERENTE, response.role());

        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void shouldFindMemberByIdSuccessfully() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MemberResponse response = memberExternalService.findById(1L);

        assertEquals(1L, response.id());
        assertEquals("Ana Souza", response.name());
    }

    @Test
    void shouldFindAllMembersSuccessfully() {
        Member employee = Member.builder()
                .id(2L)
                .name("Carlos Lima")
                .role(MemberRole.FUNCIONARIO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(memberRepository.findAll()).thenReturn(List.of(member, employee));

        List<MemberResponse> response = memberExternalService.findAll();

        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).id());
        assertEquals(2L, response.get(1).id());
    }

    @Test
    void shouldFindMemberEntityByIdSuccessfully() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Member response = memberExternalService.findEntityById(1L);

        assertSame(member, response);
    }

    @Test
    void shouldThrowExceptionWhenMemberDoesNotExist() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> memberExternalService.findEntityById(99L)
        );

        assertEquals("Membro nÃ£o encontrado com id: 99", exception.getMessage());
    }
}
