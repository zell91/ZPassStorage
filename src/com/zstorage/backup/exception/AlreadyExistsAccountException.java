package com.zstorage.backup.exception;

public class AlreadyExistsAccountException extends BackupException{

	private static final long serialVersionUID = 1L;

	public AlreadyExistsAccountException() {
		super("Account already exists on File System.");
	}

}
