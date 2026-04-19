package com.arka.report;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "debug=false",
        "logging.level.root=INFO",
        "arka.reports.scheduler.enabled=false",
        "services.inventory.base-url=http://localhost:8082",
        "services.order.base-url=http://localhost:8083"
})
class ReportServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
