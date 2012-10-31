/**
 * 
 */
package br.ufmg.dcc.comparator.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author jean
 *
 */
public class CmComparator {
	
	private String oracleContent;
	private String testContent;
	private List<Concern> oracleConcerns;
	private List<Concern> testConcerns;
	private OutPutFile outPutFile;
	
	private static final String RESOURCES_FOLDER = System.getProperty("user.dir")+File.separator+"resources"+File.separator;
	private static final String CMP_FOLDER = "cmp"+File.separator;
	
	public CmComparator(String oracleFileName, String testFileName) {
		
		String[] s = testFileName.split(File.separator);
		String cmFilename = "cmp-"+s[s.length-1]+".txt";
		/*
		 * arquivo de saída
		 */
		this.outPutFile = new OutPutFile(RESOURCES_FOLDER+CMP_FOLDER+cmFilename);
		
		try {
			//open oracle file
			InputStream isOracle = new FileInputStream(new File(oracleFileName));
			InputStreamReader isrOracle = new InputStreamReader(isOracle);
			BufferedReader brOracle = new BufferedReader(isrOracle);
			//open test file
			InputStream isTest = new FileInputStream(new File(testFileName));
			InputStreamReader isrTest = new InputStreamReader(isTest);
			BufferedReader brTest = new BufferedReader(isrTest);
			
			try {
				//read oracle file
				this.oracleContent = brOracle.readLine();
				//read test file
				this.testContent = brTest.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				//close oracle file
				brOracle.close();
				isrOracle.close();
				isOracle.close();
				//close test file
				brTest.close();
				isrTest.close();
				isTest.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void print() {
		System.out.println();
		for(Concern c : this.oracleConcerns) {
			c.print();
		}
		System.out.println();
		for(Concern c : this.testConcerns) {
			c.print();
		}
	}
	
	public void extractModel() {
		/*
		 * deixa o conteúdo na forma <concern name ... </concern><concern ... </concern> ...
		 * ou seja, deixa o conteúdo na forma de uma lista de concerns
		 */
		this.oracleContent = this.oracleContent.replaceAll(".*<model>", "");
		this.oracleContent = this.oracleContent.replaceAll("</model>.*", "");
		
		this.testContent = this.testContent.replaceAll(".*<model>", "");
		this.testContent = this.testContent.replaceAll("</model>.*", "");
	}
	
	public void extractConcerns() {
		/*
		 * separa as concerns
		 */
		
		//oracle
		this.oracleConcerns = new ArrayList<Concern>();
		this.oracleContent = this.oracleContent.replaceAll("<concern ", "");
		String[] oracleConcerns = this.oracleContent.split("</concern>");
		for(String i : oracleConcerns) {
			Concern concern = new Concern(i);
			this.oracleConcerns.add(concern);
		}
		
		//test
		this.testConcerns = new ArrayList<Concern>();
		this.testContent = this.testContent.replaceAll("<concern ", "");
		String[] testConcerns = this.testContent.split("</concern>");
		for(String i : testConcerns) {
			Concern concern = new Concern(i);
			this.testConcerns.add(concern);
		}
	}
	
	public void compare() {
		
		int equal;
		
		Iterator<Concern> oracleConcernIterator = this.oracleConcerns.iterator();
		while(oracleConcernIterator.hasNext()) {
			Concern oracleConcernTemp = oracleConcernIterator.next();
			Iterator<Concern> testConcernIterator = this.testConcerns.iterator();
			while(testConcernIterator.hasNext()) {
				Concern testConcernTemp = testConcernIterator.next();
				
				if((oracleConcernTemp.compareTo(testConcernTemp)) == 0) {
					
					equal = 0;
					
					Iterator<Element> oracleElementIterator = oracleConcernTemp.getElements().iterator();
					while(oracleElementIterator.hasNext()) {
						Element oracleElementTemp = oracleElementIterator.next();
						Iterator<Element> testElementIterator = testConcernTemp.getElements().iterator();
						while(testElementIterator.hasNext()) {
							Element testElementTemp = testElementIterator.next();
							//iguais
							if((oracleElementTemp.compareTo(testElementTemp)) == 0) {
								
								equal++;
							}
						}
					}
					
					this.outPutFile.write("Esta no oracle e no test:			" + equal);
					this.outPutFile.write("Esta no oracle e nao esta no test:	" + (oracleConcernTemp.getElements().size() - equal));
					this.outPutFile.write("Esta no test e nao esta no oracle:	" + (testConcernTemp.getElements().size() - equal));
					
					this.outPutFile.close();
					
					System.out.println("Esta no oracle e no test:		" + equal);
					System.out.println("Esta no oracle e nao esta no test:	" + (oracleConcernTemp.getElements().size() - equal));
					System.out.println("Esta no test e nao esta no oracle:	" + (testConcernTemp.getElements().size() - equal));
					
				}
			}
		}
	}
}
