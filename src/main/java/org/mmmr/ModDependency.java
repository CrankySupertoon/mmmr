package org.mmmr;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mod")
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "version" }) })
public class ModDependency extends Dependency {
    private String name;

    private String version;

    public ModDependency() {
        super();
    }

    public ModDependency(String name, String version) {
        this();
        this.name = name;
        this.version = version;
    }

    public ModDependency(Mod mod) {
        this(mod.getName(), mod.getVersion());
    }

    @XmlAttribute
    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlAttribute
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ModDependency [name=" + this.getName() + ", version=" + this.getVersion() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        ModDependency other = (ModDependency) obj;
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
}
