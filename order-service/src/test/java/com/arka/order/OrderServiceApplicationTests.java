package com.arka.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"debug=false",
		"logging.level.root=INFO",
		"spring.kafka.listener.auto-startup=false",
		"spring.task.scheduling.enabled=false",
		"arka.outbox.publisher.enabled=false",
		"notification.service.base-url=http://localhost:8084"
})
class OrderServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
