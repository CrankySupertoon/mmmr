package org.mmmr.services;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mmmr.Resource;
import org.mmmr.services.swing.ModWizardWindow;

public class ModArchiveStructTest {
    @Test
    public void testFindMapping1() {
        try {
            File a = new File("src/test/resources/struct1.7z");
            List<Resource> findMapping = ModWizardWindow.findMapping(a);
            Assert.assertEquals(3, findMapping.size());
            Assert.assertEquals("cfg.properties", findMapping.get(0).getSourcePath());
            Assert.assertEquals("cfg.properties", findMapping.get(0).getTargetPath());
            Assert.assertEquals("jar/", findMapping.get(1).getSourcePath());
            Assert.assertEquals("bin/minecraft.jar", findMapping.get(1).getTargetPath());
            Assert.assertEquals("resources/", findMapping.get(2).getSourcePath());
            Assert.assertEquals("resources/", findMapping.get(2).getTargetPath());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(String.valueOf(ex));
        }
    }

    @Test
    public void testFindMapping2() {
        try {
            File a = new File("src/test/resources/struct2.7z");
            List<Resource> findMapping = ModWizardWindow.findMapping(a);
            Assert.assertEquals(1, findMapping.size());
            Assert.assertEquals("./", findMapping.get(0).getSourcePath());
            Assert.assertEquals("bin/minecraft.jar", findMapping.get(0).getTargetPath());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(String.valueOf(ex));
        }
    }

    @Test
    public void testFindMapping3() {
        try {
            File a = new File("src/test/resources/struct3.7z");
            List<Resource> findMapping = ModWizardWindow.findMapping(a);
            Assert.assertEquals(2, findMapping.size());
            Assert.assertEquals("v10/jar/", findMapping.get(0).getSourcePath());
            Assert.assertEquals("bin/minecraft.jar", findMapping.get(0).getTargetPath());
            Assert.assertEquals("v10/mods/", findMapping.get(1).getSourcePath());
            Assert.assertEquals("mods/", findMapping.get(1).getTargetPath());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(String.valueOf(ex));
        }
    }

    @Test
    public void testFindMapping4() {
        try {
            File a = new File("src/test/resources/struct4.7z");
            List<Resource> findMapping = ModWizardWindow.findMapping(a);
            Assert.assertEquals(1, findMapping.size());
            Assert.assertEquals("jar/", findMapping.get(0).getSourcePath());
            Assert.assertEquals("bin/minecraft.jar", findMapping.get(0).getTargetPath());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(String.valueOf(ex));
        }
    }
}
