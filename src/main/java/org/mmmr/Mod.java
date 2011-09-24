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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
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
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "mod_name_version", columnNames = { "name", "version" }) })
public class Mod implements Comparable<Mod>, PersistentObject {
    private String archive;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "mod")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private List<Dependency> dependencies;

    private String description;

    /** database id */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date installationDate;

    private int installOrder;

    private String mcVersionDependency;

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

    private String actualUrl;

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
        if (this.getDependencies() == null) {
            this.dependencies = new ArrayList<Dependency>();
        }
        this.getDependencies().add(dependency);
        dependency.setMod(this);
    }

    public void addResource(Resource resource) {
        if (this.getResources() == null) {
            this.resources = new ArrayList<Resource>();
        }
        this.getResources().add(resource);
        resource.setMod(this);
    }

    @Override
    public int compareTo(final Mod other) {
        return new CompareToBuilder().append(this.name, other.name).append(this.version, other.version).toComparison();
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Mod)) {
            return false;
        }
        Mod castOther = (Mod) other;
        return new EqualsBuilder().append(this.name, castOther.name).append(this.version, castOther.version).isEquals();
    }

    @XmlTransient
    public String getActualUrl() {
        return this.actualUrl;
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

    @Override
    @XmlTransient
    public Long getId() {
        return this.id;
    }

    @XmlTransient
    public Date getInstallationDate() {
        return this.installationDate;
    }

    @XmlTransient
    public Boolean getInstalled() {
        return this.isInstalled();
    }

    @XmlTransient
    public int getInstallOrder() {
        return this.installOrder;
    }

    @XmlAttribute(name = "mc")
    public String getMcVersionDependency() {
        return this.mcVersionDependency;
    }

    @XmlTransient
    public ModPack getModPack() {
        return this.modPack;
    }

    @XmlAttribute(required = true)
    public String getName() {
        return this.name;
    }

    @XmlElement(name = "resourcecheck")
    public String getResourceCheck() {
        return this.resourceCheck;
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<Resource> getResources() {
        return this.resources;
    }

    @XmlAttribute
    public String getUrl() {
        return this.url;
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
        return new HashCodeBuilder().append(this.name).append(this.version).toHashCode();
    }

    @XmlTransient
    public Boolean isInstalled() {
        return this.getInstallationDate() != null;
    }

    public void setActualUrl(String actualUrl) {
        this.actualUrl = actualUrl;
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

    public void setInstalled(Boolean installed) {
        this.setInstallationDate(installed ? new Date() : null);
    }

    public void setInstallOrder(int installOrder) {
        this.installOrder = installOrder;
    }

    public void setMcVersionDependency(String mcVersionDependency) {
        this.mcVersionDependency = mcVersionDependency;
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
        return new ToStringBuilder(this).append("name", this.name).append("version", this.version).append("archive", this.archive) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                .append("description", this.description).append("installationDate", this.installationDate).append("installOrder", this.installOrder) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                .append("modPack", this.modPack).append("url", this.url).toString(); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
