/**
 * 
 */
package br.ufmg.dcc.comparator.main;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * @author jean
 *
 */
public class CmComparatorMainTest {

	private static final String RESOURCES_FOLDER = System.getProperty("user.dir")+File.separator+"resources"+File.separator;
	private static final String ORACLE_FILE = "simules-src2f.cm";
	private static final String T2F_FILE = "CMplayer.cm";
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testA() {
		
		/*
		 * Criado para facilitar a execução e os testes
		 */
		String oracle = RESOURCES_FOLDER + ORACLE_FILE;
		String CMplayer = RESOURCES_FOLDER + T2F_FILE;;
		
		/*
		 * Qualquer dúvida - jeanpsv@dcc.ufmg.br - jean.vasconcelos6@gmail.com
		 */
		
		/*
		 * existem alguns métodos 'print' que estão comentados, eles ajudam no debug do código
		 */
		
		/*
		 * Método que realiza a chamada de todas as funções essenciais para a comparação
		 * A entrada do método é a localização dos arquivos cm, o arquivo oráculo e o que será testado
		 */
		CmComparatorMain.main(oracle, CMplayer);
	}

	@Test
	public void testB() {
		
		/*
		 * Criado para facilitar a execução e os testes
		 */
		String oracle = RESOURCES_FOLDER + ORACLE_FILE;
		
		/*
		 * Qualquer dúvida - jeanpsv@dcc.ufmg.br - jean.vasconcelos6@gmail.com
		 */
		
		/*
		 * existem alguns métodos 'print' que estão comentados, eles ajudam no debug do código
		 */
		
		/*
		 * Método que realiza a chamada de todas as funções essenciais para a comparação
		 * A entrada do método é a localização dos arquivos cm, o arquivo oráculo e o que será testado
		 */
		CmComparatorMain.main(oracle, RESOURCES_FOLDER+"CMprojectcard.cm");
	}
	
	@Test
	public void testC() {
		
		/*
		 * Criado para facilitar a execução e os testes
		 */
		String oracle = RESOURCES_FOLDER + ORACLE_FILE;
		
		/*
		 * Qualquer dúvida - jeanpsv@dcc.ufmg.br - jean.vasconcelos6@gmail.com
		 */
		
		/*
		 * existem alguns métodos 'print' que estão comentados, eles ajudam no debug do código
		 */
		
		/*
		 * Método que realiza a chamada de todas as funções essenciais para a comparação
		 * A entrada do método é a localização dos arquivos cm, o arquivo oráculo e o que será testado
		 */
		CmComparatorMain.main(oracle, RESOURCES_FOLDER+"CMsoftwareengineer.cm");
	}
}
