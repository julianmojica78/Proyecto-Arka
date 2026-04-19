package com.arka.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"debug=false",
		"logging.level.root=INFO",
		"jwt.secret=185ee9a8b7197f600752e1deb44cb3dfe89b14d1664f18ed85fc17d1a9c861ab",
		"spring.r2dbc.url=r2dbc:postgresql://localhost:5432/auth_db",
		"spring.r2dbc.username=postgres",
		"spring.r2dbc.password=1234"
})
class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
