package net.anthavio.kitty.step;

import javax.xml.bind.annotation.XmlAttribute;

import net.anthavio.kitty.scenario.Step;


/**
 * @author vanek
 * 
 * @deprecated use WaitStep
 */
@Deprecated
public class SleepStep extends Step {

	@XmlAttribute(required = true)
	private int seconds;

	protected SleepStep() {
		//jaxb
	}

	public SleepStep(int seconds) {
		this.seconds = seconds;
	}

	public void execute() throws Exception {
		Thread.sleep(seconds * 1000);
	}

}
