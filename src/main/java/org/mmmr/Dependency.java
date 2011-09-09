package org.mmmr;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Jurgen
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Dependency {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Integer ver;

    @XmlTransient
    public Long getId() {
	return this.id;
    }

    @XmlTransient
    public Integer getVer() {
	return this.ver;
    }

    protected void setId(Long id) {
	this.id = id;
    }

    protected void setVer(Integer ver) {
	this.ver = ver;
    }
}
