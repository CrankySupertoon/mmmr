package org.mmmr;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
import org.mmmr.services.UtilityMethods;

/**
 * @author Jurgen
 */
@XmlRootElement(name = "mod")
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@Entity
public class Dependency implements Comparable<Dependency>, PersistentObject {
    /** database id */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private Mod mod;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sortableName;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private Resource resource;

    private String url;

    @Version
    private Integer ver;

    @Column(nullable = false)
    private String version;

    public Dependency() {
        super();
    }

    public Dependency(String name, String version) {
        this();
        this.setName(name);
        this.setVersion(version);
    }

    public Dependency(String name, String version, Resource resource) {
        super();
        this.setName(name);
        this.setVersion(version);
        this.setResource(resource);
    }

    /**
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Dependency other) {
        return new CompareToBuilder().append(this.name, other.name).append(this.version, other.version).toComparison();
    }

    /**
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Dependency)) {
            return false;
        }
        Dependency castOther = (Dependency) other;
        return new EqualsBuilder().append(this.name, castOther.name).append(this.version, castOther.version).isEquals();
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

    @XmlTransient
    public Mod getMod() {
        return this.mod;
    }

    @XmlAttribute
    public String getName() {
        return this.name;
    }

    @XmlTransient
    public Resource getResource() {
        return this.resource;
    }

    @XmlTransient
    public String getSortableName() {
        if (this.sortableName == null) {
            this.setName(this.getName());
        }
        return this.sortableName;
    }

    @XmlAttribute
    public String getUrl() {
        return this.url;
    }

    @XmlTransient
    public Integer getVer() {
        return this.ver;
    }

    @XmlAttribute
    public String getVersion() {
        return this.version;
    }

    /**
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.name).append(this.version).toHashCode();
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public void setMod(Mod mod) {
        this.mod = mod;
    }

    public void setName(String name) {
        this.name = name;
        this.setSortableName(UtilityMethods.sortable(name));
    }

    protected void setResource(Resource resource) {
        this.resource = resource;
    }

    protected void setSortableName(String sortableName) {
        this.sortableName = sortableName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    protected void setVer(Integer ver) {
        this.ver = ver;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", this.name).append("version", this.version).toString(); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
