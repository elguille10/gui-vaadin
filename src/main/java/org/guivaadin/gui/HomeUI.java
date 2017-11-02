
package org.guivaadin.gui;


import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import com.vaadin.ui.UI;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.navigator.View;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;


@SpringUI
@SpringViewDisplay
@Theme( "valo" )
public class HomeUI extends UI implements ViewDisplay
{

	private Panel pnlViewDisplay = new Panel();

	private RestTemplate restTemplate;


	@Autowired
	public HomeUI( @LoadBalanced RestTemplate restTemplate ) {
		this.restTemplate = restTemplate;
	}


	@Override
	protected void init( VaadinRequest request )
	{
		VerticalLayout rootLayout = new VerticalLayout();
		rootLayout.setSizeFull();

		getUI().getNavigator().addView( "" , new HomeView() );
		getUI().getNavigator().setErrorView( ErrorView.class );

		Button btnGreeting = new Button( "Greetins" );
		btnGreeting.addClickListener( e -> greeting() );

		CssLayout navigationBar = new CssLayout();
		navigationBar.addStyleName( ValoTheme.LAYOUT_COMPONENT_GROUP );
		navigationBar.addComponent( btnGreeting );
		navigationBar.addComponent( createNavigationButton( "Contacts", "contacts" ) );
		rootLayout.addComponent( navigationBar );

		pnlViewDisplay.setSizeFull();
		rootLayout.addComponent( pnlViewDisplay );
		rootLayout.setExpandRatio( pnlViewDisplay, 1.0f );

		Button btnLogout = new Button( "Logout" );
		rootLayout.addComponent( btnLogout );

		setContent( rootLayout );
	}

	public static class HomeView extends VerticalLayout implements View
	{
		@PostConstruct
		public void init() {
			addComponent( new Label( "Hola Mundo" ) );
		}

	}

	private Button createNavigationButton( String caption, String viewName )
	{
		Button button = new Button( caption );
		button.addStyleName( ValoTheme.BUTTON_SMALL );
		button.addClickListener( e -> getUI().getNavigator().navigateTo( viewName ) );		

		return button;
	}

	private void greeting()
	{
		String message = "";
		try {
			//String url = CloudConfig.getGreetingServiceURL() + "/greeting";
			//String url = "https://spring-boot-cloud-muckle-perishableness.cfapps.io/greeting";
			message = restTemplate.exchange( "//greeting-service/greeting", 
							HttpMethod.GET, new HttpEntity<>( null ), String.class ).getBody();

			if( message != null)
			{
				Notification.show( message );
				System.out.println( message );
			}
		}
		catch( NullPointerException npe ) {
			npe.printStackTrace();
		}
	}

	@Override
	public void showView(View view) {
		pnlViewDisplay.setContent( (Component) view );
	}

}
