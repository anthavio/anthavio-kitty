package net.anthavio.kitty.test;

import java.io.File;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import net.anthavio.kitty.scenario.Scenario;
import net.anthavio.kitty.scenario.Step;
import net.anthavio.kitty.step.WaitStep;

import org.springframework.beans.factory.annotation.Configurable;


/**
 * Example implementation of Kitty Scenario
 * 
 * @author vanek
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Configurable
public class ExampleScenario extends Scenario {

	@XmlElements({//zde je treba vyjmenovat vsechny implementace ScenarioStep 
	@XmlElement(name = "WaitStep", type = WaitStep.class) })
	private List<Step> steps;

	@Override
	public List<Step> getSteps() {
		return steps;
	}

	@Override
	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public ExampleScenario() {
		//jaxb
	}

	public ExampleScenario(String id, List<Step> steps, File baseDir) {
		super(id, steps, baseDir);
	}

	public ExampleScenario(String id, List<Step> steps) {
		super(id, steps);
	}

}
