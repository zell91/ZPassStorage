package com.zstorage.credential;

import java.io.Serializable;
import java.security.Key;

public class EntryCredential implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private byte[] user;
	private byte[] pass;
	
	private Key key;
	
	public EntryCredential(byte[] user, byte[] pass, Key key) {
		
		if(pass == null || user == null)
			throw new IllegalArgumentException("User and password cannot be null");

		this.user = user;
		this.pass = pass;		
		this.key = key;
	}
	
	public byte[] getCryptedPassword() {
		return this.pass;
	}

	public byte[] getCryptedUser() {
		return this.user;
	}

	public Key getKey() {
		return key;
	}
	
	
	
}
