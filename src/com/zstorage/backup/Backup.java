package com.zstorage.backup;

import static com.zstorage.cipher.Cipher.SIZE_KEY;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.nio.file.Path;

import javax.crypto.SealedObject;

import com.zstorage.backup.exception.BackupException;

public class Backup extends File{
	
	private static final long serialVersionUID = 1L;
	private long realLength;
	
	public Backup(String name) throws BackupException {
		super(name);
		this.validate();
	}
	
	public Backup(File path, String name) throws BackupException {
		super(path, name);
		this.validate();		
	}
	
	public Backup(String parent, String child) throws BackupException {
		super(parent, child);
		this.validate();
	}
	
	public Backup(URI uri) throws BackupException {
		super(uri);
		this.validate();
	}
		
	private void validate() throws BackupException {
		File toCheck = this.getAbsoluteFile();
		boolean valid = false;		
		ObjectInputStream _input = null;
		
		try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(toCheck));
			ObjectInputStream input = new ObjectInputStream(in)){
			Object accountId = input.readObject();
			
			if(accountId instanceof Integer) {				
				this.realLength = in.available();

				if(realLength > SIZE_KEY) {
					
					in.skip(SIZE_KEY);
					_input = new ObjectInputStream(in);
					
					Object sealedObj = _input.readObject();
					
					valid = sealedObj instanceof SealedObject;
				}					
			}
		}catch(IOException | ClassNotFoundException ex) {		
			ex.printStackTrace();
		}finally {
			if(_input != null) {
				try {
					_input.close();
				} catch (IOException e) {	}
			}
		}
		
		if(!valid) {
			throw new BackupException("Backup file corrupt or not valid");
		}
	}
	
	public static Backup toBackup(File file) throws BackupException {
		return new Backup(file.toString());
	}
	
	public static Backup toBackup(Path file) throws BackupException {
		return new Backup(file.toString());
	}
	
	public long realLength() {
		return this.realLength;
	}
}
