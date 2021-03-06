package ca.ubc.ece.salt.pangor.classify;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierMetrics;

public class ClassifyDataSetMain {

	/**
	 * Creates the learning data set for extracting repair patterns.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		ClassifyDataSetOptions options = new ClassifyDataSetOptions();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			ClassifyDataSetMain.printUsage(e.getMessage(), parser);
			return;
		}

		/* Print the help page. */
		if(options.getHelp()) {
			ClassifyDataSetMain.printHelp(parser);
			return;
		}

		/* Re-construct and filter the data set. */
		ClassifierDataSet dataSet = new ClassifierDataSet(options.getDataSetPath());

		/* Print the metrics from the data set. */
		if(options.getPrintMetrics()) {
			ClassifierMetrics metrics = dataSet.getMetrics();
			System.out.println(metrics.printMetricsAsLatexTable());
		}

		/* Pre-process the file. */
		dataSet.preProcess();

		/* Print the data set. */
		dataSet.writeFilteredDataSet(options.getFilteredPath());

	}

	/**
	 * Prints the help file for main.
	 * @param parser The args4j parser.
	 */
	private static void printHelp(CmdLineParser parser) {
        System.out.print("Usage: DataSetMain ");
        parser.setUsageWidth(Integer.MAX_VALUE);
        parser.printSingleLineUsage(System.out);
        System.out.println("\n");
        parser.printUsage(System.out);
        System.out.println("");
        return;
	}

	/**
	 * Prints the usage of main.
	 * @param error The error message that triggered the usage message.
	 * @param parser The args4j parser.
	 */
	private static void printUsage(String error, CmdLineParser parser) {
        System.out.println(error);
        System.out.print("Usage: DataSetMain ");
        parser.setUsageWidth(Integer.MAX_VALUE);
        parser.printSingleLineUsage(System.out);
        System.out.println("");
        return;
	}

}
