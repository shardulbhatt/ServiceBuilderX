package com.generated.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.lang.Long;
import java.lang.String;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;

import javax.persistence.Entity;

@Entity
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String flatNo;

	@Column
	private String apartment;

	@Column
	private String landmark;

	@Column
	private String city;

	@Column
	private String state;

	@Column
	private String country;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setFlatNo(String flatNo) {
		this.flatNo = flatNo;
	}

	public String getFlatNo() {
		return flatNo;
	}

	public void setApartment(String apartment) {
		this.apartment = apartment;
	}

	public String getApartment() {
		return apartment;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountry() {
		return country;
	}

}