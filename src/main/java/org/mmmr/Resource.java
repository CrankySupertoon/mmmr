package org.mmmr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Jurgen
 */
@XmlRootElement
@Entity
public class Resource {
    @OneToMany(cascade = { CascadeType.ALL })
    private List<Dependency> dependencies;

    private String exclude;

    @OneToMany(cascade = { CascadeType.ALL })
    private List<MCFile> files;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String include;

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
	if (getDependencies() == null)
	    dependencies = new ArrayList<Dependency>();
	getDependencies().add(dependency);
    }

    public void addFile(MCFile file) {
	if (getFiles() == null)
	    files = new ArrayList<MCFile>();
	getFiles().add(file);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Resource other = (Resource) obj;
	if (this.getSourcePath() == null) {
	    if (other.getSourcePath() != null)
		return false;
	} else if (!this.getSourcePath().equals(other.getSourcePath()))
	    return false;
	if (this.getTargetPath() == null) {
	    if (other.getTargetPath() != null)
		return false;
	} else if (!this.getTargetPath().equals(other.getTargetPath()))
	    return false;
	return true;
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

    @XmlTransient
    public Long getId() {
	return this.id;
    }

    @XmlAttribute
    public String getInclude() {
	return this.include;
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
	final int prime = 31;
	int result = 1;
	result = prime * result + ((this.getSourcePath() == null) ? 0 : this.getSourcePath().hashCode());
	result = prime * result + ((this.getTargetPath() == null) ? 0 : this.getTargetPath().hashCode());
	return result;
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
	return "Resource [sourcePath=" + this.getSourcePath() + ", targetPath=" + this.getTargetPath() + ", dependencies=" + this.getDependencies() + ", files=" + this.getFiles()
		+ "]";
    }
}
