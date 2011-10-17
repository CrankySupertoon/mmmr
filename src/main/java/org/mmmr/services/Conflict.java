package org.mmmr.services;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.mmmr.Installable;
import org.mmmr.MC;
import org.mmmr.Mod;
import org.mmmr.ModPack;

/**
 * @author Jurgen
 */
public class Conflict implements Comparable<Conflict>, Installable {
    private final String path;

    private final MC mc;

    private final ModPack modPack;

    private final Mod mod;

    public Conflict(String path, MC mc, ModPack modPack, Mod mod) {
        super();
        this.path = path;
        this.mc = mc;
        this.modPack = modPack;
        this.mod = mod;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Conflict other) {
        return new CompareToBuilder().append(this.getInstallOrder(), other.getInstallOrder()).toComparison();
    }

    /**
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Conflict) {
            return Conflict.class.cast(obj).hashCode() == this.hashCode();
        }
        if (this.mc != null) {
            return this.mc.equals(obj);
        }
        if (this.modPack != null) {
            return this.modPack.equals(obj);
        }
        if (this.mod != null) {
            return this.mod.equals(obj);
        }

        return false;
    }

    public Object get() {
        if (this.mc != null) {
            return this.mc;
        }
        if (this.modPack != null) {
            return this.modPack;
        }
        if (this.mod != null) {
            return this.mod;
        }
        return null;
    }

    /**
     * 
     * @see org.mmmr.Installable#getInstallOrder()
     */
    @Override
    public int getInstallOrder() {
        return this.mod != null ? this.mod.getInstallOrder() : (this.modPack != null ? this.modPack.getInstallOrder() : 0);
    }

    public MC getMc() {
        return this.mc;
    }

    public Mod getMod() {
        return this.mod;
    }

    public ModPack getModPack() {
        return this.modPack;
    }

    public String getPath() {
        return this.path;
    }

    /**
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (this.mc != null) {
            return this.mc.hashCode();
        }
        if (this.modPack != null) {
            return this.modPack.hashCode();
        }
        if (this.mod != null) {
            return this.mod.hashCode();
        }
        return 0;
    }

    /**
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Conflict::");
        sb.append(this.getInstallOrder()).append("::");
        if (this.mc != null) {
            sb.append(this.mc);
        }
        if (this.modPack != null) {
            sb.append(this.modPack);
        }
        if (this.mod != null) {
            sb.append(this.mod);
        }
        return sb.toString();
    }
}
