package com.zstorage.login;

import static com.zstorage.cipher.Cipher.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import com.zstorage.credential.Account;
import com.zstorage.credential.Password;
import com.zstorage.credential.User;

public class Login {
		
	public static final File keyPath = new File("keys");

	private static final Login login = new Login();
	
	private volatile static Integer attempt = 0;

	private Set<File> accountFiles = new HashSet<>();
		
	private Login() {
		if(keyPath.exists()) {
			File[] keys = keyPath.listFiles();
			
			for(File key : keys) {
				if(key.getName().matches("user\\d+")){
					accountFiles.add(key);
				}
			}
			
				
		}else {
			keyPath.mkdirs();
		}
	}
	
	public static Login getDefault() {
		return login;
	}
	
	public Set<File> getAccountKeys() {
		return Collections.unmodifiableSet(this.accountFiles); 
	}
	
	public Account login(User user, Password password, File accountFile) {
		try {
			final Cipher cipher = Cipher.getInstance(TRASFORMATION);
			final KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
			
			synchronized(attempt) {			
				SecretKey secretKey = null;
				
				try(FileInputStream in = new FileInputStream(accountFile)){

					keyStore.load(in, user.getCredential());
					secretKey = (SecretKey) keyStore.getKey("", password.getCredential());						
				}catch(IOException | UnrecoverableKeyException  ex) {
					return null;
				}
									
				cipher.init(Cipher.DECRYPT_MODE, secretKey);	

				BufferedInputStream in = new BufferedInputStream(new FileInputStream(accountFile));
				
				in.skip(SIZE_KEY);
				
	        	try(ObjectInputStream objStream = new ObjectInputStream(in)) {
	        		
					SealedObject obj = (SealedObject) objStream.readObject();
					
					Account account = (Account) obj.getObject(cipher);						
					
					in.close();
					
					return account;					
	        	} catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
	        		e.printStackTrace();
				}
			}

			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | KeyStoreException | CertificateException | InvalidKeyException | IOException e) {
			e.printStackTrace();
		}		
		
		return null;
	}
	
	public Account login(User user, Password password) {		
		if(accountFiles.isEmpty())
			return null;
		
		for(File accountFile : accountFiles) {
			Account account = this.login(user, password, accountFile);

			if(account != null) 
				return account; 
		}
		
		return null;
	}

	public static boolean checkUser(User username) {
		Login login = Login.getDefault();
		
		if(login.accountFiles.isEmpty())
			return false;
		
		Iterator<File> iterator = login.accountFiles.iterator();
		
		while(iterator.hasNext()) {
			File accountKey = iterator.next();
			
			try(FileInputStream in = new FileInputStream(accountKey)){
				
				final KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);				
				
				keyStore.load(in, username.getCredential());	
				
				if(keyStore.aliases().hasMoreElements())
					return true;
				else
					continue;
				
			} catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
			}
		}	
		
		return false;
	}

	public static void update(File accountFile) {
		login.accountFiles.add(accountFile);
	}
		
}
