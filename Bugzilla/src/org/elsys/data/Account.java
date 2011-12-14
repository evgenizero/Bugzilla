package org.elsys.data;

import java.util.Random;

public class Account {

	private String userEmail;
	private String userPassword;
	private String accountDescription;
	private String accountUrl;
	private int accountId;
	private static Random generator = new Random();
	
	public Account(String userEmail, String userPassword, String accountUrl) {
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.accountUrl = accountUrl;
	}

	public static int generateAccountId() {
		return generator.nextInt(100);
	}

	public void setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
	
	public String getUserPassword() {
		return userPassword;
	}
	
	public String getAccountUrl() {
		return accountUrl;
	}
	
	public String getAccountDescription() {
		return accountDescription;
	}
	
	public void setAccountId(int id) {
		this.accountId = id;
	}
	
	public int getAccountId() {
		return accountId;
	}
}
