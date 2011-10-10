package org.mmmr.services.swing;

import java.io.File;
import java.util.Date;

import org.mmmr.Mod;
import org.mmmr.Mode;
import org.mmmr.services.Config;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.InstallationService;
import org.mmmr.services.swing.ModOptionsWindow.Versions;

public class ModOption {
    protected Boolean updated;

    protected Boolean installed = false;

    protected Date wasInstalledDate = null;

    protected Integer installOrder;

    protected final Config cfg;

    protected Versions versions = new Versions();

    public ModOption(Config cfg, Mod mod) {
        this.cfg = cfg;
        this.addMod(mod);
    }

    public void addMod(Mod mod) {
        this.versions.addVersion(mod);

        if (mod.getInstalled()) {
            this.installed = true;
            this.wasInstalledDate = mod.getInstallationDate();
            this.installOrder = mod.getInstallOrder();
        }
    }

    public Boolean checkIfUpdated() {// this trick only works here so if it is another site there is no check => false
        if ((this.getUrl() == null) || !this.getUrl().contains("minecraftforum")) { //$NON-NLS-1$
            return false;
        }
        // check only needed when not set (not persisten in db, not persisted in xml)
        if (this.updated == null) {
            // if actualUrl is set and differs url we already know that there is an update => true
            if ((this.getMod().getActualUrl() != null) && !this.getMod().getActualUrl().equals(this.getUrl())) {
                return true;
            }
            // trick: the site redirects to a link where the title is replaced (updated)
            // so if the title has changed: the mod is probably updated
            try {
                String newUrl = InstallationService.getUrl(this.getUrl());
                if ((newUrl != null) && !"null".equals(newUrl)) { //$NON-NLS-1$
                    // title not changed => false OR title changed => true
                    return !this.getUrl().equals(newUrl);
                }
                return false;
            } catch (Exception ex) {
                ExceptionAndLogHandler.log(ex);
            }
        }
        return null;
    }

    public String getDescription() {
        return this.getMod().getDescription();
    }

    public Date getInstallationDate() {
        return this.getVersions().getInstalledOrLatestMod().getInstallationDate();
    }

    public Boolean getInstalled() {
        return this.installed;
    }

    public Integer getInstallOrder() {
        return this.installOrder;
    }

    public Mod getMod() {
        return this.versions.getMod();
    }

    public Mode getMode() {
        return this.getMod().getMode();
    }

    public String getName() {
        return this.getMod().getName();
    }

    public Boolean getUpdated() {
        return this.updated;
    }

    public String getUrl() {
        return this.getMod().getUrl();
    }

    public Versions getVersions() {
        return this.versions;
    }

    public boolean isModArchive() {
        return new File(this.cfg.getMods(), this.getMod().getArchive()).exists();
    }

    public void setDescription(String description) {
        throw new UnsupportedOperationException(String.valueOf(description));
    }

    public void setInstallationDate(Date installationDate) {
        this.wasInstalledDate = installationDate;
    }

    public void setInstalled(Boolean installed) {
        this.installed = installed;
        this.setInstallationDate(installed ? (this.wasInstalledDate == null ? new Date() : this.wasInstalledDate) : null);
    }

    public void setInstallOrder(Integer installOrder) {
        this.installOrder = installOrder;
    }

    public void setModArchive(boolean modArchive) {
        throw new UnsupportedOperationException(String.valueOf(modArchive));
    }

    public void setMode(Mode mode) {
        throw new UnsupportedOperationException(String.valueOf(mode));
    }

    public void setName(String name) {
        throw new UnsupportedOperationException(String.valueOf(name));
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }

    public void setUrl(String url) {
        throw new UnsupportedOperationException(String.valueOf(url));
    }

    public void setVersions(Versions versions) {
        this.versions = versions;
    }
}
