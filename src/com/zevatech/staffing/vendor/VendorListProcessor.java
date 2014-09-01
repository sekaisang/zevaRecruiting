package com.zevatech.staffing.vendor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class VendorListProcessor {

	private List<String> inputPaths;
	private String outputPath;
	
	private Set<String> emailSet;
	
	public VendorListProcessor(List<String> inputPaths, String outputPath) {
		this.emailSet = new HashSet<String>();
		this.inputPaths = inputPaths;
		this.outputPath = outputPath;
	}
	
	public void readVendors() throws IOException {
		for (String inputPath : inputPaths) {
			readVendorsFromFile(inputPath);
		}
	}
	
	private void readVendorsFromFile(String inputPath) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(inputPath)));
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			
			StringTokenizer st = new StringTokenizer(line, ",");
			while (st.hasMoreElements()) {
				String element = (String) st.nextElement();
				if (element.trim().length() == 0) {
					continue;
				}
				
				StringTokenizer st1 = new StringTokenizer(element, ";");
				while (st1.hasMoreElements()) {
					String email = ((String) st1.nextElement()).trim();
					if (email.trim().length() == 0) {
						continue;
					}
					
					if (email.startsWith("<")) {
						email = email.substring(1);
					}
					if (email.endsWith(">")) {
						email = email.substring(0, email.length() - 1);
					}
					
					email = email.trim();
					if (email.length() > 0 && email.indexOf("@") > 0) {
						emailSet.add(email);
					}
				}
			}
		}
		
		reader.close();
	}
	
	public void writeVendors() throws IOException {
		PrintStream ps = new PrintStream(new FileOutputStream(outputPath));
		int count = 0;
		for (String email : emailSet) {
			count ++;
			
			ps.print(email + ",");
			if (count == 99) {
				count = 0;
				ps.println();
				ps.println();
			}
		}
		ps.flush();
		ps.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		List<String> inputPaths = new ArrayList<String>();
		inputPaths.add("C:\\USB Backup\\ZevaTechnology\\vendor set.txt");
		String outputPath = "C:\\USB Backup\\ZevaTechnology\\vendor set2.txt";
		VendorListProcessor processor = new VendorListProcessor(inputPaths, outputPath);
		processor.readVendors();
		processor.writeVendors();
		System.out.println("Total vendors: " + processor.emailSet.size());
	}

}
