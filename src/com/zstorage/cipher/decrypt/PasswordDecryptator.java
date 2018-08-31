package com.zstorage.cipher.decrypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.zstorage.credential.Password;

public class PasswordDecryptator implements Decryptator<Password>{

	private static final PasswordDecryptator decryptator = new PasswordDecryptator();
	
	private PasswordDecryptator() { }
	
	public static PasswordDecryptator getDecryptator() {
		return decryptator;
	}
	
	@Override
	public Password decrypt(byte[] crypted, Key key) {				
		Password password = null;
				
		try {
			final Cipher cipher = Cipher.getInstance(TRASFORMATION);
			
			cipher.init(Cipher.DECRYPT_MODE, key);

			byte[] decRaw = cipher.doFinal(crypted);
			
			char[] pass = new String(decRaw, "UTF-8").toCharArray();
			
			password = new Password(pass);
						
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}		
		
		return password;
	}

	
}
