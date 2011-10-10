package org.mmmr.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mmmr.MC;
import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.Resource;
import org.mmmr.services.InstallationService.UninstallMod;

/**
 * @author jurgen
 */
public class InstallTest extends DBTstSuperClass {
    private void _install1() {
        Mod m = new Mod("m1", "v1");
        m.setInstallOrder(1);
        Resource r = new Resource("./", "./");
        m.addResource(r);
        MCFile f1 = new MCFile("class1.class");
        r.addFile(f1);
        MCFile f2 = new MCFile("class2.class");
        r.addFile(f2);
        MCFile f3 = new MCFile("classm1.class");
        r.addFile(f3);
        DBTstSuperClass.session.persist(m);
        DBTstSuperClass.session.flush();
    }

    private void _install2() {
        Mod m = new Mod("m2", "v2");
        m.setInstallOrder(2);
        Resource r = new Resource("./", "./");
        m.addResource(r);
        MCFile f1 = new MCFile("class2.class");
        r.addFile(f1);
        MCFile f2 = new MCFile("class3.class");
        r.addFile(f2);
        MCFile f3 = new MCFile("classm2.class");
        r.addFile(f3);
        DBTstSuperClass.session.persist(m);
        DBTstSuperClass.session.flush();
    }

    @Test
    public void testCheck() {
        //
    }

    @Test
    public void testConflict1() {
        this._install1();
        for (MCFile existing : DBTstSuperClass.dbService.getAll(new MCFile("class2.class"))) {
            if (existing.getMc() != null) {
                continue;
            }
            if (existing.getResource() != null) {
                Assert.assertEquals("m1", existing.getResource().getMod().getName());
            } else {
                Assert.fail("there should be a conflict");
            }
        }
        for (MCFile existing : DBTstSuperClass.dbService.getAll(new MCFile("class3.class"))) {
            if (existing.getMc() != null) {
                continue;
            }
            if (existing.getResource() != null) {
                Assert.fail("there should be no conflict");
            }
        }
    }

    @Test
    public void testConflict2() {
        this._install1();
        this._install2();
        for (MCFile existing : DBTstSuperClass.dbService.getAll(new MCFile("class1.class"))) {
            if (existing.getMc() != null) {
                continue;
            }
            if (existing.getResource() != null) {
                Assert.assertEquals("m1", existing.getResource().getMod().getName());
            } else {
                Assert.fail("there should be a conflict");
            }
        }
        for (MCFile existing : DBTstSuperClass.dbService.getAll(new MCFile("class3.class"))) {
            if (existing.getMc() != null) {
                continue;
            }
            if (existing.getResource() != null) {
                Assert.assertEquals("m2", existing.getResource().getMod().getName());
            } else {
                Assert.fail("there should be a conflict");
            }
        }
    }

    @Test
    public void testInstallStruct1() {
        try {
            File archive = new File("src/test/resources/structure.zip");
            final Mod mod = new Mod();
            {
                Resource r = new Resource("classes", "./");
                r.setExclude("README.txt");
                mod.addResource(r);
            }
            {
                Resource r = new Resource("optional", "target/");
                r.setInclude(".class");
                mod.addResource(r);
            }
            ModInstallHelper helper = new ModInstallHelper(null, mod);
            ArchiveService.extract(archive, helper, helper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testInstallStruct2() {
        try {
            File archive = new File("src/test/resources/structure.zip");
            final Mod mod = new Mod();
            {
                Resource r = new Resource("./", "./");
                r.setExclude("README.txt");
                mod.addResource(r);
            }
            ModInstallHelper helper = new ModInstallHelper(null, mod);
            ArchiveService.extract(archive, helper, helper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testInstallStruct3() {
        try {
            File archive = new File("src/test/resources/structure.zip");
            final Mod mod = new Mod();
            {
                Resource r = new Resource("classes", "./");
                r.setExclude("README.txt");
                mod.addResource(r);
            }
            {
                Resource r = new Resource("optional", "target/");
                r.setInclude(".class");
                mod.addResource(r);
            }
            List<String> paths = new ArrayList<String>();
            paths.add("base.class");
            ModInstallHelper helper = new ModInstallHelper(null, mod, paths);
            ArchiveService.extract(archive, helper, helper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testNoConflict() {
        for (MCFile existing : DBTstSuperClass.dbService.getAll(new MCFile("class1.class"))) {
            if (existing.getMc() != null) {
                continue;
            }
            if (existing.getResource() != null) {
                Assert.fail("there should be no conflict");
            }
        }
        for (MCFile existing : DBTstSuperClass.dbService.getAll(new MCFile("class2.class"))) {
            if (existing.getMc() != null) {
                continue;
            }
            if (existing.getResource() != null) {
                Assert.fail("there should be no conflict");
            }
        }
    }

    @Test
    public void testRemove1() {
        this._install1();
        this._install2();
        Mod m = DBTstSuperClass.dbService.getAll(new Mod("m1", "v1")).get(0);
        UninstallMod info = InstallationService.uninstallMod(DBTstSuperClass.dbService, m);
        Assert.assertEquals(1, info.keep.size());
        Assert.assertEquals(1, info.delete.size());
        Assert.assertEquals(1, info.restore.size());
        Assert.assertEquals("classm1.class", info.delete.get(0));
        Assert.assertEquals("class2.class", info.keep.get(0));
        MC mc1 = DBTstSuperClass.dbService.hql("from MC", MC.class).get(0);
        MC mc2 = MC.class.cast(info.restore.entrySet().iterator().next().getValue());
        Assert.assertEquals(mc1, mc2);
    }

    @Test
    public void testRemove2() {
        this._install1();
        this._install2();
        Mod m = DBTstSuperClass.dbService.getAll(new Mod("m2", "v2")).get(0);
        UninstallMod info = InstallationService.uninstallMod(DBTstSuperClass.dbService, m);
        Assert.assertEquals(0, info.keep.size());
        Assert.assertEquals(1, info.delete.size());
        Assert.assertEquals(2, info.restore.size());
        Assert.assertEquals("classm2.class", info.delete.get(0));
        {
            Assert.assertNotNull(info.restore.get("class2.class"));
            Mod m1 = DBTstSuperClass.dbService.getAll(new Mod("m1", "v1")).get(0);
            Mod m2 = Mod.class.cast(info.restore.get("class2.class"));
            Assert.assertEquals(m1, m2);
        }
        {
            Assert.assertNotNull(info.restore.get("class3.class"));
            MC mc1 = DBTstSuperClass.dbService.hql("from MC", MC.class).get(0);
            MC mc2 = MC.class.cast(info.restore.get("class3.class"));
            Assert.assertEquals(mc1, mc2);
        }
    }
}
