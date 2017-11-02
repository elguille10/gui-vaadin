
package org.guivaadin.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.boot.web.client.RestTemplateBuilder;


@Configuration
@Profile( "cloud" )
@EnableDiscoveryClient
public class CloudConfig
{

	/***
	 * Creation of the bean RestTemplate with Load Balanced interceptor for Netflix Ribbon
	 * @param restTemplateBuilder
	 * @return
	 */
	@Bean
	@LoadBalanced
	public static RestTemplate restTemplate( RestTemplateBuilder restTemplateBuilder ) {
		return restTemplateBuilder.build();
	}

}
