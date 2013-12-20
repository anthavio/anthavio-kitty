package net.anthavio.tap.svc;

import java.util.List;

import javax.inject.Inject;

import net.anthavio.aspect.Logged;
import net.anthavio.aspect.NullCheck;
import net.anthavio.dao.search.PagedResult;
import net.anthavio.tap.db.ExampleEntity;
import net.anthavio.tap.db.ExampleEntityDao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author vanek
 * 
 */
@Logged
@Service
public class TapService {

	@Value("#{environment['config.value']}")
	private Integer configValue;

	@Value("#{environment['tap.ext.dir']}")
	private String tapEtxDir;

	@Inject
	private ExampleEntityDao dao;

	// @Inject
	// private SpringConfig config;

	@Transactional
	public void create(@NullCheck ExampleEntity entity) {
		dao.persist(entity);
	}

	@Transactional
	public void update(@NullCheck ExampleEntity entity) {
		dao.merge(entity);
	}

	@Transactional
	public void delete(@NullCheck Integer id) {
		dao.removeById(id);
	}

	@Transactional(readOnly = true)
	public PagedResult<ExampleEntity> search(ExampleSearchCriteria criteria) {
		return dao.search(criteria);
	}

	@Transactional(readOnly = true)
	public List<ExampleEntity> findAll() {
		List<ExampleEntity> list = dao.findAll();
		return list;
	}

	@Transactional(readOnly = true)
	public ExampleEntity findById(@NullCheck Integer id) {
		return dao.find(id);
	}

	public String getConfigValue() {
		return "config.value = " + configValue + ", tap.ext.dir = " + tapEtxDir /*
																																						 * +
																																						 * " config = "
																																						 * +
																																						 * config
																																						 * .
																																						 * getEnvInt
																																						 * (
																																						 * "config.value"
																																						 * )
																																						 */;
	}
}
