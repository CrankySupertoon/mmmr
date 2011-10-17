package org.mmmr.services;

import junit.framework.Assert;

import org.junit.Test;
import org.mmmr.services.interfaces.Path;

/**
 * @author Jurgen
 */
public class PathTest {
    @Test
    public void testAppend() {
        Assert.assertEquals("a/b", new Path("a/").append(new Path("b/")).getPath());
        Assert.assertEquals("a", new Path("a/").append(new Path("./")).getPath());
        Assert.assertEquals("b", new Path("./").append(new Path("b")).getPath());
    }

    @Test
    public void testConstruction() {
        Path p0 = new Path("");
        Assert.assertEquals("", p0.getPath());
        Path p1 = new Path("./");
        Assert.assertEquals("", p1.getPath());
        Path p2 = new Path("a//p");
        Assert.assertEquals("a/p", p2.getPath());
        Path p3 = new Path("a\\p");
        Assert.assertEquals("a/p", p3.getPath());
        Path p4 = new Path("/a\\p/");
        Assert.assertEquals("a/p", p4.getPath());
    }

    @Test
    public void testEndsWith() {
        Path p1 = new Path("a/b/c/d");
        Path p2 = new Path("a/b");
        Path p3 = new Path("c/d");
        Assert.assertEquals(false, p1.endsWith(p2));
        Assert.assertEquals(true, p1.endsWith(p3));
        Assert.assertEquals(false, p2.endsWith(p3));
        Assert.assertEquals(false, p2.endsWith(p1));
        Assert.assertEquals(false, p3.endsWith(p2));
        Assert.assertEquals(false, p3.endsWith(p1));
    }

    @Test
    public void testRelativePathTo() {
        Assert.assertEquals("c/d", new Path("a/b/c/d").relativePathTo(new Path("a/b")).getPath());

        Path p1 = new Path("a/b/c/d");
        Path p2 = new Path("a/b");
        Path p3 = new Path("c/d");

        try {
            p1.relativePathTo(p3);
            Assert.fail("");
        } catch (IllegalArgumentException ex) {
            //
        }
        try {
            p2.relativePathTo(p3);
            Assert.fail("");
        } catch (IllegalArgumentException ex) {
            //
        }
        try {
            p2.relativePathTo(p1);
            Assert.fail("");
        } catch (IllegalArgumentException ex) {
            //
        }
        try {
            p3.relativePathTo(p2);
            Assert.fail("");
        } catch (IllegalArgumentException ex) {
            //
        }
        try {
            p3.relativePathTo(p1);
            Assert.fail("");
        } catch (IllegalArgumentException ex) {
            //
        }
    }

    @Test
    public void testStartsWith() {
        Path p1 = new Path("a/b/c/d");
        Path p2 = new Path("a/b");
        Path p3 = new Path("c/d");
        Assert.assertEquals(true, p1.startsWith(p2));
        Assert.assertEquals(false, p1.startsWith(p3));
        Assert.assertEquals(false, p2.startsWith(p3));
        Assert.assertEquals(false, p2.startsWith(p1));
        Assert.assertEquals(false, p3.startsWith(p2));
        Assert.assertEquals(false, p3.startsWith(p1));
    }
}
