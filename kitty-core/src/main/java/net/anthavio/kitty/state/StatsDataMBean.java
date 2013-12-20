/**
 * 
 */
package net.anthavio.kitty.state;

import java.util.List;

/**
 * @author vanek
 *
 */
public interface StatsDataMBean {

	public abstract List<Long> getLast();

	/**
	 * @return the xfail
	 */
	public abstract int getXfail();

	/**
	 * @return the xavrg
	 */
	public abstract int getXavrg();

	/**
	 * @return the m1done
	 */
	public abstract int getM1done();

	/**
	 * @return the m1fail
	 */
	public abstract int getM1fail();

	/**
	 * @return the m1avrg
	 */
	public abstract int getM1avrg();

	/**
	 * @return the h1done
	 */
	public abstract int getH1done();

	/**
	 * @return the h1fail
	 */
	public abstract int getH1fail();

	/**
	 * @return the h1avrg
	 */
	public abstract int getH1avrg();

}