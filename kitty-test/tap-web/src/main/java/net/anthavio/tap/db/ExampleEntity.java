package net.anthavio.tap.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.anthavio.log.ToStringBuilder;

import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author vanek
 * 
 */
@Entity
@Table(name = "EXAMPLE")
public class ExampleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Integer id;

	@Size(min = 5, max = 1000)
	@Column(name = "TEXT")
	private String text;

	@NotNull
	@Column(name = "DATUM")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "d.M.yyyy")
	private Date datum;

	public ExampleEntity() {
		// default
	}

	public ExampleEntity(String text) {
		this.text = text;
		this.datum = new Date();
	}

	public ExampleEntity(String text, Date datum) {
		this.text = text;
		this.datum = datum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ExampleEntity other = (ExampleEntity) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	@Override
	public String toString() {
		return ToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
