/**
 * 
 */
package net.anthavio.kitty.state;

import java.util.Date;

/**
 * @author vanek
 *
 */
public class ExecutionCriteria {

	public static enum ExeState {
		ALL, FAILED, PASSED;
	}

	private ExeState exeState = ExeState.ALL;

	private Date startDate;

	private Date endDate;

	public ExeState getExeState() {
		return exeState;
	}

	public void setExeState(ExeState exeState) {
		this.exeState = exeState;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
