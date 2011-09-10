package org.mmmr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Cascade;

/**
 * @author Jurgen
 */
@XmlRootElement(name = "mc")
@Entity
public class MC extends Dependency {
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "mc")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private List<MCFile> files;

    public MC() {
	super();
	setName("MC");
    }

    public MC(String version) {
	this();
	setVersion(version);
    }

    public void addFile(MCFile file) {
	if (getFiles() == null)
	    files = new ArrayList<MCFile>();
	getFiles().add(file);
	file.setMc(this);
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

    @Override
    public String toString() {
	return "MC [version=" + getVersion() + "]";
    }
}
