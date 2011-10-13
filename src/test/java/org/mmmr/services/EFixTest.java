package org.mmmr.services;

import java.io.File;
import java.io.FileOutputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.mmmr.Mod;
import org.mmmr.Resource;
import org.mmmr.services.impl.ArchiveService7Zip;

public class EFixTest implements EFixTestCte {
    @Test
    public void test() {
        try {
            File arch = File.createTempFile("test", ".zip");
            File tmp = new File(arch.getParentFile(), "testing");
            tmp.mkdirs();
            UtilityMethods.copy(EFixTest.class.getClassLoader().getResourceAsStream(EFixTestCte.RESOURCE), new FileOutputStream(arch));
            Config cfg = new Config();
            ArchiveService7Zip as = new ArchiveService7Zip();
            XmlService xs = new XmlService(cfg);
            String path = as.list(arch).get(0).path.getPath();
            if (path.indexOf('/') != -1) {
                path = path.substring(0, path.indexOf('/'));
            }
            Mod m = xs.load(EFixTest.class.getClassLoader().getResourceAsStream(EFixTestCte.WRONG_XML), Mod.class);
            ModInstallHelper helper = new ModInstallHelper(tmp, m);
            as.extract(arch, helper, helper);
            Assert.assertEquals(0, UtilityMethods.listRecursive(tmp).size());
            for (Resource r : m.getResources()) {
                String fixed = path + '/' + r.getSourcePath().substring(r.getSourcePath().indexOf('/') + 1);
                r.setSourcePath(fixed);
            }
            helper = new ModInstallHelper(tmp, m);
            as.extract(arch, helper, helper);
            Assert.assertEquals(1, UtilityMethods.listRecursive(tmp).size());
        } catch (Exception ex) {
            Assert.fail(String.valueOf(ex));
        }
    }
}
