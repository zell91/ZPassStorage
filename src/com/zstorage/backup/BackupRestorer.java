package com.zstorage.backup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.zstorage.backup.exception.AlreadyExistsAccountException;
import com.zstorage.backup.exception.BackupException;
import com.zstorage.credential.Account;
import com.zstorage.credential.Password;
import com.zstorage.credential.User;
import com.zstorage.login.Login;

public class BackupRestorer {
	
	private static final BackupRestorer restorer = new BackupRestorer();
	
	private BackupRestorer() {}
	
	public static BackupRestorer getDefault() {
		return restorer;
	}
	
	public File restore(Backup backup, User user, Password password) throws BackupException{		
		File tmpFile = null;

		try(BufferedInputStream is = new BufferedInputStream(new FileInputStream(backup));
			ObjectInputStream in = new ObjectInputStream(is)){
			
			in.readObject();
						
			Files.copy(is, (tmpFile = File.createTempFile("zuser", ".tmp")).toPath(), StandardCopyOption.REPLACE_EXISTING);			
		}catch(IOException | ClassNotFoundException ex) {
			throw new BackupException("Backup file corrupt or not valid");
		}
		
		Login login = Login.getDefault();
		
		Account account = login.login(user, password, tmpFile);
		
		if(account != null) {
			if(account.getFileAccount().exists()) {
				if(Login.checkUser(user))
					throw new AlreadyExistsAccountException();
				else
					account.generateHashCode();
			}
			
			account.store();
			
			return account.getFileAccount();
		}
		
		return null;
	}


}
