package org.mmmr;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "file")
@Entity
public class MCFile {
    @Version
    private Integer ver;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String path;

    private long crc32;

    private Date modificationDate;

    public MCFile(String path, Date modificationDate, long crc32) {
        this();
        setPath(path);
        setModificationDate(modificationDate);
        setCrc32(crc32);
    }

    public MCFile() {
        super();
    }

    @XmlTransient
    public Integer getVer() {
        return this.ver;
    }

    public void setVer(Integer ver) {
        this.ver = ver;
    }

    @XmlTransient
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlAttribute
    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path.replace('\\', '/').replaceAll("//", "/");
    }

    @XmlAttribute
    public long getCrc32() {
        return this.crc32;
    }

    public void setCrc32(long crc32) {
        this.crc32 = crc32;
    }

    @XmlElement
    public Date getModificationDate() {
        return this.modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    @Override
    public String toString() {
        return "MCFile [path=" + this.getPath() + ", crc32=" + this.getCrc32() + ", modificationDate=" + this.getModificationDate() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getPath() == null) ? 0 : this.getPath().hashCode());
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
        MCFile other = (MCFile) obj;
        if (this.getPath() == null) {
            if (other.getPath() != null)
                return false;
        } else if (!this.getPath().equals(other.getPath()))
            return false;
        return true;
    }
}
