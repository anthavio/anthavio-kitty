package net.anthavio.kitty.scenario;

import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * 
 * @author vanek
 *
 */
public interface ScenarioBinder {

	public Scenario load(Reader reader);
	
	public void save(Scenario scenario, Writer writer);
	
	public void save(Scenario scenario, OutputStream stream);
}
