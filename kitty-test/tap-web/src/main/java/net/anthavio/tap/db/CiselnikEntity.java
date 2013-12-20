package net.anthavio.tap.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author vanek
 *
 */
@Entity
@Table(name = "CISELNIK")
public class CiselnikEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	private String kod;

	@Column(name = "TEXT", nullable = false)
	private String text;

	public CiselnikEntity() {
		//default
	}

	public CiselnikEntity(String kod, String text) {
		this.kod = kod;
		this.text = text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kod == null) ? 0 : kod.hashCode());
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
		CiselnikEntity other = (CiselnikEntity) obj;
		if (kod == null) {
			if (other.kod != null) {
				return false;
			}
		} else if (!kod.equals(other.kod)) {
			return false;
		}
		return true;
	}

	public String getKod() {
		return kod;
	}

	public void setKod(String kod) {
		this.kod = kod;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
