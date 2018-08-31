package com.zstorage.cipher.decrypt;

import java.security.Key;

import com.zstorage.cipher.Cipher;

public interface Decryptator<T> extends Cipher{
	T decrypt(byte[] crypted, Key key);	
}
