package org.mmmr.services;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mmmr.services.impl.ArchiveService7Zip;
import org.mmmr.services.impl.ExceptionAndLogHandlerLog4j;
import org.mmmr.services.interfaces.ArchiveEntry;

/**
 * @author Jurgen
 */
public class ArchiveServiceTest {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ExceptionAndLogHandlerLog4j.noFileLogging();
    }

    @Test
    public void test1() {
        try {
            ArchiveService7Zip helper = new ArchiveService7Zip();
            String[] formats = { "zip", "rar", "7z" };
            String[] suffix = { "1", "2" };
            for (String format : formats) {
                for (String element : suffix) {
                    try {
                        String f = "file" + element + "." + format;
                        System.out.println(f);
                        helper.extract(new File("src/test/resources/" + f), new DefaultArchiveOutputStreamBuilder(new File("target/test-classes/"
                                + format + "/" + element + "/")), new DefaultArchiveEntryMatcher(Collections.singletonList("file.txt")), "test");
                    } catch (Exception ex) {
                        ex.printStackTrace(System.out);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(String.valueOf(ex));
        }
    }

    @Test
    public void test2() {
        try {
            File a = new File("src/test/resources/1-5.7z");
            ArchiveService7Zip s = new ArchiveService7Zip();
            Collection<ArchiveEntry> as = s.extract(a, new MockArchiveOutputStreamBuilder(),
                    new DefaultArchiveEntryMatcher(Collections.singletonList("1.txt")));
            Assert.assertEquals(1, as.size());
            Assert.assertEquals("1.txt", as.iterator().next().path.getPath());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(String.valueOf(ex));
        }

    }
}
