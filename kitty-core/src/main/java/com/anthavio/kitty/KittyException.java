/**
 * 
 */
package com.anthavio.kitty;

/**
 * @author vanek
 *
 */
public class KittyException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public KittyException(String message, Throwable cause) {
		super(message, cause);
	}

	public KittyException(String message) {
		super(message);
	}

	public KittyException(Throwable cause) {
		super(cause);
	}

}
