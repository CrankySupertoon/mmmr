package org.mmmr;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "installedmod")
@Entity
public class InstalledMod {
    @Version
    private Integer ver;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public InstalledMod() {
        super();
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
}
