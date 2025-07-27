package code.kuma.Project_ITSS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"code.kuma.Project_ITSS",
		"controllers",
		"services",
		"config",
		"util",
		"dtos",
		"response",
		"repositories",
		"models",
		"security"
})
@EntityScan(basePackages = "models")
@EnableJpaRepositories(basePackages = "repositories")
public class ProjectItssApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjectItssApplication.class, args);
	}
}