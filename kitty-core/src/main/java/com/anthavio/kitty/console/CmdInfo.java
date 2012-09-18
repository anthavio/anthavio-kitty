/**
 * 
 */
package com.anthavio.kitty.console;

import java.util.Arrays;
import java.util.List;

/**
 * @author vanek
 *
 */
public class CmdInfo {

	private final List<String> keys;

	private final String description;

	public CmdInfo(List<String> keys, String description) {
		this.keys = keys;
		this.description = description;
	}

	public CmdInfo(String key, String description) {
		this.keys = Arrays.asList(key);
		this.description = description;
	}

	public List<String> getKeys() {
		return keys;
	}

	public String getDescription() {
		return description;
	}

}
