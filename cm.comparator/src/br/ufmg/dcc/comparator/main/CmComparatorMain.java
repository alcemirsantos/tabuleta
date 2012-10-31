/**
 * 
 */
package br.ufmg.dcc.comparator.main;

import br.ufmg.dcc.comparator.model.CmComparator;

/**
 * @author jean
 *
 */
public class CmComparatorMain {
	
	/**
	 * 
	 * @param oracleFileName
	 * @param testFileName
	 */
	public static void main(String oracleFileName, String testFileName) {
		
		/*
		 * recebe a localização dos arquivos cm, abre-os e realiza a leitura
		 */
		CmComparator cm = new CmComparator(oracleFileName, testFileName);
		
		/*
		 * extrai o model
		 */
		cm.extractModel();
		
		/*
		 * extrai os concerns
		 */
		cm.extractConcerns();
		
		/*
		 * realiza a comparação
		 * 
		 * DICA: dentro do método compare existem linhas comentadas que realizam a impressão do resultado na tela.
		 */
		cm.compare();
//		cm.print();
		
		
	}

}
