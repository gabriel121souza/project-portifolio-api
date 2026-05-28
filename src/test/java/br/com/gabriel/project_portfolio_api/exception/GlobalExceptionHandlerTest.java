package br.com.gabriel.project_portfolio_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/projects");
    }

    @Test
    void shouldHandleResourceNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(
                new ResourceNotFoundException("Projeto não encontrado"),
                request
        );

        assertError(response, HttpStatus.NOT_FOUND, "Resource not found", "Projeto não encontrado", List.of());
    }

    @Test
    void shouldHandleBusinessException() {
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(
                new BusinessException("Regra inválida"),
                request
        );

        assertError(response, HttpStatus.BAD_REQUEST, "Business rule violation", "Regra inválida", List.of());
    }

    @Test
    void shouldHandleValidationException() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "name", "é obrigatório"));
        Method method = SampleController.class.getDeclaredMethod("create", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(exception, request);

        assertError(
                response,
                HttpStatus.BAD_REQUEST,
                "Validation error",
                "Existem campos inválidos na requisição",
                List.of("name: é obrigatório")
        );
    }

    @Test
    void shouldHandleDataIntegrityViolation() {
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("constraint"),
                request
        );

        assertError(
                response,
                HttpStatus.CONFLICT,
                "Data integrity violation",
                "Não foi possível concluir a operação devido a uma restrição de integridade dos dados",
                List.of()
        );
    }

    @Test
    void shouldHandleGenericException() {
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(
                new RuntimeException("Erro interno"),
                request
        );

        assertError(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                "Ocorreu um erro inesperado no servidor",
                List.of()
        );
    }

    private void assertError(
            ResponseEntity<ErrorResponse> response,
            HttpStatus status,
            String error,
            String message,
            List<String> details
    ) {
        ErrorResponse body = response.getBody();

        assertEquals(status, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.timestamp());
        assertEquals(status.value(), body.status());
        assertEquals(error, body.error());
        assertEquals(message, body.message());
        assertEquals("/api/projects", body.path());
        assertEquals(details, body.details());
    }

    private static class SampleController {

        @SuppressWarnings("unused")
        void create(String name) {
        }
    }
}
