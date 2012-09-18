package com.anthavio.kitty.step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.anthavio.aspect.ApiPolicyOverride;
import com.anthavio.kitty.scenario.Step;

/**
 * @author vanek
 * 
 * Ceka zadany pocet sekund nebo na stisk klavesy Enter
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WaitStep extends Step {

	@XmlAttribute
	private Integer seconds;

	private String message;

	@XmlAttribute
	private Boolean enter;

	protected WaitStep() {
		//jaxb
	}

	public WaitStep(int seconds) {
		this.seconds = seconds;
	}

	public WaitStep(Integer seconds, String message) {
		this.seconds = seconds;
		this.message = message;
	}

	public WaitStep(boolean enter) {
		this.enter = enter;
	}

	public WaitStep(boolean enter, String message) {
		this.enter = enter;
		this.message = message;
	}

	@Override
	public void init() throws Exception {
		if (seconds == null && enter == null) {
			throw new IllegalStateException("seconds or enter property must be enabled");
		}
	}

	@ApiPolicyOverride
	public void execute() throws Exception {
		if (message != null) {
			System.out.println(message);
		}

		if (seconds != null) {
			Thread.sleep(seconds * 1000);
		}

		if (enter != null) {
			if (message == null) {
				System.out.println("Press Enter to continue");
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				reader.readLine();
			} catch (IOException iox) {
				iox.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		String ret = super.toString();
		if (seconds != null) {
			ret += " " + seconds + " seconds";
		}
		if (enter != null) {
			ret += " for Enter";
		}
		return ret;
	}
}
