package com.zstorage.cipher.encrypt;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.zstorage.credential.Password;

public class PasswordEncrypter implements Cryptator<Password>, Serializable{
	
	private static final long serialVersionUID = 1L;

	private byte[] cryptPass;

	private Key key;
	
	private static final PasswordEncrypter instance = new PasswordEncrypter();
	
	private PasswordEncrypter( ) { }
	
	private PasswordEncrypter(Key key) {
		this.key = key;
	}
	
	public static PasswordEncrypter getInstance(Key key) {
		instance.key = key;
		
		return instance;
	}
	
	public static PasswordEncrypter getInstance() {
		return instance;
	}
	
	public byte[] getCryptPass() {
		return cryptPass;
	}

	@Override
	public byte[] crypt(Password pass) { 
		
		if(this.key == null)
			throw new RuntimeException("Key not initialized");
		
		byte[] encPass = null;
		
		try {
			Cipher cipher = Cipher.getInstance(TRASFORMATION);

			cipher.init(Cipher.ENCRYPT_MODE, this.key);						
			
			encPass = cipher.doFinal(new String(pass.getCredential()).getBytes("UTF-8"));
			
			this.cryptPass = encPass;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | InvalidKeyException e) {
			e.printStackTrace();
		}
		
		return encPass;
	}

	@Override
	public Key getKey() {
		return key;
	}

	@Override
	public void setKey(Key key) {
		this.key = key;
	}
}
