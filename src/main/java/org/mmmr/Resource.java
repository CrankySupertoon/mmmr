package org.mmmr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAttribute;
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
@XmlRootElement
@Entity
public class Resource implements Comparable<Resource>, PersistentObject {
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "resource")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private List<Dependency> dependencies;

    private String exclude;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "resource")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private List<MCFile> files;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String include;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private Mod mod;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private ModPack modPack;

    private String sourcePath;

    private String targetPath;

    @Version
    private Integer ver;

    public Resource() {
	super();
    }

    public Resource(String sourcePath, String targetPath) {
	this();
	this.sourcePath = sourcePath;
	this.targetPath = targetPath;
    }

    public void addDependency(Dependency dependency) {
	if (this.getDependencies() == null) {
	    this.dependencies = new ArrayList<Dependency>();
	}
	this.getDependencies().add(dependency);
	dependency.setResource(this);
    }

    public void addFile(MCFile file) {
	if (this.getFiles() == null) {
	    this.files = new ArrayList<MCFile>();
	}
	this.getFiles().add(file);
	file.setResource(this);
    }

    @Override
    public int compareTo(final Resource other) {
	return new CompareToBuilder().append(this.sourcePath, other.sourcePath).append(this.targetPath, other.targetPath).toComparison();
    }

    @Override
    public boolean equals(final Object other) {
	if (!(other instanceof Resource)) {
	    return false;
	}
	Resource castOther = (Resource) other;
	return new EqualsBuilder().append(this.sourcePath, castOther.sourcePath).append(this.targetPath, castOther.targetPath).isEquals();
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<Dependency> getDependencies() {
	return this.dependencies;
    }

    @XmlAttribute
    public String getExclude() {
	return this.exclude;
    }

    @XmlTransient
    public List<MCFile> getFiles() {
	return this.files;
    }

    @Override
    @XmlTransient
    public Long getId() {
	return this.id;
    }

    @XmlAttribute
    public String getInclude() {
	return this.include;
    }

    @XmlTransient
    public Mod getMod() {
	return this.mod;
    }

    @XmlTransient
    public ModPack getModPack() {
	return this.modPack;
    }

    @XmlAttribute(name = "sourcepath")
    public String getSourcePath() {
	return this.sourcePath;
    }

    @XmlAttribute(name = "targetpath")
    public String getTargetPath() {
	return this.targetPath;
    }

    @XmlTransient
    public Integer getVer() {
	return this.ver;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(this.sourcePath).append(this.targetPath).toHashCode();
    }

    public void setDependencies(List<Dependency> dependencies) {
	this.dependencies = dependencies;
    }

    public void setExclude(String exclude) {
	this.exclude = exclude;
    }

    public void setFiles(List<MCFile> files) {
	this.files = files;
    }

    protected void setId(Long id) {
	this.id = id;
    }

    public void setInclude(String include) {
	this.include = include;
    }

    public void setMod(Mod mod) {
	this.mod = mod;
    }

    protected void setModPack(ModPack modPack) {
	this.modPack = modPack;
    }

    public void setSourcePath(String sourcePath) {
	this.sourcePath = sourcePath;
    }

    public void setTargetPath(String targetPath) {
	this.targetPath = targetPath;
    }

    protected void setVer(Integer ver) {
	this.ver = ver;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this).append("exclude", this.exclude).append("include", this.include).append("mod", this.mod).append("modPack", this.modPack)
		.append("sourcePath", this.sourcePath).append("targetPath", this.targetPath).toString();
    }
}
