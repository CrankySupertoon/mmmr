package org.mmmr;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Jurgen
 */
@XmlRootElement(name = "mod")
@Entity
public class ModDependency extends Dependency {

    private String url;

    public ModDependency() {
	super();
    }

    public ModDependency(Mod mod) {
	this(mod.getName(), mod.getVersion());
    }

    public ModDependency(String name, String version) {
	this();
	setName(name);
	setVersion(version);
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

    @XmlAttribute
    public String getUrl() {
	return url;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
	result = prime * result + ((this.getVersion() == null) ? 0 : this.getVersion().hashCode());
	return result;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this).appendSuper(super.toString()).append("url", url).toString();
    }
}
