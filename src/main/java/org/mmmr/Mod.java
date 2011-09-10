package org.mmmr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
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
@Table(uniqueConstraints = { @UniqueConstraint(name = "mod_name_version", columnNames = { "name", "version" }) })
public class Mod implements Comparable<Mod>, PersistentObject {
    private String archive;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "mod")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private List<Dependency> dependencies;

    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date installationDate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private ModPack modPack;

    @Column(nullable = false)
    private String name;

    private String resourceCheck;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "mod")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private List<Resource> resources;

    private String url;

    @Version
    private Integer ver;

    @Column(nullable = false)
    private String version;

    public Mod() {
	super();
    }

    public Mod(String archive) {
	this();
	this.archive = archive;
    }

    public Mod(String name, String version) {
	this();
	this.name = name;
	this.version = version;
    }

    public Mod(String name, String version, String url) {
	this();
	this.name = name;
	this.version = version;
	this.url = url;
    }

    public Mod(String name, String version, String url, String resourceCheck) {
	this();
	this.name = name;
	this.version = version;
	this.url = url;
	this.resourceCheck = resourceCheck;
    }

    public void addDepencency(Dependency dependency) {
	if (getDependencies() == null)
	    dependencies = new ArrayList<Dependency>();
	getDependencies().add(dependency);
	dependency.setMod(this);
    }

    public void addResource(Resource resource) {
	if (getResources() == null)
	    resources = new ArrayList<Resource>();
	getResources().add(resource);
	resource.setMod(this);
    }

    public int compareTo(final Mod other) {
	return new CompareToBuilder().append(name, other.name).append(version, other.version).toComparison();
    }

    @Override
    public boolean equals(final Object other) {
	if (!(other instanceof Mod))
	    return false;
	Mod castOther = (Mod) other;
	return new EqualsBuilder().append(name, castOther.name).append(version, castOther.version).isEquals();
    }

    @XmlAttribute
    public String getArchive() {
	return this.archive;
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<Dependency> getDependencies() {
	return this.dependencies;
    }

    @XmlAttribute
    public String getDescription() {
	return this.description;
    }

    @XmlTransient
    public Long getId() {
	return this.id;
    }

    @XmlTransient
    public Date getInstallationDate() {
	return this.installationDate;
    }

    @XmlTransient
    public ModPack getModPack() {
	return modPack;
    }

    @XmlAttribute(required = true)
    public String getName() {
	return this.name;
    }

    @XmlElement(name = "resourcecheck")
    public String getResourceCheck() {
	return resourceCheck;
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<Resource> getResources() {
	return this.resources;
    }

    @XmlAttribute
    public String getUrl() {
	return url;
    }

    @XmlTransient
    public Integer getVer() {
	return this.ver;
    }

    @XmlAttribute(required = true)
    public String getVersion() {
	return this.version;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(name).append(version).toHashCode();
    }

    public boolean isInstalled() {
	return getInstallationDate() != null;
    }

    public void setArchive(String archive) {
	this.archive = archive;
    }

    public void setDependencies(List<Dependency> dependencies) {
	this.dependencies = dependencies;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    protected void setId(Long id) {
	this.id = id;
    }

    public void setInstallationDate(Date installationDate) {
	this.installationDate = installationDate;
    }

    protected void setModPack(ModPack modPack) {
	this.modPack = modPack;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setResourceCheck(String resourceCheck) {
	this.resourceCheck = resourceCheck;
    }

    public void setResources(List<Resource> resources) {
	this.resources = resources;
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
	return new ToStringBuilder(this).append("name", name).append("version", version).append("archive", archive).append("description", description)
		.append("installationDate", installationDate).append("modPack", modPack).append("url", url).toString();
    }
}
