package net.anthavio.tap.db;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;

/**
 * @author vanek
 *
 */
public abstract class BaseDao<T, ID extends Serializable> extends GenericDAOImpl<T, ID> {

	@Inject
	@Named("PdbDataSource")
	protected DataSource dataSource;

	@Inject
	@Named("PdbJdbcTemplate")
	protected JdbcTemplate jdbcTemplate;

	@Override
	@PersistenceContext(unitName = "PdbUnit")
	public void setEntityManager(EntityManager entityManager) {
		super.setEntityManager(entityManager);
	}

	/*
	@Inject
	@Named("PdbSearchProcessor")
	@Override
	public void setSearchProcessor(JPASearchProcessor searchProcessor) {
		super.setSearchProcessor(searchProcessor);
	}
	 */

	public EntityManager getEntityManager() {
		return em();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
