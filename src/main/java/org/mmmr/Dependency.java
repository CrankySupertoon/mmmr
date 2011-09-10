package org.mmmr;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;

/**
 * @author Jurgen
 */
@XmlRootElement(name = "mod")
@Entity
public class Dependency implements Comparable<Dependency>, PersistentObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private Mod mod;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private Resource resource;

    private String url;

    @Version
    private Integer ver;

    private String version;

    public int compareTo(final Dependency other) {
	return new CompareToBuilder().append(name, other.name).append(version, other.version).toComparison();
    }

    @Override
    public boolean equals(final Object other) {
	if (!(other instanceof Dependency))
	    return false;
	Dependency castOther = (Dependency) other;
	return new EqualsBuilder().append(name, castOther.name).append(version, castOther.version).isEquals();
    }

    @XmlTransient
    public Long getId() {
	return this.id;
    }

    @XmlTransient
    public Mod getMod() {
	return mod;
    }

    @XmlAttribute
    public String getName() {
	return name;
    }

    @XmlTransient
    public Resource getResource() {
	return resource;
    }

    @XmlAttribute
    public String getUrl() {
	return url;
    }

    @XmlTransient
    public Integer getVer() {
	return this.ver;
    }

    @XmlAttribute
    public String getVersion() {
	return version;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(name).append(version).toHashCode();
    }

    protected void setId(Long id) {
	this.id = id;
    }

    public void setMod(Mod mod) {
	this.mod = mod;
    }

    public void setName(String name) {
	this.name = name;
    }

    protected void setResource(Resource resource) {
	this.resource = resource;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    protected void setVer(Integer ver) {
	this.ver = ver;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this).append("name", name).append("version", version).toString();
    }
}
