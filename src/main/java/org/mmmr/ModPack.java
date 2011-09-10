package org.mmmr;

import java.util.ArrayList;
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
@XmlRootElement(name = "modpack")
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "modpack_name_version", columnNames = { "name", "version" }) })
public class ModPack implements Comparable<ModPack>, PersistentObject {
    private String description;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private MC mc;

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
	if (getMods() == null)
	    mods = new ArrayList<Mod>();
	getMods().add(mod);
	mod.setModPack(this);
    }

    public void addResource(Resource resource) {
	if (getResources() == null)
	    resources = new ArrayList<Resource>();
	getResources().add(resource);
	resource.setModPack(this);
    }

    public int compareTo(final ModPack other) {
	return new CompareToBuilder().append(name, other.name).append(version, other.version).toComparison();
    }

    @Override
    public boolean equals(final Object other) {
	if (!(other instanceof ModPack))
	    return false;
	ModPack castOther = (ModPack) other;
	return new EqualsBuilder().append(name, castOther.name).append(version, castOther.version).isEquals();
    }

    @XmlAttribute
    public String getDescription() {
	return this.description;
    }

    @XmlTransient
    public Long getId() {
	return this.id;
    }

    @XmlElement
    public MC getMc() {
	return mc;
    }

    @XmlAttribute(name = "mc")
    public String getMcVersionDependency() {
	return mcVersionDependency;
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<Mod> getMods() {
	return mods;
    }

    @XmlAttribute(required = true)
    public String getName() {
	return name;
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<Resource> getResources() {
	return this.resources;
    }

    @XmlTransient
    public Integer getVer() {
	return this.ver;
    }

    @XmlAttribute(required = true)
    public String getVersion() {
	return version;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(name).append(version).toHashCode();
    }

    public void setDescription(String description) {
	this.description = description;
    }

    protected void setId(Long id) {
	this.id = id;
    }

    public void setMc(MC mc) {
	this.mc = mc;
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
	return new ToStringBuilder(this).append("name", name).append("version", version).append("description", description).append("mc", mc).toString();
    }
}
