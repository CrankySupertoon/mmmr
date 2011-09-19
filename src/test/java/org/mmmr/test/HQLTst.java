package org.mmmr.test;

import java.io.File;

import org.mmmr.MCFile;
import org.mmmr.services.Config;
import org.mmmr.services.DBService;

/**
 * test hql fast
 * 
 * @author Jurgen
 */
public class HQLTst {
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