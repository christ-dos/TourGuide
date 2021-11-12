package com.tripMaster.tourguideclient.model;

import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

@Getter
@Setter
public class UserPreferences {
	
	private int attractionProximity = Integer.MAX_VALUE;
	private CurrencyUnit currency = Monetary.getCurrency("USD");
	private Money lowerPricePoint = Money.of(0, currency);
	private Money highPricePoint = Money.of(Integer.MAX_VALUE, currency);
	private int tripDuration = 5;
	private int ticketQuantity = 1;
	private int numberOfAdults = 2;
	private int numberOfChildren = 1;
	
	public UserPreferences() {
	}


}
