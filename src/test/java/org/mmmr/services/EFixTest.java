package org.mmmr.services;

import java.io.File;
import java.io.FileOutputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.mmmr.Mod;
import org.mmmr.Resource;
import org.mmmr.services.impl.ArchiveService7Zip;

public class EFixTest {
    public static String RESOURCE = "extract-problem/Millenaire1.6.8.zip";

    public static String WRONG_XML = "extract-problem/Millenaire1.6.8.zip.xml.unfixed.xml";

    @Test
    public void test() {
        try {
            File arch = File.createTempFile("test", ".zip");
            File tmp = new File(arch.getParentFile(), "dir-" + File.createTempFile("testing", null).getName());
            tmp.mkdirs();
            UtilityMethods.copy(EFixTest.class.getClassLoader().getResourceAsStream(EFixTest.RESOURCE), new FileOutputStream(arch));
            Config cfg = new Config();
            ArchiveService7Zip as = new ArchiveService7Zip();
            XmlService xs = new XmlService(cfg);
            String path = as.list(arch).get(0).path.getPath();
            if (path.indexOf('/') != -1) {
                path = path.substring(0, path.indexOf('/'));
            }
            Mod m = xs.load(EFixTest.class.getClassLoader().getResourceAsStream(EFixTest.WRONG_XML), Mod.class);
            ModInstallHelper helper = new ModInstallHelper(tmp, m);
            as.extract(arch, helper, helper);
            Assert.assertEquals(UtilityMethods.listRecursive(tmp).toString(), 0, UtilityMethods.listRecursive(tmp).size());
            for (Resource r : m.getResources()) {
                String fixed = path + '/' + r.getSourcePath().substring(r.getSourcePath().indexOf('/') + 1);
                r.setSourcePath(fixed);
            }
            helper = new ModInstallHelper(tmp, m);
            as.extract(arch, helper, helper);
            Assert.assertEquals(UtilityMethods.listRecursive(tmp).toString(), 1, UtilityMethods.listRecursive(tmp).size());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(String.valueOf(ex));
        }
    }
}
