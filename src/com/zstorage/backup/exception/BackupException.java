package com.zstorage.backup.exception;

public class BackupException extends Exception {

	private static final long serialVersionUID = 1L;

	public BackupException(String mess) {
		super(mess);
	}

	public BackupException() {
		super();
	}

}
