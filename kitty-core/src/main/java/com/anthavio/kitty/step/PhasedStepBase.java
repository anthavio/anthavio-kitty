/**
 * 
 */
package com.anthavio.kitty.step;

import com.anthavio.kitty.scenario.Step;

/**
 * @author vanek
 *
 */
public abstract class PhasedStepBase extends Step {

	@Override
	public final void execute() throws Exception {
		preExecute();
		doExecute();
		postExecute();
	}

	public abstract void preExecute() throws Exception;

	public abstract void doExecute() throws Exception;

	public abstract void postExecute() throws Exception;

}
