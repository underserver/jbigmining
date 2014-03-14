package test;

import org.sg.recognition.*;
import org.sg.recognition.classifiers.*;
import org.sg.recognition.evaluation.BasicsMetric;
import org.sg.recognition.evaluation.EvaluationManager;
import org.sg.recognition.evaluation.EvaluationMetric;
import org.sg.recognition.filters.BinaryFilter;
import org.sg.recognition.filters.Filter;
import org.sg.recognition.filters.NormalizeFilter;
import org.sg.recognition.validations.*;

import java.util.List;

/**
 * Project Name: PatternRecognition
 * Project Url: http://www.dotrow.com/projects/java/jcase
 * Author: Sergio Ceron
 * Version: 1.0
 * Date: 2/10/13 09:43 PM
 * Desc:
 */
public class mNNTest {

	private static final String FILE = "./banks/patrones.csv";

	private static final ValidationMethod validationMethod  = new LeaveOneOutValidation();
	private static final Algorithm algorithm = new Euclidean();
	private static final Filter[] filters = { /*new NormalizeFilter(), new BinaryFilter()*/ };

	private DataSet dataSet = null;

	public void init() {
		Parser parser = new CSVParser( FILE );
		dataSet = parser.parse();
		for( Filter filter : filters ) {
			filter.setDataSet( dataSet );
			filter.processAll();
			dataSet = filter.getNewDataSet();
		}
	}

	public void test() {
		EvaluationManager evaluationManager = new EvaluationManager();
		evaluationManager.setDataSet( dataSet );
		evaluationManager.setAlgorithm( algorithm );
		evaluationManager.setValidationMethod( validationMethod );
		evaluationManager.evaluate();

		for( EvaluationMetric metric : evaluationManager.getMetrics() ) {
			if( metric instanceof BasicsMetric ) {
				System.out.println("\nTPR");
				double[] tpr = ( (BasicsMetric) metric ).getTpr();
				for( double s : tpr ) {
					System.out.printf("%.3f\n", s);
				}
				System.out.println("\nFPR");
				double[] fpr = ( (BasicsMetric) metric ).getFpr();
				for( double s : fpr ) {
					System.out.printf("%.3f\n", s);
				}
			}
		}
	}


	public static void main( String[] args ) {
		mNNTest test = new mNNTest();
		test.init();
		test.test();
	}

}
