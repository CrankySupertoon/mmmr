package org.mmmr.test;

import java.io.File;
import java.util.Scanner;

import org.mmmr.services.IOMethods;

/**
 * @author Jurgen
 */
public class CompareDirs {
    public static void main(String[] args) {
        try {
            Scanner kb = new Scanner(System.in);
            System.out.println("oldest directory: ");
            File od = new File(kb.next());
            System.out.println("newest directory: ");
            File nd = new File(kb.next());
            int ndp = nd.getAbsolutePath().length() + 1;
            System.out.println("save changes to directory: ");
            File ch = IOMethods.newDir(kb.next());

            for (File nf : IOMethods.listRecursive(nd)) {
                if (nf.isDirectory()) {
                    continue;
                }
                String relative = nf.getAbsolutePath().substring(ndp);
                File of = new File(od, relative);
                if (!of.exists() || !IOMethods.fileEquals(nf, of)) {
                    IOMethods.copyFile(nf, new File(ch, relative));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
