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

import com.zstorage.credential.User;

public class UserEncrypter implements Cryptator<User>, Serializable{

	private static final long serialVersionUID = 1L;

	private byte[] cryptUser;
	
	private Key key;
	
	private static final UserEncrypter instance = new UserEncrypter();
	
	private UserEncrypter() { }
	
	public static UserEncrypter getInstance(Key key) {
		instance.key = key;
		
		return instance;
	}
	
	public static UserEncrypter getInstance() {
		return instance;
	}
	
	public byte[] getCryptUser() {
		return this.cryptUser;
	}
	
	@Override
	public byte[] crypt(User user) {
		byte[] encUser = null;
		
		try {
			Cipher cipher = Cipher.getInstance(TRASFORMATION);
						
			cipher.init(Cipher.ENCRYPT_MODE, this.key);
			
			encUser = cipher.doFinal(new String(user.getCredential()).getBytes("UTF-8"));
			
			this.cryptUser = encUser;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | InvalidKeyException e) {
			e.printStackTrace();
		}
		
		return encUser;
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
