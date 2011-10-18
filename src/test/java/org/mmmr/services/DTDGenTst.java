package org.mmmr.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.mmmr.Dependency;
import org.mmmr.Mod;
import org.mmmr.Mode;
import org.mmmr.Resource;

/**
 * @author Jurgen
 */
public class DTDGenTst {
    public static void main(String[] args) {
        try {
            File generatedSources = new File("src/main/resources");//$NON-NLS-1$ 

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

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XmlService service = new XmlService(new Config());
            service.save(out, mod);

            DTDGenerator app = new DTDGenerator();

            app.read(new ByteArrayInputStream(out.toByteArray()));

            app.write(System.out);

            FileOutputStream fout = new FileOutputStream(new File(generatedSources, service.getContextPath() + ".mod.dtd"));//$NON-NLS-1$ 
            PrintStream pw = new PrintStream(fout);
            app.write(pw);
            pw.flush();
            fout.flush();
            fout.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
