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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;

/**
 * @author Jurgen
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(uniqueConstraints = { @UniqueConstraint(name = "dependency_name_version", columnNames = { "name", "version" }) })
public abstract class Dependency implements Comparable<Dependency>, PersistentObject {
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

    protected void setMod(Mod mod) {
	this.mod = mod;
    }

    public void setName(String name) {
	this.name = name;
    }

    protected void setResource(Resource resource) {
	this.resource = resource;
    }

    protected void setVer(Integer ver) {
	this.ver = ver;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this).append("name", name).append("version", version).append("mod", mod).append("resource", resource).toString();
    }
}
