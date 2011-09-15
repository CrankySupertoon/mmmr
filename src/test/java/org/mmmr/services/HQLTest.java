package org.mmmr.services;

import java.io.File;

import org.mmmr.MCFile;

/**
 * test hql fast
 * 
 * @author Jurgen
 */
public class HQLTest {
    public static void main(String[] args) {
        try {
            Config cfg = new Config(args, new File("DUMMY").getAbsoluteFile().getParentFile());
            DBService db = DBService.getInstance(cfg);
            String hql = "from " + MCFile.class.getName();
            for (Object record : db.hql(hql, MCFile.class)) {
                System.out.println(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
