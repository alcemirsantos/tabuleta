/*************************************************************************
 * Copyright (c) 2012 Federal University of Minas Gerais - UFMG 
 * All rights avaiable. This program and the accompanying materials
 * are made avaiable under the terms of the Eclipse Public Lincense v1.0
 * which accompanies this distribution, and is avaiable at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Alcemir R. Santos - improvements on the ConcernMapper
 * 			architeture. ConcernMapper is available at
 * 			http://www.cs.mcgill.ca/~martin/cm/
 *************************************************************************/
package br.ufmg.dcc.t2fm.model.io;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import br.ufmg.dcc.t2fm.model.ConcernModel;

/**
 * @author Alcemir R. Santos
 * 
 */
public class JavaFileWriter {

	public static final String lineSeparator = System
			.getProperty("line.separator");

	// The class of the main JUnit suite, and the prefix of the subsuite names.
	public String className;

	// The package name of the main JUnit suite
	public String packageName;

	// The directory where the JUnit files should be written to.
	private String dirName;

	// The model to wrote in the JavaFile
	private ConcernModel concernModel;

	/**
	 * Constructor of the Java file writer.
	 * 
	 * @param dirName
	 * @param packageName
	 * @param className
	 * @param model
	 */
	public JavaFileWriter(String dirName, String packageName,
			String className, ConcernModel model) {
		this.dirName = dirName;
		this.packageName = packageName;
		this.className = className;
		this.concernModel = model;
	}

	/**
	 * Writes the Java file.
	 * 
	 * @param model
	 * 
	 * @return
	 */
	public File write(IFile pFile) {

//		/**
//		 * Creates a file concurrently with other threads.
//		 */
//		class CreateFile extends Thread {
//			private IFile aFile;
//			private PipedInputStream aInStream;
//
//			/**
//			 * Creates a new file to write to.
//			 * 
//			 * @param pFile
//			 *            The file handle.
//			 * @param pInStream
//			 *            An input stream.
//			 */
//			public CreateFile(IFile pFile, PipedInputStream pInStream) {
//				aFile = pFile;
//				aInStream = pInStream;
//				start();
//			}
//
//			/**
//			 * @see java.lang.Runnable#run()
//			 */
//			public void run() {
//				try {
//					if (aFile.exists()) {
//						aFile.setContents(aInStream, true, false, null);
//					} else {
//						aFile.create(aInStream, true, null);
//					}
//				} catch (CoreException lException) {
//					throw new RuntimeException(
//							"Exception while creating a new file", lException);
//				} finally {
//					try {
//						aInStream.close();
//					} catch (IOException lException) {
//						throw new RuntimeException(
//								"Exception while creating a new file",
//								lException);
//					}
//				}
//			}
//		}
//
//		try {
//			PipedOutputStream lOutStream = new PipedOutputStream();
//			PipedInputStream lInStream = new PipedInputStream(lOutStream);
//			Thread lThread = new CreateFile(pFile, lInStream);
//
//			Source lSource = new DOMSource(createDocument());
//			Result lResult = new StreamResult(lOutStream);
//			Transformer lTransformer = TransformerFactory.newInstance()
//					.newTransformer();
//			lTransformer.transform(lSource, lResult);
//			
//			lOutStream.flush();
//			lOutStream.close();
//			try {
//				lThread.join();
//			} catch (InterruptedException lException) {
//				// Proceed
//			}
//		} catch (TransformerConfigurationException lException) {
//			throw new ModelIOException(lException.getMessage());
//		} catch (TransformerException lException) {
//			throw new ModelIOException(lException.getMessage());
//		} catch (IOException lException) {
//			throw new ModelIOException(lException.getMessage());
//		}
//
//		/**
//		 * fim do modelwrither.java
//		 */
		return getDocument();
	}

	private File getDocument() {
		File file = new File(getDir(), className + ".java");
		PrintStream out = createTextOutputStream(file);

		try {
			outputPackageName(out, packageName);
			out.println();
			out.println("import junit.framework.*;");
			out.println();
			out.println("public class " + className + " extends TestCase {");
			out.println();
			out.println("  public void test() throws Throwable {");
			out.println();
			out.println(indent("if (true) { System.out.println(); System.out.print(\""
					+ className + ".test\"); }"));
			out.println();
			out.println("  }");
			out.println();
			out.println("}");
		} finally {
			if (out != null)
				out.close();
		}

		return file;
	}

	/**
	 * Append identation to the code string.
	 * 
	 * @param codeString
	 * @return
	 */
	public static String indent(String codeString) {
		StringBuilder indented = new StringBuilder();
		String[] lines = codeString.split(lineSeparator);
		for (String line : lines) {
			indented.append("    " + line + lineSeparator);
		}
		return indented.toString();
	}

	/**
	 * It tests if is default package to return the right package.
	 * 
	 * @param out
	 * @param packageName
	 */
	private static void outputPackageName(PrintStream out, String packageName) {
		boolean isDefaultPackage = packageName.length() == 0;
		if (!isDefaultPackage)
			out.println("package " + packageName + ";");
	}

	/**
	 * Build the path to the file.
	 * 
	 * @return File, with its path.
	 */
	public File getDir() {
		File dir = null;

		if (dirName == null || dirName.length() == 0)
			dir = new File(System.getProperty("user.dir"));
		else
			dir = new File(dirName);

		if (packageName == null)
			return dir;

		packageName = packageName.trim(); // Just in case.

		if (packageName.length() == 0)
			return dir;

		String[] split = packageName.split("\\.");
		for (String s : split) {
			dir = new File(dir, s);
		}
		return dir;
	}

	/**
	 * Create a output stream to write to the file.
	 * 
	 * @param file
	 * @return
	 */
	private static PrintStream createTextOutputStream(File file) {
		try {
			return new PrintStream(file);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			throw new Error("This can't happen");
		}
	}
}
