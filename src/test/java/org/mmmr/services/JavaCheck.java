package org.mmmr.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.mmmr.services.IOMethods.MemInfo;

public class JavaCheck {
    public static void main(String[] args) {
	try {
	    int min;
	    int max;
	    MemInfo info = IOMethods.getMemInfo();
	    if (info.memfreemb < 512) {
		min = 256;
		max = 1024;
	    } else if (info.memfreemb < 1024) {
		min = 512;
		max = 1536;
	    } else if (info.memfreemb < 2560) {
		min = 1024;
		max = 2048;
	    } else {
		min = 2048;
		max = 2048;
	    }

	    for (String[] jreinfo : IOMethods.getAllJavaInfo(IOMethods.getAllJavaRuntimes())) {
		String jre = jreinfo[0];
		String _64 = jreinfo[2];

		int _min = min;
		int _max = max;

		if ("false".equals(_64)) {
		    _min = Math.min(min, 1024);
		    _max = Math.min(max, 1024);
		}

		String[] options = ("-Xms" + _min + "m -Xmx" + _max + "m -client -XX:+UseConcMarkSweepGC -XX:+DisableExplicitGC -XX:+UseAdaptiveGCBoundary -XX:MaxGCPauseMillis=500 -XX:-UseGCOverheadLimit -XX:SurvivorRatio=12 -Xnoclassgc -XX:UseSSE=3 -Xincgc")
			.split(" ");

		System.out.println(jre);
		for (String option : options) {
		    System.out.println(option + " :: " + process(true, false, jre + "/bin/java.exe", option, "-version").get(0).toLowerCase().startsWith("java version"));
		}
		System.out.println();
	    }
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    private static List<String> process(boolean capture, boolean log, String... command) throws IOException {
	List<String> lines = new ArrayList<String>();
	ProcessBuilder pb = new ProcessBuilder(command);
	pb.redirectErrorStream(true);
	Process p = pb.start();

	if (capture) {
	    InputStream is = p.getInputStream();
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String line;

	    while ((line = br.readLine()) != null) {
		if (capture) {
		    lines.add(line);
		}

		if (log) {
		    System.out.println(line);
		}
	    }

	    is.close();
	}

	return lines;
    }
}
