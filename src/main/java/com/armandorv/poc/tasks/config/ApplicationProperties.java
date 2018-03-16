package com.armandorv.poc.tasks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

	private Security security = new Security();
	private ErrorHandler errorHandler = new ErrorHandler();

	@Data
	public static class ErrorHandler {
		private Boolean debug = false;
	}

	@Data
	public static class Security {

		private Jwt jwt = new Jwt();

		@Data
		public static class Jwt {
			private String secret = "secret";
			private Long tokenValidityInSeconds = 1800L;
		}
	}
}
