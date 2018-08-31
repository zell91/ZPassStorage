package com.zstorage.test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.zstorage.cipher.encrypt.PasswordEncrypter;
import com.zstorage.cipher.encrypt.UserEncrypter;
import com.zstorage.credential.Account;
import com.zstorage.credential.EntryCredential;
import com.zstorage.credential.Password;
import com.zstorage.credential.User;
import com.zstorage.login.Login;
import com.zstorage.store.StoredObject;

public class Test {
	
	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, InterruptedException {
		Password password = new Password(new char[] {'c'});
		User username = new User("user".toCharArray());		
		
		
		Login login = Login.getDefault();
		
		Account account = Account.createNew(username, password);

		//Account account = login.login(username, password);
		
		//account.deleteAccount(username, password);
		
		if(account != null) {
			
		//	account.setNewCredential(new Password("a".toCharArray()), new Password("c".toCharArray()));

		//	account.setNewCredential(new User("USER".toCharArray()), new User("user".toCharArray()));

			UserEncrypter userEncrypter = UserEncrypter.getInstance(account.getSecretKey());
			PasswordEncrypter encPassword = PasswordEncrypter.getInstance(account.getSecretKey());

			userEncrypter.crypt(new User("secondo".toCharArray()));		
			encPassword.crypt(new Password(new char[]{'p', 'a', 's', 's', '4', 'o', '5', 'd'}));
			
			EntryCredential entry = new EntryCredential(userEncrypter.getCryptUser(), encPassword.getCryptPass(), account.getSecretKey());
			
			StoredObject stored = new StoredObject(entry);
			account.addStoredObject(stored);
		}		
		
		if(account != null) {
			System.out.println(account.getStoredObjects());
			account.getStoredObjects().forEach(s->System.out.println("User: " + new String(s.getUser().getCredential()) + "\n" + "Password: " + new String(s.getPassword().getCredential())));
		}
		
	}

}
