package br.com.gabriel.project_portfolio_api.controller;

import br.com.gabriel.project_portfolio_api.dto.request.MemberCreateRequest;
import br.com.gabriel.project_portfolio_api.dto.response.MemberResponse;
import br.com.gabriel.project_portfolio_api.enums.MemberRole;
import br.com.gabriel.project_portfolio_api.service.MemberExternalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberExternalControllerTest {

    private MemberExternalService memberExternalService;
    private MemberExternalController controller;
    private MemberResponse memberResponse;

    @BeforeEach
    void setUp() {
        memberExternalService = mock(MemberExternalService.class);
        controller = new MemberExternalController(memberExternalService);
        memberResponse = new MemberResponse(
                1L,
                "Ana Souza",
                MemberRole.GERENTE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateMember() {
        MemberCreateRequest request = new MemberCreateRequest("Ana Souza", MemberRole.GERENTE);
        when(memberExternalService.create(request)).thenReturn(memberResponse);

        MemberResponse response = controller.create(request);

        assertEquals(memberResponse, response);
        verify(memberExternalService).create(request);
    }

    @Test
    void shouldFindMemberById() {
        when(memberExternalService.findById(1L)).thenReturn(memberResponse);

        MemberResponse response = controller.findById(1L);

        assertEquals(memberResponse, response);
        verify(memberExternalService).findById(1L);
    }

    @Test
    void shouldFindAllMembers() {
        when(memberExternalService.findAll()).thenReturn(List.of(memberResponse));

        List<MemberResponse> response = controller.findAll();

        assertEquals(List.of(memberResponse), response);
        verify(memberExternalService).findAll();
    }
}
