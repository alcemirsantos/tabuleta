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
package br.ufmg.dcc.tabuleta.views.components;

/**
 * Esta classe representa um relatório dos valores obtidos em uma compraração 
 *  de um par de arquivos <code>.cm</code>.
 * 
 * @author Alcemir R. Santos
 *
 */
public class MetricsReport {

	private String fileName;
	private String truePositives;
	private String falsePositives;
	private String falseNegatives;
	private String f1Score;
	private String recall;
	private String precision;
	
	/**
	 * @param filename
	 * @param truePositives
	 * @param falsePositives
	 * @param falseNegatives
	 * @param f1Score
	 * @param recall
	 * @param precision
	 */
	public MetricsReport(String filename, String truePositives, String falsePositives,
			String falseNegatives, String recall, String precision, String f1Score) {
		super();
		this.fileName = filename;
		this.truePositives = truePositives;
		this.falsePositives = falsePositives;
		this.falseNegatives = falseNegatives;
		this.f1Score = f1Score;
		this.recall = recall;
		this.precision = precision;
	}

	/**
	 * @param truePositives the truePositives to set
	 */
	public void setTruePositives(String truePositives) {
		this.truePositives = truePositives;
	}

	/**
	 * @param falsePositives the falsePositives to set
	 */
	public void setFalsePositives(String falsePositives) {
		this.falsePositives = falsePositives;
	}

	/**
	 * @param falseNegatives the falseNegatives to set
	 */
	public void setFalseNegatives(String falseNegatives) {
		this.falseNegatives = falseNegatives;
	}

	/**
	 * @param f1Score the f1Score to set
	 */
	public void setF1Score(String f1Score) {
		this.f1Score = f1Score;
	}

	/**
	 * @param recall the recall to set
	 */
	public void setRecall(String recall) {
		this.recall = recall;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(String precision) {
		this.precision = precision;
	}

	/**
	 * @param file the file to set
	 */
	public void setFileName(String file) {
		this.fileName = file;
	}

	/**
	 * @return
	 */
	public String getTruePositives() {
		return this.truePositives;
	}

	/**
	 * @return
	 */
	public String getFalsePositives() {
		return this.falsePositives;
	}

	/**
	 * @return
	 */
	public String getFalseNegatives() {
		return this.falseNegatives;
	}

	/**
	 * @return
	 */
	public String getRecall() {
		return this.recall;
	}
	
	/**
	 * @return
	 */
	public String getPrecision() {
		return this.precision;
	}
	
	/**
	 * @return
	 */
	public String getF1Score() {
		return this.f1Score;
	}

	/**
	 * @return the file
	 */
	public String getFileName() {
		return fileName;
	}

}
