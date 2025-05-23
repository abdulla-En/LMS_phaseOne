package net.java.lms_backend;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.mock;

@SpringBootTest
class LmsBackendApplicationTests {

    @Nested
    @TestConfiguration
    class TestConfig {
		@Bean
		public JavaMailSender javaMailSender() {
			return mock(JavaMailSender.class);
		}
	@Test
	void contextLoads() {

	}

}}