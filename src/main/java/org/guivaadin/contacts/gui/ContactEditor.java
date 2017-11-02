
package org.guivaadin.contacts.gui;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button;
import com.vaadin.data.Binder;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.event.ShortcutAction;

import org.guivaadin.contacts.model.Contact;


@SpringComponent
@UIScope
public class ContactEditor extends VerticalLayout
{

	private RestTemplate restTemplate;

	private Contact		contact;

	private TextField	firstName = new TextField( "First Name" );
	private TextField	lastName = new TextField( "Last Name" );
	private TextField	phoneNumber = new TextField( "Phone" );
	private TextField	emailAddress = new TextField( "Email" );
	private Button		btnSave = new Button( "Save", FontAwesome.SAVE );
	private Button		btnDelete = new Button( "Delete", FontAwesome.TRASH_O );
	private Button		btnCancel = new Button( "Cancel" );

	private static Binder<Contact> binder = new Binder<>( Contact.class );


	@Autowired
	public ContactEditor( @LoadBalanced RestTemplate restTemplate )
	{
		this.restTemplate = restTemplate;

		binder.bindInstanceFields( this );

		CssLayout actions = new CssLayout( btnSave, btnDelete, btnCancel );
		actions.setStyleName( ValoTheme.LAYOUT_COMPONENT_GROUP );

		btnSave.setStyleName( ValoTheme.BUTTON_PRIMARY );
		btnSave.setClickShortcut( ShortcutAction.KeyCode.ENTER );

		btnSave.addClickListener( e -> contact = restTemplate.postForObject( "//api-gateway/contact-service/contact/api/", contact, Contact.class ) );
		btnDelete.addClickListener( e -> restTemplate.delete( "//api-gateway/contact-service/contact/api/" + contact.getId().toString() ) );
		btnCancel.addClickListener( e -> setVisible( false ) );

		addComponents( firstName, lastName, phoneNumber, emailAddress );
		addComponents( actions );

		setSpacing( true );
		setVisible( false );
	}

	public void editContact( Contact c )
	{
		evaluateIfAlreadyPersisted( c );

		/***
		 * Bind contact properties to similarly named fields
		 * moving values from fields to entities before saving
		 */
		binder.setBean( contact ); 

		// A hack to ensure the whole form is visible
		btnSave.focus();
		
		setVisible( true );
	}

	private void evaluateIfAlreadyPersisted( Contact c )
	{
		if( c != null )
		{
			boolean persisted = c.getId() != null;
	
			if( persisted ) {
				contact = restTemplate.exchange( "//api-gateway/contact-service/contact/api/" + c.getId().toString(), 
											HttpMethod.GET, new HttpEntity<>( null ), Contact.class ).getBody();
			}
			else {
				contact = c;
			}
	
			btnDelete.setVisible( persisted );
		}
	}

	/***
	 * Adding functionality to the events of the buttons
	 * using functional interface
	 */
	@FunctionalInterface
	public static interface ChangeHandler {
		void onChange();
	}

	public void setChangeHandler( ChangeHandler ch )
	{
		btnSave.addClickListener( e -> ch.onChange() );
		btnDelete.addClickListener( e -> ch.onChange() );
	}

	public Contact getContact() {
		return this.contact;
	}
}
