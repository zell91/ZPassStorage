package com.zstorage.backup.exception;

public class AccountFileNotFoundException extends BackupException{

	private static final long serialVersionUID = 1L;

	public AccountFileNotFoundException() {
		super("Account not found in the File System.");
	}

}
