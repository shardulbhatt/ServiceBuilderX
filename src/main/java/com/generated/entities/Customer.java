package com.generated.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.lang.Long;
import java.lang.String;
import javax.persistence.GenerationType;
import java.util.Set;
import com.generated.entities.Address;
import javax.persistence.GeneratedValue;

import javax.persistence.Entity;

@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String name;

	@Column
	private String occupation;

	@Column
	private int age;

	@OneToMany
	private Set<Address> addressSet;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getAge() {
		return age;
	}

	public void setAddressSet(Set<Address> addressSet) {
		this.addressSet = addressSet;
	}

	public Set<Address> getAddressSet() {
		return addressSet;
	}

}