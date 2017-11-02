
package org.guivaadin.contacts.model;


import java.io.Serializable;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;


@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class Contact implements Serializable
{
	private Long id;

	@NotNull @NonNull
	private String	firstName;

	@NotNull @NonNull
	private String	lastName;

	@NotNull @NonNull
	private String	phoneNumber;

	@NotNull @NonNull
	private String	emailAddress;




}
