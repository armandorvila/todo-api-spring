package com.armandorv.poc.tasks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

	private Security security = new Security();
	
	@Data
	public static class Security {
		
		private Jwt jwt = new Jwt();
		
		@Data
		public static class Jwt {
			private String secret = null;
			private Long tokenValidityInSeconds = 1800L;
		}
	}
}
