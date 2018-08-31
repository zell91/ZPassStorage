package com.zstorage.cipher.encrypt;

import java.security.Key;

import com.zstorage.cipher.Cipher;

public interface Cryptator<T> extends Cipher{
	
	byte[] crypt(T raw);	
		
	Key getKey();

	void setKey(Key key);

}
