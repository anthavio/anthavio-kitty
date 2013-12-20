/**
 * 
 */
package net.anthavio.kitty.step;

import static org.testng.Assert.fail;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import net.anthavio.kitty.scenario.Step;


/**
 * @author vanek
 *
 */
public abstract class WaitForConditionStep extends Step {

	@XmlAttribute
	private Integer timeout; //milliseconds

	@XmlAttribute
	private Integer interval;//milliseconds

	@XmlTransient
	private boolean done = false;

	@XmlTransient
	private AssertionError error;

	private transient Object[] lock = new Object[0];

	@Override
	public void execute() throws Exception {
		//reset state to allow multiexecution
		this.done = false;
		this.error = null;

		int timeout = value(this.timeout, 60000);
		int interval = value(this.interval, 1000);
		long started = System.currentTimeMillis();
		try {
			do {
				boolean checked = check();
				if (checked || this.done || this.error != null || (System.currentTimeMillis() - started) > timeout) {
					break;
				}
				synchronized (lock) {
					lock.wait(interval);
				}
			} while (true);

		} catch (Exception x) {
			error = new AssertionError(x);
		}

		if (error != null) {
			throw error;
		}

		if (!done) {
			fail("Wait timeout: " + timeout + " ms");
		}
	}

	protected abstract boolean check();

	public void setDone(boolean done) {
		this.done = done;
		if (done) {
			synchronized (lock) {
				lock.notify();
			}
		}
	}

	public void setError(String message) {
		this.error = new AssertionError(message);
		setDone(true);
	}

	public void setError(Exception x) {
		this.error = new AssertionError(x);
		setDone(true);
	}

	public void setError(AssertionError error) {
		this.error = error;
		setDone(true);
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public boolean isDone() {
		return done;
	}

	public AssertionError getError() {
		return error;
	}

}
