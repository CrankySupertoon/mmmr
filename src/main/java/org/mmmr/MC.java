package org.mmmr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Jurgen
 */
@XmlRootElement(name = "mc")
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "version" }) })
public class MC extends Dependency {
    @OneToMany(cascade = { CascadeType.ALL })
    private List<MCFile> files;

    private String version;

    public MC() {
	super();
    }

    public MC(String version) {
	this();
	this.version = version;
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
	MC other = (MC) obj;
	if (this.getVersion() == null) {
	    if (other.getVersion() != null)
		return false;
	} else if (!this.getVersion().equals(other.getVersion()))
	    return false;
	return true;
    }

    @XmlTransient
    public List<MCFile> getFiles() {
	return this.files;
    }

    @XmlAttribute
    public String getVersion() {
	return this.version;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
	return result;
    }

    public void setFiles(List<MCFile> files) {
	this.files = files;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    @Override
    public String toString() {
	return "MC [version=" + getVersion() + "]";
    }
}
