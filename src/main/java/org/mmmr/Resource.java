package org.mmmr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAttribute;
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
@XmlRootElement(name = "resource")
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@Entity
public class Resource implements Comparable<Resource>, PersistentObject {
    private String exclude;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "resource")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private List<MCFile> files;

    /** database id */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String include;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private Mod mod;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private ModPack modPack;

    private String sourcePath;

    private String targetPath;

    @Version
    private Integer ver;

    public Resource() {
        super();
    }

    public Resource(String sourcePath, String targetPath) {
        this();
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }

    public void addFile(MCFile file) {
        if (this.getFiles() == null) {
            this.files = new ArrayList<MCFile>();
        }
        this.getFiles().add(file);
        file.setResource(this);
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Resource other) {
        return new CompareToBuilder().append(this.sourcePath, other.sourcePath).append(this.targetPath, other.targetPath).toComparison();
    }

    /**
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Resource)) {
            return false;
        }
        Resource castOther = (Resource) other;
        return new EqualsBuilder().append(this.sourcePath, castOther.sourcePath).append(this.targetPath, castOther.targetPath).isEquals();
    }

    @XmlAttribute(required = false)
    public String getExclude() {
        return this.exclude;
    }

    @XmlTransient
    public List<MCFile> getFiles() {
        return this.files;
    }

    /**
     * 
     * @see org.mmmr.PersistentObject#getId()
     */
    @Override
    @XmlTransient
    public Long getId() {
        return this.id;
    }

    @XmlAttribute(required = false)
    public String getInclude() {
        return this.include;
    }

    @XmlTransient
    public Mod getMod() {
        return this.mod;
    }

    @XmlTransient
    public ModPack getModPack() {
        return this.modPack;
    }

    @XmlAttribute(name = "sourcepath", required = false)
    public String getSourcePath() {
        return this.sourcePath;
    }

    @XmlAttribute(name = "targetpath", required = true)
    public String getTargetPath() {
        return this.targetPath;
    }

    @XmlTransient
    public Integer getVer() {
        return this.ver;
    }

    /**
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.sourcePath).append(this.targetPath).toHashCode();
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    public void setFiles(List<MCFile> files) {
        this.files = files;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public void setMod(Mod mod) {
        this.mod = mod;
    }

    protected void setModPack(ModPack modPack) {
        this.modPack = modPack;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    protected void setVer(Integer ver) {
        this.ver = ver;
    }

    /**
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("exclude", this.exclude).append("include", this.include) //$NON-NLS-1$ //$NON-NLS-2$ 
                .append("sourcePath", this.sourcePath).append("targetPath", this.targetPath).toString(); //$NON-NLS-1$ //$NON-NLS-2$ 
    }
}
