package org.mmmr;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Cascade;

/**
 * @author Jurgen
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Dependency implements PersistentObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private Mod mod;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private Resource resource;

    @Version
    private Integer ver;

    @XmlTransient
    public Long getId() {
	return this.id;
    }

    @XmlTransient
    public Mod getMod() {
	return mod;
    }

    @XmlTransient
    public Resource getResource() {
	return resource;
    }

    @XmlTransient
    public Integer getVer() {
	return this.ver;
    }

    protected void setId(Long id) {
	this.id = id;
    }

    protected void setMod(Mod mod) {
	this.mod = mod;
    }

    protected void setResource(Resource resource) {
	this.resource = resource;
    }

    protected void setVer(Integer ver) {
	this.ver = ver;
    }
}
