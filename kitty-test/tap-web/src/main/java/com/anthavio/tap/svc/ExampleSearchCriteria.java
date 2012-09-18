package com.anthavio.tap.svc;

import com.anthavio.dao.search.PagedCriteria;
import com.anthavio.dao.search.criteria.DateCriteria;
import com.anthavio.dao.search.criteria.LikeCriteria;

public class ExampleSearchCriteria extends PagedCriteria {

	private Integer id;

	private LikeCriteria textCriteria;

	private DateCriteria dateCriteria;

	public ExampleSearchCriteria() {
		//default
	}

	public ExampleSearchCriteria(Integer offset, Integer limit) {
		super(offset, limit);
	}

	public DateCriteria getDateCriteria() {
		return dateCriteria;
	}

	public void setDateCriteria(DateCriteria dateCriteria) {
		this.dateCriteria = dateCriteria;
	}

	public LikeCriteria getTextCriteria() {
		return textCriteria;
	}

	public void setTextCriteria(LikeCriteria textLike) {
		this.textCriteria = textLike;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
