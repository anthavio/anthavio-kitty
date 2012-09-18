/**
 * 
 */
package com.anthavio.kitty.state;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.anthavio.kitty.KittyException;
import com.anthavio.kitty.model.ExecutionStats;
import com.anthavio.kitty.scenario.ScenarioExecution;
import com.anthavio.kitty.state.ExecutionCriteria.ExeState;
import com.anthavio.util.DateUtil;

/**
 * @author vanek
 *
 */
//@Component
public class ExecutionDao {

	private static final ScExRowMapper mapper = new ScExRowMapper();

	@Inject
	@Named("KittyJdbcTemplate")
	private JdbcTemplate jdbc;

	public int execStart(ScenarioExecution se) {
		String canonicalPath = se.getFileCannonicalPath();
		int id = jdbc.queryForInt("SELECT SEQ_SCENARIO_EXECUTION.nextval FROM DUAL");
		se.setId(id);
		String sql = "INSERT INTO SCENARIO_EXECUTION (ID, FILE_PATH, START_TS) VALUES(?, ?, ?)";
		jdbc.update(sql, id, canonicalPath, se.getStarted());
		return id;
	}

	public void execEnded(ScenarioExecution se) {
		String sql = "UPDATE SCENARIO_EXECUTION SET END_TS = ?, ERR_STEP = ?, ERR_MSG = ? WHERE ID = ?";
		jdbc.update(sql, se.getEnded(), se.getErrorStep(), se.getErrorMessage(), se.getId());
	}

	public void insert(ScenarioExecution se) {
		String canonicalPath = getCannonicalPath(se.getFile());
		String sql = "INSERT INTO SCENARIO_EXECUTION (FILE_PATH, START_TS, END_TS, ERR_STEP, ERR_MSG) VALUES(?,?,?,?,?)";
		jdbc.update(sql, canonicalPath, se.getStarted(), se.getEnded(), se.getErrorStep(), se.getErrorMessage());
	}

	public List<ScenarioExecution> list(int last) {
		String sql = "SELECT * FROM SCENARIO_EXECUTION ORDER BY START_TS LIMIT " + last;
		return jdbc.query(sql, mapper);
	}

	public ExecutionStats getStats() {
		String sql = "SELECT COUNT(*) FROM SCENARIO_EXECUTION";
		int totalCnt = jdbc.queryForInt(sql);
		int totalErr = jdbc.queryForInt(sql + " WHERE ERR_MSG IS NOT NULL");
		Date now = new Date();
		Date startDt = DateUtil.getStartOfDay(now);
		Date endDt = DateUtil.getEndOfDay(now);
		int todayCnt = jdbc.queryForInt(sql + " WHERE START_TS >=? AND START_TS <= ?", startDt, endDt);
		int todayErr = jdbc.queryForInt(sql + " WHERE START_TS >=? AND START_TS <= ? AND ERR_MSG IS NOT NULL", startDt,
				endDt);
		return new ExecutionStats(totalCnt, totalErr, todayCnt, todayErr);
	}

	public List<ScenarioExecution> list(ExecutionCriteria criteria) {
		WhereWithArgs where = buildWhere(criteria);
		String sql = "SELECT * FROM SCENARIO_EXECUTION";
		if (where.isEmpty() == false) {
			sql += " WHERE " + where;
		}
		sql += " ORDER BY START_TS DESC";

		if (where.isEmpty()) {
			sql += " LIMIT 100";
		}
		return jdbc.query(sql, where.getArgsArray(), mapper);
	}

	public int delete(ExecutionCriteria criteria) {
		WhereWithArgs where = buildWhere(criteria);
		String sql = "DELETE FROM SCENARIO_EXECUTION";
		if (where.isEmpty() == false) {
			sql += " WHERE " + where;
		}
		return jdbc.update(sql, where.getArgsArray());
	}

	public WhereWithArgs buildWhere(ExecutionCriteria options) {
		WhereWithArgs where = new WhereWithArgs();
		if (options.getExeState() == ExeState.PASSED) {
			where.and("ERR_MSG IS NULL");
		} else if (options.getExeState() == ExeState.FAILED) {
			where.and("ERR_MSG IS NOT NULL");
		}

		if (options.getStartDate() != null) {
			options.setStartDate(DateUtil.getStartOfDay(options.getStartDate()));
			where.and("START_TS >= ?", options.getStartDate());
		}
		if (options.getEndDate() != null) {
			options.setEndDate(DateUtil.getEndOfDay(options.getEndDate()));
			where.and("START_TS <= ?", options.getEndDate());
		}
		return where;
	}

	private static class WhereWithArgs {

		private StringBuilder where = new StringBuilder();

		private List<Object> args = new ArrayList<Object>();

		public void and(String sqlFragment) {
			if (where.length() > 0) {
				where.append(" AND ");
			}
			where.append(sqlFragment);
		}

		public void and(String sqlFragment, Object arg) {
			if (arg == null) {
				throw new IllegalArgumentException("Arg is null for " + sqlFragment);
			}
			args.add(arg);
			and(sqlFragment);
		}

		public String getWhere() {
			return where.toString();
		}

		public Object[] getArgsArray() {
			if (args.size() > 0) {
				return args.toArray(new Object[args.size()]);
			} else {
				return null;
			}
		}

		public List<Object> getArgs() {
			return args;
		}

		public boolean isEmpty() {
			return where.length() == 0;
		}

		@Override
		public String toString() {
			return getWhere();
		}

	}

	private static class ScExRowMapper implements RowMapper<ScenarioExecution> {

		@Override
		public ScenarioExecution mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new ScenarioExecution(new File(rs.getString("FILE_PATH")),// 
					rs.getTimestamp("START_TS"), rs.getTimestamp("END_TS"),//
					rs.getString("ERR_STEP"), rs.getString("ERR_MSG"));
		}
	}

	public static String getCannonicalPath(File f) {
		try {
			return f.getCanonicalPath();
		} catch (IOException iox) {
			throw new KittyException(iox);
		}
	}

}
