package org.mmmr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

@XmlRootElement(name = "compilation")
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "version" }) })
public class ModCompilation {
    @Version
    private Integer ver;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    @OneToMany(cascade = { CascadeType.ALL })
    private List<Mod> mods;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String version;

    @ManyToOne(cascade = { CascadeType.ALL })
    private MC mc;

    public ModCompilation(String name, String version) {
        this();
        this.name = name;
        this.version = version;
    }

    public ModCompilation() {
        super();

    }

    public void addMod(Mod mod) {
        if (getMods() == null)
            mods = new ArrayList<Mod>();
        getMods().add(mod);
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<Mod> getMods() {
        return mods;
    }

    public void setMods(List<Mod> mods) {
        this.mods = mods;
    }

    @XmlAttribute(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(required = true)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlElement
    public MC getMc() {
        return mc;
    }

    public void setMc(MC mc) {
        this.mc = mc;
    }

    @Override
    public String toString() {
        return "ModCompilation [mods=" + getMods() + ", name=" + getName() + ", description=" + getDescription() + ", version=" + getVersion()
                + ", mc=" + getMc() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
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
        ModCompilation other = (ModCompilation) obj;
        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;
        if (getVersion() == null) {
            if (other.getVersion() != null)
                return false;
        } else if (!getVersion().equals(other.getVersion()))
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
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
