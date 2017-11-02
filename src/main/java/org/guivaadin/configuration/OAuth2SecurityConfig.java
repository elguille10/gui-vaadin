
package org.guivaadin.configuration;


import java.util.Arrays;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Bean;

import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;


@Configuration
@Profile( "secure" )
@EnableOAuth2Client
public class OAuth2SecurityConfig
{

	@Bean
	@LoadBalanced
	public OAuth2RestTemplate oAuth2RestTemplate(  OAuth2ClientContext oauth2ClientContext ) {
		return new OAuth2RestTemplate( new AuthorizationCodeResourceDetails(), oauth2ClientContext ); 
	}

	/*@Bean
	public OAuth2ProtectedResourceDetails createAuthorizationResource()
	{	
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails(); 

		resource.setId( "oauth2-resource" );
		resource.setClientId( "gui-vaadin" );
		resource.setClientSecret( "123" );
		resource.setScope( Arrays.asList( "webclient" ) );
		resource.setAccessTokenUri( "http://localhost:8901/oauth/token" );
		resource.setGrantType( "password" );

		resource.setUsername( "admin" );
		resource.setPassword( "123" );

		return resource;
	}*/
}
