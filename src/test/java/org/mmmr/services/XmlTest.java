package org.mmmr.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.mmmr.Dependency;
import org.mmmr.Mod;
import org.mmmr.Mode;
import org.mmmr.Resource;

public class XmlTest {
    @Test
    public void test() {
        Mod mod = new Mod("name", "version", "url", "resourceCheck");
        mod.setMcVersionDependency("mc");
        mod.setMode(Mode.SERVER);
        {
            Resource resource = new Resource("sourcePath", "targetPath");
            resource.setInclude("include");
            resource.setExclude("include");
            mod.addResource(resource);
        }
        {
            Resource resource = new Resource("sourcePath", "targetPath");
            mod.addResource(resource);
        }
        {
            Dependency dependency = new Dependency("name", "version");
            dependency.setUrl("url");
            mod.addDepencency(dependency);
        }
        {
            Dependency dependency = new Dependency("name", "version");
            mod.addDepencency(dependency);
        }
        mod.setDescription("description");

        try {
            XmlService x = new XmlService(new Config());
            File d = new File("target/test");
            d.mkdirs();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            x.save(out, mod);
            out.flush();
            out.close();

            String s = new String(out.toByteArray());
            s = s.replaceAll("resource", "rsource");
            x.validate(new ByteArrayInputStream(s.getBytes()));
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            Assert.fail(String.valueOf(ex));
        }
    }
}
