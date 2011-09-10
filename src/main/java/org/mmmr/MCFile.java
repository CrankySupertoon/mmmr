package org.mmmr;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;

/**
 * @author Jurgen
 */
@Entity
public class MCFile implements Comparable<MCFile>, PersistentObject {
    private long crc32;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private MC mc;

    private Date modificationDate;

    private String path;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private Resource resource;

    @Version
    private Integer ver;

    public MCFile() {
	super();
    }

    public MCFile(String path) {
	this();
	setPath(path);
    }

    public MCFile(String path, Date modificationDate, long crc32) {
	this();
	setPath(path);
	setModificationDate(modificationDate);
	setCrc32(crc32);
    }

    public int compareTo(final MCFile other) {
	return new CompareToBuilder().append(path, other.path).toComparison();
    }

    @Override
    public boolean equals(final Object other) {
	if (!(other instanceof MCFile))
	    return false;
	MCFile castOther = (MCFile) other;
	return new EqualsBuilder().append(path, castOther.path).isEquals();
    }

    public long getCrc32() {
	return this.crc32;
    }

    public Long getId() {
	return this.id;
    }

    public MC getMc() {
	return mc;
    }

    public Date getModificationDate() {
	return this.modificationDate;
    }

    public String getPath() {
	return this.path;
    }

    @XmlTransient
    public Resource getResource() {
	return resource;
    }

    public Integer getVer() {
	return this.ver;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(path).toHashCode();
    }

    public void setCrc32(long crc32) {
	this.crc32 = crc32;
    }

    protected void setId(Long id) {
	this.id = id;
    }

    protected void setMc(MC mc) {
	this.mc = mc;
    }

    public void setModificationDate(Date modificationDate) {
	this.modificationDate = modificationDate;
    }

    public void setPath(String path) {
	this.path = path.replace('\\', '/').replaceAll("//", "/");
    }

    protected void setResource(Resource resource) {
	this.resource = resource;
    }

    protected void setVer(Integer ver) {
	this.ver = ver;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this).append("path", path).append("crc32", crc32).append("mc", mc).append("modificationDate", modificationDate).append("resource", resource)
		.toString();
    }
}
