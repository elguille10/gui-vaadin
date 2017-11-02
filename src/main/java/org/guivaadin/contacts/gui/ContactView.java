
package org.guivaadin.contacts.gui;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.navigator.View;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import org.guivaadin.contacts.model.Contact;


@SpringView( name = "contacts" )
@UIScope
public class ContactView extends VerticalLayout implements View
{

	private RestTemplate		restTemplate;

	private ContactEditor		editor;
	private Grid<Contact>		grid = new Grid<>( Contact.class );
	private List<Contact> 		contacts;
	private TextField			txtFilter = new TextField();
	private ComboBox<String>	cbxFilterOptions = new ComboBox<>();


	@Autowired
	public ContactView( @LoadBalanced RestTemplate restTemplate, ContactEditor editor )
	{
		this.restTemplate	= restTemplate;
		this.editor			= editor;
	}


	@PostConstruct
	public void init()
	{
		HorizontalLayout actions = new HorizontalLayout();
		
		Button btnNew = new Button( "New Contact", FontAwesome.PLUS );
		// Instantiate and edit new Contact when the new button is clicked
		btnNew.addClickListener( e -> {
				txtFilter.clear();
				editor.editContact( new Contact() );
			} );

		txtFilter.setPlaceholder( "Filter by last name" );
		// Replace listing with filtered content when user changes filter
		txtFilter.setValueChangeMode( ValueChangeMode.LAZY );
		txtFilter.addValueChangeListener( e -> listContacts( e.getValue() ) );

		cbxFilterOptions.setItems( "Last Name", "Id" );

		grid.setWidth( "800" );
		grid.setHeight( "300" );
		listContacts( null );
		// Connect selected Customer to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener( e -> editor.editContact( e.getValue() ) );

		// Add events to the editor buttons, refresh data from backend
		editor.setChangeHandler( () -> {
				listContacts( null );
				grid.deselectAll();
				editor.setVisible( false );
			} );

		actions.addComponents( txtFilter, cbxFilterOptions, btnNew );
		addComponents( actions, grid, editor );
		setVisible( true );
	}

	public void listContacts( String filterText )
	{
		contacts = new ArrayList<>();

		if( txtFilter.isEmpty() )
		{
			castJsonToContactAndFillGrid( restTemplate.exchange( "//api-gateway/contact-service/contact/api",
									HttpMethod.GET, new HttpEntity<>( null ), List.class ).getBody()
							);
		}
		else if( filterText != null && cbxFilterOptions.getValue() != null )
		{
			if( cbxFilterOptions.getValue().equals( "Last Name" ) )
			{
				castJsonToContactAndFillGrid( restTemplate.exchange( "//api-gateway/contact-service/contact/service/findbylastname/" + filterText, 
										HttpMethod.GET, new HttpEntity<>( null ), List.class ).getBody()
							);
			}
			else if( cbxFilterOptions.getValue().equals( "Id" ) )
			{
				try {
					Contact c = restTemplate.exchange( "//api-gateway/contact-service/contact/api/" + filterText, 
												HttpMethod.GET, new HttpEntity<>( null ), Contact.class ).getBody();

					if( c != null )
						contacts.add( c );
				}
				catch( NumberFormatException ne ) {
					ne.printStackTrace();
					Notification.show( "Valor ingresado no valido", Type.WARNING_MESSAGE );
				}
			}

		}

		if( contacts != null && contacts.size() > 0 )
			grid.setItems( contacts );
	}

	private void castJsonToContactAndFillGrid( List resultado )
	{
		Contact c;
		for( int i=0; i<resultado.size(); i++ )
		{
			LinkedHashMap map = (LinkedHashMap) resultado.get( i );
			c = new Contact();

			c.setId( Long.parseLong( map.get( "id" ).toString() ) );
			c.setFirstName( map.get( "firstName" ).toString() );
			c.setLastName( map.get( "lastName" ).toString() );
			c.setPhoneNumber( map.get( "phoneNumber" ).toString() );
			c.setEmailAddress( map.get( "emailAddress" ).toString() );

			contacts.add( c );
		}

	}

}
