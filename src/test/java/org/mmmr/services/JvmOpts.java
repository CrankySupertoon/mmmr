package org.mmmr.services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JvmOpts {
    private static class LogProcessOutput implements ParseProcessOutput {
        @Override
        public void process(String line) {
            System.out.println(line);
        }
    }

    private static interface ParseProcessOutput {
        public void process(String line);
    }

    public static void main(String[] args) {
        try {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("jvm-opts.txt"))));
            StringBuilder sb = new StringBuilder();
            List<String> cmd = new ArrayList<String>();
            cmd.add("java");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if ((line.length() > 2) && (line.charAt(0) == '-')) {
                    sb.append(line).append(" ");
                    cmd.add(line);
                }
            }
            System.out.println(sb);
            cmd.add("-version");
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            System.out.println(pb.command());
            Process process = pb.start();
            JvmOpts.parseProcessOutput(process, new LogProcessOutput());
            System.out.println(process.exitValue());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void parseProcessOutput(Process process, ParseProcessOutput processor) throws Exception {
        InputStreamReader tempReader = new InputStreamReader(new BufferedInputStream(process.getInputStream()));
        BufferedReader reader = new BufferedReader(tempReader);

        while (true) {
            String line = reader.readLine();

            if (line == null) {
                break;
            }

            processor.process(line);
        }
    }
}
