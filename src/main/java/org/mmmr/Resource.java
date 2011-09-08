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

@XmlRootElement
@Entity
public class Resource {
    @Version
    private Integer ver;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String sourcePath;

    private String targetPath;

    @OneToMany(cascade = { CascadeType.ALL })
    private List<Dependency> dependencies;

    private String include;

    private String exclude;

    @OneToMany(cascade = { CascadeType.ALL })
    private List<MCFile> files;

    public Resource() {
        super();
    }

    public Resource(String sourcePath, String targetPath) {
        this();
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }

    @XmlAttribute(name = "sourcepath")
    public String getSourcePath() {
        return this.sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @XmlAttribute(name = "targetpath")
    public String getTargetPath() {
        return this.targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public void addDependency(Dependency dependency) {
        if (getDependencies() == null)
            dependencies = new ArrayList<Dependency>();
        getDependencies().add(dependency);
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<Dependency> getDependencies() {
        return this.dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return "Resource [sourcePath=" + this.getSourcePath() + ", targetPath=" + this.getTargetPath() + ", dependencies=" + this.getDependencies()
                + ", files=" + this.getFiles() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getSourcePath() == null) ? 0 : this.getSourcePath().hashCode());
        result = prime * result + ((this.getTargetPath() == null) ? 0 : this.getTargetPath().hashCode());
        return result;
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

    @XmlTransient
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlTransient
    public Integer getVer() {
        return this.ver;
    }

    public void setVer(Integer ver) {
        this.ver = ver;
    }

    @XmlAttribute
    public String getInclude() {
        return this.include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    @XmlAttribute
    public String getExclude() {
        return this.exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    public void addFile(MCFile file) {
        if (getFiles() == null)
            files = new ArrayList<MCFile>();
        getFiles().add(file);
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<MCFile> getFiles() {
        return this.files;
    }

    public void setFiles(List<MCFile> files) {
        this.files = files;
    }
}
