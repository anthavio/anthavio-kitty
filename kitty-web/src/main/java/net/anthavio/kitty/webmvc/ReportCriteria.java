/**
 * 
 */
package net.anthavio.kitty.webmvc;

import net.anthavio.kitty.state.ExecutionCriteria;

/**
 * @author vanek
 *
 */
public class ReportCriteria extends ExecutionCriteria {

	public static enum Format {
		HTML, EXCEL;
	}

	private Format format = Format.EXCEL;

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	@Override
	public String toString() {
		return "ReportCriteria [format=" + format + ", getExeState()=" + getExeState() + ", getStartDate()="
				+ getStartDate() + ", getEndDate()=" + getEndDate() + "]";
	}

}
