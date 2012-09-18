/**
 * 
 */
package com.anthavio.kitty.state;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author vanek
 *
 */
public class StatsData implements StatsDataMBean {

	private static final long serialVersionUID = 3265140106490607334L;

	public List<Long> last = new CopyOnWriteArrayList<Long>();

	public int xdone = 0;

	public int xfail = 0;

	public int xavrg = 0;

	public long m1ts = System.currentTimeMillis();

	public int m1done = 0;

	public int m1fail = 0;

	public int m1avrg = 0;

	public long h1ts = m1ts;

	public int h1done = 0;

	public int h1fail = 0;

	public int h1avrg = 0;

	private String id;

	public StatsData(String id) {
		this.id = id;
	}

	public void update(long milis) {

		tryH1M1();

		if (milis < 0) {
			//zaporny cas znaci chybu
			++xfail;
			++m1fail;
			++h1fail;
		} else {
			last.add(milis);
			if (last.size() > 10) {
				last.remove(0);
			}
			xavrg = (int) ((xavrg * xdone + milis) / ++xdone);
			m1avrg = (int) ((m1avrg * m1done + milis) / ++m1done);
			h1avrg = (int) ((h1avrg * h1done + milis) / ++h1done);
		}

	}

	public void tryH1M1() {
		long now = System.currentTimeMillis();
		if (now > m1ts + 60000) {
			//CtToolStats.loadLog.info("STAT1M: " + id + " done: " + m1done + " fail: " + m1fail + " avrg: " + m1avrg);
			m1done = 0;
			m1fail = 0;
			m1avrg = 0;
			m1ts = now;
		}
		if (now > h1ts + 60000 * 60) {
			//CtToolStats.loadLog.info("STAT1H: " + id + " done: " + m1done + " fail: " + m1fail + " avrg: " + m1avrg);
			h1done = 0;
			h1fail = 0;
			h1avrg = 0;
			h1ts = now;
		}
	}

	public List<Long> getLast() {
		return last;
	}

	public int getXdone() {
		return xdone;
	}

	public int getXfail() {
		return xfail;
	}

	public int getXavrg() {
		return xavrg;
	}

	public int getM1done() {
		return m1done;
	}

	public int getM1fail() {
		return m1fail;
	}

	public int getM1avrg() {
		return m1avrg;
	}

	public int getH1done() {
		return h1done;
	}

	public int getH1fail() {
		return h1fail;
	}

	public int getH1avrg() {
		return h1avrg;
	}

}
