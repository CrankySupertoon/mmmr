package org.mmmr;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class MCFile {
    private long crc32;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date modificationDate;

    private String path;

    @Version
    private Integer ver;

    public MCFile() {
	super();
    }

    public MCFile(String path, Date modificationDate, long crc32) {
	this();
	setPath(path);
	setModificationDate(modificationDate);
	setCrc32(crc32);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	MCFile other = (MCFile) obj;
	if (this.getPath() == null) {
	    if (other.getPath() != null)
		return false;
	} else if (!this.getPath().equals(other.getPath()))
	    return false;
	return true;
    }

    public long getCrc32() {
	return this.crc32;
    }

    public Long getId() {
	return this.id;
    }

    public Date getModificationDate() {
	return this.modificationDate;
    }

    public String getPath() {
	return this.path;
    }

    public Integer getVer() {
	return this.ver;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((this.getPath() == null) ? 0 : this.getPath().hashCode());
	return result;
    }

    public void setCrc32(long crc32) {
	this.crc32 = crc32;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public void setModificationDate(Date modificationDate) {
	this.modificationDate = modificationDate;
    }

    public void setPath(String path) {
	this.path = path.replace('\\', '/').replaceAll("//", "/");
    }

    public void setVer(Integer ver) {
	this.ver = ver;
    }

    @Override
    public String toString() {
	return "MCFile [path=" + this.getPath() + ", crc32=" + this.getCrc32() + ", modificationDate=" + this.getModificationDate() + "]";
    }
}
