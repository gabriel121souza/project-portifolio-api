package br.com.gabriel.project_portfolio_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProjectPortfolioApiApplicationTests {

	@Test
	void shouldDeclareSpringBootApplication() {
		assertNotNull(ProjectPortfolioApiApplication.class.getAnnotation(SpringBootApplication.class));
	}

}
