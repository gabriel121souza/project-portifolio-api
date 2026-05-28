package br.com.gabriel.project_portfolio_api.controller;

import br.com.gabriel.project_portfolio_api.dto.request.MemberCreateRequest;
import br.com.gabriel.project_portfolio_api.dto.response.MemberResponse;
import br.com.gabriel.project_portfolio_api.service.MemberExternalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/external-api/members")
public class MemberExternalController {

    private final MemberExternalService memberExternalService;

    public MemberExternalController(MemberExternalService memberExternalService) {
        this.memberExternalService = memberExternalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse create(@RequestBody @Valid MemberCreateRequest request) {
        return memberExternalService.create(request);
    }

    @GetMapping("/{id}")
    public MemberResponse findById(@PathVariable Long id) {
        return memberExternalService.findById(id);
    }

    @GetMapping
    public List<MemberResponse> findAll() {
        return memberExternalService.findAll();
    }
}
