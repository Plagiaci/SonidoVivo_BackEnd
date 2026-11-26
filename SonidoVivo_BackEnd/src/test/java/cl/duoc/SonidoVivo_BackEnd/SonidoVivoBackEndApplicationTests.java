package cl.duoc.SonidoVivo_BackEnd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:sonidovivo-context;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"app.jwt.secret=isolated-context-test-secret-key-at-least-32-bytes"
})
class SonidoVivoBackEndApplicationTests {

	@Test
	void contextLoads() {
	}

}
