package org.mmmr.services.swing;

import java.io.File;
import java.util.Date;

import org.mmmr.Mod;
import org.mmmr.services.Config;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.InstallationService;

public class ModOption {
    protected final Mod mod;

    protected Boolean updated;

    protected final Config cfg;

    public ModOption(Config cfg, Mod mod) {
        this.cfg = cfg;
        this.mod = mod;
    }

    public int compareTo(Mod other) {
        return this.mod.compareTo(other);
    }

    @Override
    public boolean equals(Object other) {
        return this.mod.equals(other);
    }

    public String getDescription() {
        return this.mod.getDescription();
    }

    public Date getInstallationDate() {
        return this.mod.getInstallationDate();
    }

    public Boolean getInstalled() {
        return this.mod.getInstalled();
    }

    public int getInstallOrder() {
        return this.mod.getInstallOrder();
    }

    public Mod getMod() {
        return this.mod;
    }

    public String getName() {
        return this.mod.getName();
    }

    public Boolean getUpdated() {
        // check only needed when not set (not persisten in db, not persisted in xml)
        if (this.updated == null) {
            // check only needed when installed => no change
            if (this.mod.isInstalled()) {
                // if actualUrl is set and differs url we already know that there is an update => true
                if ((this.mod.getActualUrl() != null) && !this.mod.getActualUrl().equals(this.getUrl())) {
                    this.updated = true;
                } else {
                    // this trick only works here so if it is another site there is no check => false
                    if (!this.getUrl().contains("minecraftforum")) { //$NON-NLS-1$
                        this.updated = false;
                    } else {
                        // trick: the site redirects to a link where the title is replaced (updated)
                        // so if the title has changed: the mod is probably updated
                        try {
                            String newUrl = InstallationService.getUrl(this.getUrl());
                            if ((newUrl != null) && !"null".equals(newUrl)) { //$NON-NLS-1$
                                // title not changed => false OR title changed => true
                                this.updated = !this.getUrl().equals(newUrl);
                            } else {
                                this.updated = false;
                            }
                        } catch (Exception ex) {
                            ExceptionAndLogHandler.log(ex);
                        }
                    }
                }
            }
        }
        return this.updated;
    }

    public String getUrl() {
        return this.mod.getUrl();
    }

    public String getVersion() {
        return this.mod.getVersion();
    }

    @Override
    public int hashCode() {
        return this.mod.hashCode();
    }

    public boolean isModArchive() {
        return new File(this.cfg.getMods(), this.getMod().getArchive()).exists();
    }

    public void setDescription(String description) {
        this.mod.setDescription(description);
    }

    public void setInstallationDate(Date installationDate) {
        this.mod.setInstallationDate(installationDate);
    }

    public void setInstalled(Boolean installed) {
        this.mod.setInstalled(installed);
    }

    public void setInstallOrder(int installOrder) {
        this.mod.setInstallOrder(installOrder);
    }

    public void setModArchive(@SuppressWarnings("unused") boolean modArchive) {
        //
    }

    public void setName(String name) {
        this.mod.setName(name);
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }

    public void setUrl(String url) {
        this.mod.setUrl(url);
    }

    public void setVersion(String version) {
        this.mod.setVersion(version);
    }

    @Override
    public String toString() {
        return this.mod.toString();
    }
}
