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
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
@XmlRootElement(name = "modpack")
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "modpack_name_version", columnNames = { "name", "version" }) })
public class ModPack implements Comparable<ModPack>, PersistentObject, Installable {
    private String description;

    /** database id */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date installationDate;

    private int installOrder;

    private String mcVersionDependency;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "modPack")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private List<Mod> mods;

    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "modPack")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private List<Resource> resources;

    @Version
    private Integer ver;

    @Column(nullable = false)
    private String version;

    public ModPack() {
        super();
    }

    public ModPack(String name, String version) {
        this();
        this.name = name;
        this.version = version;
    }

    public void addMod(Mod mod) {
        if (this.getMods() == null) {
            this.mods = new ArrayList<Mod>();
        }
        this.getMods().add(mod);
        mod.setModPack(this);
    }

    public void addResource(Resource resource) {
        if (this.getResources() == null) {
            this.resources = new ArrayList<Resource>();
        }
        this.getResources().add(resource);
        resource.setModPack(this);
    }

    @Override
    public int compareTo(final ModPack other) {
        return new CompareToBuilder().append(this.name, other.name).append(this.version, other.version).toComparison();
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof ModPack)) {
            return false;
        }
        ModPack castOther = (ModPack) other;
        return new EqualsBuilder().append(this.name, castOther.name).append(this.version, castOther.version).isEquals();
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

    @Override
    @XmlTransient
    public int getInstallOrder() {
        return this.installOrder;
    }

    @XmlAttribute(name = "mc")
    public String getMcVersionDependency() {
        return this.mcVersionDependency;
    }

    @XmlElementWrapper
    @XmlElement
    public List<Mod> getMods() {
        return this.mods;
    }

    @XmlAttribute(required = true)
    public String getName() {
        return this.name;
    }

    @XmlElementWrapper
    @XmlElement
    public List<Resource> getResources() {
        return this.resources;
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

    public void setDescription(String description) {
        this.description = description;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public void setInstallationDate(Date installationDate) {
        this.installationDate = installationDate;
    }

    public void setInstallOrder(int installOrder) {
        this.installOrder = installOrder;
    }

    public void setMcVersionDependency(String mcVersionDependency) {
        this.mcVersionDependency = mcVersionDependency;
    }

    public void setMods(List<Mod> mods) {
        this.mods = mods;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    protected void setVer(Integer ver) {
        this.ver = ver;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", this.name).append("installationDate", this.installationDate).append("version", this.version) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                .append("description", this.description).toString(); //$NON-NLS-1$
    }
}
