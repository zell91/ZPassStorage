package com.zstorage.backup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.zstorage.backup.exception.AccountFileNotFoundException;
import com.zstorage.backup.exception.BackupException;
import com.zstorage.credential.Account;

public class BackupExporter {
	
	private static final BackupExporter exporter = new BackupExporter();
	
	private BackupExporter() { }
	
	public static final BackupExporter getDefault() {
		return exporter;
	}
	
	public Backup export(Account account, File dest) throws BackupException {
		File accountFile = account.getFileAccount();
		
		if(!accountFile.exists()) {
			throw new AccountFileNotFoundException();
		}
		
		final Integer accountId = account.hashCode();
		
		try(FileOutputStream os = new FileOutputStream(dest);
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(accountFile));
			ObjectOutputStream out = new ObjectOutputStream(os)){
			
			out.writeObject(accountId);

			int read = 0;

			while((read = in.read()) != -1) os.write(read);
			
		} catch (IOException e) {
			throw new BackupException(e.getMessage());
		}

		return Backup.toBackup(dest);
	}

}
