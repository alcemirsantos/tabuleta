package alcemir.comparator;

import java.util.ArrayList;
import java.util.List;

public class MetricsCalculator {

	
	private List<CMElementTag> test;
	private List<CMElementTag> oracle;

	public MetricsCalculator(List<CMElementTag> oracleElements,
			List<CMElementTag> t2fElements) {
		this.oracle = oracleElements;
		this.test = t2fElements;
	}

	public int getTP() {
		ArrayList<CMElementTag> aux = new ArrayList<CMElementTag>();
		aux.addAll(this.oracle);
		aux.retainAll(test);
		return aux.size();
	}

	public int getFP() {
		return test.size()-getTP();
	}

	public int getFN() {
		return oracle.size()-getTP();
	}

	public double getRecall() {
		double tp = getTP();
		double fn = getFN();
		double r = tp/(tp+fn);
		return r;
	}
	
	public double getPrecision(){
		double tp = getTP();
		double fp = getFP();
		double p = tp/(tp+fp);
		return p;
	}
	
	public double getF1Score(){
		double f = (2*getPrecision()*getRecall())/(getPrecision()+getRecall());
		return f;
	}

}
