package com.zstorage.credential;

import java.io.Serializable;

public abstract class Credential implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private char[] credential;

	public Credential(char[] credential) {
		this.credential = credential;
	}
	
	public char[] getCredential() {
		return credential;
	}	
	
	@Override
	public boolean equals(Object object) {
		if(!(object instanceof Credential))
			return false;
				
		return new String(this.credential).equals(new String(((Credential)object).credential));
	}
	
}
