/**
 * 
 */
package br.ufmg.dcc.comparator.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author jean
 *
 */
public class OutPutFile {
	
	private OutputStream os;
	private OutputStreamWriter osw;
	private BufferedWriter bw;
	
	public OutPutFile(String fileName) {
		try {
			this.os = new FileOutputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.osw = new OutputStreamWriter(this.os);
		this.bw = new BufferedWriter(this.osw);
	}
	
	public void write(String message) {
		try {
			this.bw.write(message);
			this.bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			this.bw.close();
			this.osw.close();
			this.os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
