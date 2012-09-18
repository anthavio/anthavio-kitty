/**
 * 
 */
package com.anthavio.kitty.model;

/**
 * @author vanek
 *
 */
public class ExecutionStats {

	private int totalExecuted;

	private int totalFailed;

	private int todayExecuted;

	private int todayFailed;

	public ExecutionStats(int totalExecuted, int totalFailed, int todayExecuted, int todayFailed) {
		this.totalExecuted = totalExecuted;
		this.totalFailed = totalFailed;
		this.todayExecuted = todayExecuted;
		this.todayFailed = todayFailed;
	}

	public int getTodayExecuted() {
		return todayExecuted;
	}

	public void setTodayExecuted(int todayExecuted) {
		this.todayExecuted = todayExecuted;
	}

	public int getTodayFailed() {
		return todayFailed;
	}

	public void setTodayFailed(int todayFailed) {
		this.todayFailed = todayFailed;
	}

	public int getTotalExecuted() {
		return totalExecuted;
	}

	public void setTotalExecuted(int totalExecuted) {
		this.totalExecuted = totalExecuted;
	}

	public int getTotalFailed() {
		return totalFailed;
	}

	public void setTotalFailed(int totalFailed) {
		this.totalFailed = totalFailed;
	}

}
