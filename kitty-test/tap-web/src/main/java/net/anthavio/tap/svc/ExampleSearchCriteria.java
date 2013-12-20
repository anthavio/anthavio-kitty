package net.anthavio.tap.svc;

import net.anthavio.dao.search.PagedCriteria;
import net.anthavio.dao.search.criteria.DateCriteria;
import net.anthavio.dao.search.criteria.LikeCriteria;

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
