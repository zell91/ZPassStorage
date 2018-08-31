package com.zstorage.cipher.decrypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.zstorage.credential.User;

public class UserDecryptator implements Decryptator<User> {
	
	private static final UserDecryptator decryptator = new UserDecryptator();
	
	private UserDecryptator() { }
	
	public static UserDecryptator getDecryptator() {
		return decryptator;
	}
	
	@Override
	public User decrypt(byte[] crypt, Key key) {
		User user = null;		
		
		try {
			final Cipher cipher = Cipher.getInstance(TRASFORMATION);
			
			cipher.init(Cipher.DECRYPT_MODE, key);

			byte[] decRaw = cipher.doFinal(crypt);
			
			String u = new String(decRaw, "UTF-8");
			
			user = new User(u.toCharArray());
						
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}		
		
		return user;
	}
	

}
