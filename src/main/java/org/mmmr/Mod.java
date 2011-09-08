package org.mmmr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

@XmlRootElement(name = "mod")
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "version" }) })
public class Mod {
    @Version
    private Integer ver;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String version;

    private String archive;

    private String url;

    private String description;

    @OneToMany(cascade = { CascadeType.ALL })
    private List<Resource> resources;

    @OneToMany(cascade = { CascadeType.ALL })
    private List<Dependency> dependencies;

    private Date installationDate;

    public Mod(String archive) {
        this();
        this.archive = archive;
    }

    public Mod(String name, String version, String url) {
        this();
        this.name = name;
        this.version = version;
        this.url = url;
    }

    public Mod(String name, String version) {
        this();
        this.name = name;
        this.version = version;
    }

    public Mod() {
        super();
    }

    @XmlAttribute(required = true)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(required = true)
    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlAttribute
    public String getArchive() {
        return this.archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<Dependency> getDependencies() {
        return this.dependencies;
    }

    public void addDepencency(Dependency dependency) {
        if (getDependencies() == null)
            dependencies = new ArrayList<Dependency>();
        getDependencies().add(dependency);
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public void addResource(Resource resource) {
        if (getResources() == null)
            resources = new ArrayList<Resource>();
        getResources().add(resource);
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<Resource> getResources() {
        return this.resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getArchive() == null) ? 0 : this.getArchive().hashCode());
        result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
        result = prime * result + ((this.getVersion() == null) ? 0 : this.getVersion().hashCode());
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
        Mod other = (Mod) obj;
        if (this.getArchive() == null) {
            if (other.getArchive() != null)
                return false;
        } else if (!this.getArchive().equals(other.getArchive()))
            return false;
        if (this.getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!this.getName().equals(other.getName()))
            return false;
        if (this.getVersion() == null) {
            if (other.getVersion() != null)
                return false;
        } else if (!this.getVersion().equals(other.getVersion()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Mod [name=" + getName() + ", version=" + getVersion() + ", description=" + getDescription() + ", archive=" + getArchive() + ", url="
                + getUrl() + ", installationDate=" + getInstallationDate() + ", resources=" + getResources() + ", dependencies=" + getDependencies()
                + "]";
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
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlAttribute
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlElement(name = "installationdate")
    public Date getInstallationDate() {
        return this.installationDate;
    }

    public void setInstallationDate(Date installationDate) {
        this.installationDate = installationDate;
    }
}
