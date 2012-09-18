package com.anthavio.tap.db;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mysema.query.jpa.impl.JPAQuery;

import com.anthavio.dao.search.PagedResult;
import com.anthavio.dao.search.QueryDslSearch;
import com.anthavio.tap.svc.ExampleSearchCriteria;

/**
 * @author vanek
 *
 */
@Repository
public class ExampleEntityDao extends BaseDao<ExampleEntity, Integer> {

	public List<ExampleEntity> findByText(String jmeno) {
		QExampleEntity entity = QExampleEntity.exampleEntity;
		JPAQuery query = new JPAQuery(em());
		return query.from(entity).where(entity.text.like(jmeno)).list(entity);
	}

	public PagedResult<ExampleEntity> search(ExampleSearchCriteria criteria) {
		QueryDslSearch search = new QueryDslSearch(em());
		QExampleEntity entity = QExampleEntity.exampleEntity;
		search.from(entity);

		search.eq(entity.id, criteria.getId());
		search.like(entity.text, criteria.getTextCriteria());
		search.range(entity.datum, criteria.getDateCriteria());

		return search.listPaged(entity, criteria);

	}
}
