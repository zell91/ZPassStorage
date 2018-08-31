package com.zstorage.store;

import java.io.Serializable;
import java.security.Key;

import com.zstorage.cipher.decrypt.PasswordDecryptator;
import com.zstorage.cipher.decrypt.UserDecryptator;
import com.zstorage.credential.EntryCredential;
import com.zstorage.credential.Password;
import com.zstorage.credential.User;

public class StoredObject implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private byte[] cryptPass;
	private byte[] cryptUser;
	
	private String note;
	private String category;
	
	private Key key;
	
	public StoredObject(EntryCredential cryptCredential, String note) {
		if(cryptCredential == null || cryptCredential.getCryptedPassword() == null)
			throw new IllegalArgumentException();
		
		this.cryptUser = cryptCredential.getCryptedUser();
		this.cryptPass = cryptCredential.getCryptedPassword();
		this.key = cryptCredential.getKey();
		this.note = note;
	}
	
	public StoredObject(EntryCredential cryptCredential) {
		this(cryptCredential, null);
	}
	
	public byte[] getCryptPass() {
		return this.cryptPass;
	}
	
	public void setCryptPassword(byte[] cryptPass) {
		this.cryptPass = cryptPass;
	}
	
	public Password getPassword() {
		PasswordDecryptator decryptator = PasswordDecryptator.getDecryptator();		
		return decryptator.decrypt(this.cryptPass, this.key);		
	}
	
	public byte[] getCryptUser() {
		return this.cryptUser;
	}
	
	public void setCryptUser(byte[] cryptUser) {
		this.cryptUser = cryptUser;
	}
	
	public User getUser() {
		UserDecryptator decryptator = UserDecryptator.getDecryptator();
		return decryptator.decrypt(this.cryptUser, this.key);
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public Key getKey() {
		return key;
	} 
}
