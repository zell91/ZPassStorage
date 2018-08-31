package com.zstorage.credential;

import static com.zstorage.cipher.Cipher.KEY_STORE_TYPE;
import static com.zstorage.cipher.Cipher.TRASFORMATION;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import com.zstorage.cipher.decrypt.PasswordDecryptator;
import com.zstorage.cipher.decrypt.UserDecryptator;
import com.zstorage.cipher.encrypt.PasswordEncrypter;
import com.zstorage.cipher.encrypt.UserEncrypter;
import com.zstorage.login.Login;
import com.zstorage.store.StoredObject;

public class Account implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_NAME = "User";
	
	public static final File DEFAULT_AVATAR = new File("images/user.png").getAbsoluteFile(); 
	
	private String name;
	private File avatar;
	
	private int _hashCode;

	private List<StoredObject> storedObjects = Collections.synchronizedList(new ArrayList<>());
	
	private EntryCredential entry;
	
	private SecretKey secretKey;
	
	private File file;
	
	private Account(EntryCredential entry) throws UnsupportedEncodingException {		
		this(entry, DEFAULT_NAME);		
	}
	
	private Account(EntryCredential entry, String name) throws UnsupportedEncodingException {		
		this.generateHashCode();
		this.entry = entry;
		this.secretKey = (SecretKey) entry.getKey();
		this.name = name;
		this.avatar = DEFAULT_AVATAR;
	}
	
	public void setAvatar(File avatar) {
		this.avatar = avatar;
		this.store();
	}
	
	public File getAvatar() throws FileNotFoundException{
		if(!this.avatar.exists()) {
			avatar = DEFAULT_AVATAR;
		}
		
		return this.avatar;
	}
	
	public void setName(String name) {
		this.name = name;
		this.store();
	}
	
	public String getName() {
		return name;
	}
	
	public void generateHashCode() {
		while(new File(Login.keyPath, "user" + (_hashCode = new Random().nextInt(Integer.MAX_VALUE))).exists());
	}
	
	@Override
	public int hashCode() {
		return _hashCode;
	}
	
	public synchronized void store() {
		Thread storeThread = new Thread(()->{
			this.file = new File(Login.keyPath, "user" + this.hashCode());
			
			Password password = PasswordDecryptator.getDecryptator().decrypt(this.entry.getCryptedPassword(), this.secretKey);
			User user = UserDecryptator.getDecryptator().decrypt(this.entry.getCryptedUser(), this.secretKey);

			try {
				Cipher cipher = Cipher.getInstance(TRASFORMATION);	
				cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);			

				try(FileOutputStream out = new FileOutputStream(this.file)){
					KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);

					keyStore.load(null, password.getCredential());				
								
					KeyStore.SecretKeyEntry ske = new KeyStore.SecretKeyEntry(this.secretKey);

					keyStore.setEntry("", ske, new KeyStore.PasswordProtection(password.getCredential()));
									        
					keyStore.store(out, user.getCredential());
				}catch(IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException ex) {
					ex.printStackTrace();
				}
				
				try(ObjectOutputStream objStream = new ObjectOutputStream(new FileOutputStream(this.file, true))) {
					SealedObject obj = new SealedObject(this, cipher);										
					objStream.writeObject(obj);
		    	} catch (IllegalBlockSizeException | IOException e) {
					e.printStackTrace();
				}	
				
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e1) {
				e1.printStackTrace();
			}
		});
		
		storeThread.setName("StoreThread");
		storeThread.setDaemon(false);
		storeThread.start();
	}
	
	public void addStoredObject(StoredObject storedObj) {		
		this.storedObjects.add(storedObj);
		this.store();
	}
	
	public void removeStoredObject(StoredObject storedObj) {
		this.storedObjects.remove(storedObj);
		this.store();
	}
	
	public void clearStoredObjects() {
		this.storedObjects.clear();
		this.store();
	}
	
	public boolean deleteAccount(User user, Password password) {
		if(Login.getDefault().login(user, password) != null) {
			return this.file.delete();
		}
		
		return false;		
	}

	public void setNewCredential(Credential oldCredential, Credential newCredential) throws UnsupportedEncodingException {
		PasswordDecryptator decryptator = PasswordDecryptator.getDecryptator();
		
		if(!oldCredential.getClass().equals(newCredential.getClass()))
			throw new IllegalArgumentException("The args must be same type");
		
		Credential current = newCredential instanceof Password ? decryptator.decrypt(this.entry.getCryptedPassword(), this.secretKey) : decryptator.decrypt(this.entry.getCryptedUser(), this.secretKey);

		if(current.equals(oldCredential)) {
			if(newCredential instanceof User) {
				UserEncrypter user = UserEncrypter.getInstance(this.secretKey);
				user.crypt((User)newCredential);
				this.entry = new EntryCredential(user.getCryptUser(), this.entry.getCryptedPassword(), this.secretKey);												
			}else {
				PasswordEncrypter pass = PasswordEncrypter.getInstance(this.secretKey);
				pass.crypt((Password)newCredential);
				this.entry = new EntryCredential(this.entry.getCryptedUser(), pass.getCryptPass() , this.secretKey);	
				

			}
			
			this.store();
		}			
	}

	public List<StoredObject> getStoredObjects() {
		return Collections.unmodifiableList(storedObjects);
	}
	
	public static Account createNew(String name, User username, Password password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		
		KeyGenerator keyGen = KeyGenerator.getInstance(TRASFORMATION);		
		SecretKey key = keyGen.generateKey();
		
		PasswordEncrypter pass = PasswordEncrypter.getInstance(key);
		UserEncrypter user = UserEncrypter.getInstance(key);
		
		user.crypt(username);		
		pass.crypt(password);
		
		EntryCredential credential = new EntryCredential(user.getCryptUser(), pass.getCryptPass(), key);		
		
		Account account = name != null ? new Account(credential, name) : new Account(credential);
		
		if(exist(username))
			return null;
		
		account.store();
				
		return account;
	}
	
	public static Account createNew(User username, Password password) throws UnsupportedEncodingException, NoSuchAlgorithmException {				
		return createNew(null, username, password);
	}

	private static boolean exist(User username) {		
		return Login.checkUser(username);
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public File getFileAccount() {
		return this.file;
	}
}
