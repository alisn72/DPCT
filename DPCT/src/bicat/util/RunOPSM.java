package bicat.util;

import bicat.biclustering.Dataset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

/**
 * Superclass of algorithm main classes.
 *
 * @author Thomas Frech
 * @version 2004-07-22
 */
public abstract class RunOPSM
{
	// dataset filename
	protected String datasetFilename;

	// dataset
	protected OPSMDataset dataset;

	// id string, used by run engine
	public static String idString = "";


//	/**
//	 * Constructor.
//	 *
//	 * @param datasetFilename dataset filename
//	 */
//	public RunOPSM(String datasetFilename)
//	{
//		// initialize random generator
//		if (Option.initializeRandomGenerator)
//			XMath.initRandom(11031979);
//
//		// load dataset
//		this.datasetFilename = datasetFilename;
//		dataset = new OPSMDataset(datasetFilename);
//	}

	/**
	 * Constructor.
	 *
	 * @param datasetFilename dataset filename
	 */
	public RunOPSM(Dataset dataset)
	{
	}
	
	/**
	 * Run algorithm, display and save result.
	 *
	 * @return result
	 */
	public ParetoModelRecord run()
	{
		if (dataset==null)
			return null;

		// initialize XMath
		XMath.initialize(Math.max(dataset.nr_rows, dataset.nr_col));

		// run algorithm
		if (Option.printMainMessages)
			System.out.println("\nAlgorithm started.");
		long start = System.currentTimeMillis();
		ParetoModelRecord result = runAlgorithm();
		long runtime = (System.currentTimeMillis()-start)/1000;
		if (Option.printMainMessages)
			System.out.println("Algorithm finished.\n");

		// display and save result
		if (Option.printMainMessages)
		{
			System.out.println("Pareto optimal models:\n");
			System.out.println(result.toString());
			System.out.println("Runtime: "+runtime+"s");
		}
		String algorithmName = algorithmName().toLowerCase().replace(' ','_').replace('-','_');
		String resultDirectoryname = datasetFilename+"."+algorithmName+"_result"+idString;
		if (Option.saveResults)
			result.save(resultDirectoryname);

		// write report
		String reportFilename = datasetFilename+"."+algorithmName+"_report"+idString+".txt";
		writeReport(dataset,result,resultDirectoryname,runtime,reportFilename);

		return result;
	}

	/**
	 * Subclasses have to call the algorithm in this method.
	 *
	 * @return result
	 */
	protected abstract ParetoModelRecord runAlgorithm();

	// write report
	private void writeReport(OPSMDataset dataset, ParetoModelRecord result, String resultDirectoryname, long runtime, String reportFilename)
	{
		if (Option.printMainMessages)
			System.out.print("Writing report to '"+reportFilename+"' ... ");

		try
		{
			// open stream
			File file = new File(reportFilename);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			PrintStream out = new PrintStream(fileOutputStream);

			// write header
			writeReportHeader(dataset,datasetFilename,runtime,out);

			// write parameter section
			out.println("parameters:");
			writeReportParameterSection(out);

			// write result section
			writeReportResultSection(dataset,result,resultDirectoryname,out);

			// close stream
			out.close();
			fileOutputStream.close();
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			if (Option.printErrors)
				System.out.println("ERROR: "+e.toString());
		}

		if (Option.printMainMessages)
			System.out.println("done");
	}

	// write report header
	private void writeReportHeader(OPSMDataset dataset, String datasetFilename, long runtime, PrintStream out)
	{
		String title = algorithmName()+" Algorithm Report";
		out.println(title);
		for (int i=0; i<title.length(); i++)
			out.print("-");
		out.println();
		out.println();

		Date date = new Date();
		out.println("date and time of run: "+date.toString());
		out.println();

		out.println("run time: "+runtime+"s");
		out.println();

		out.println("dataset loaded from file '"+datasetFilename+"':");
		out.println("   number of tissues        : m      = "+dataset.nr_col);
		out.println("   number of genes          : n      = "+dataset.nr_rows);
		if (dataset.plantedModel!=null)
		{
			out.println("   number of planted tissues: t      = "+dataset.plantedModel.lengthOfArrayOfTissueIndices);
			out.println("   number of planted genes  : g      = "+dataset.plantedModel.lengthArrayOfGeneIndices);
			dataset.plantedModel.U();
			out.println("   score                    : U(t,g) = "+dataset.plantedModel.U());
		}
		out.println();
	}

	/**
	 * Subclasses have to return the algorithm name in this method.
	 *
	 * @return algorithm name
	 */
	public abstract String algorithmName();

	/**
	 * Subclasses have to print the parameter section of the report in this method.
	 * Format: 1 line per parameter, 3 spaces at the beginning of each line.
	 *
	 * @param out print stream
	 */
	protected abstract void writeReportParameterSection(PrintStream out);

	// write report result section
	private void writeReportResultSection(OPSMDataset dataset, ParetoModelRecord result, String resultDirectoryname, PrintStream out)
	{
		if (result==null)
		{
			out.println();
			out.println("No models found.");
			return;
		}

		Model[] model = result.getArray();
		for (int index=0; index<model.length; index++)
		{
			// write model properties
			out.println();
			out.println("Model saved to '"+resultDirectoryname+"/model_"+(index+1)+".txt':");
			out.println("   number of tissues: s      = "+model[index].lengthOfArrayOfTissueIndices);
			out.println("   number of genes  : k      = "+model[index].lengthArrayOfGeneIndices);
			out.println("   score            : U(s,k) = "+model[index].U());

			// compare model to planted model
			if (dataset.plantedModel!=null)
			{
				int plantedTissuesFound = 0;
				int unplantedTissuesFound = 0;
				int plantedGenesFound = 0;
				int unplantedGenesFound = 0;

				for (int i=0; i<model[index].lengthOfArrayOfTissueIndices; i++)
					if (dataset.plantedModel.isInT[model[index].arrayOfTissueIndices[i]])
						plantedTissuesFound++;
					else
						unplantedTissuesFound++;
				for (int j=0; j<model[index].lengthArrayOfGeneIndices; j++)
					if (dataset.plantedModel.isInG[model[index].arrayOfGeneIndices[j]])
						plantedGenesFound++;
					else
						unplantedGenesFound++;

				out.println("   model contains:");
				out.println("      "+plantedTissuesFound+" of "+dataset.plantedModel.lengthOfArrayOfTissueIndices+" planted tissues");
				out.println("      "+unplantedTissuesFound+" unplanted tissues");
				out.println("      "+plantedGenesFound+" of "+dataset.plantedModel.lengthArrayOfGeneIndices+" planted genes");
				out.println("      "+unplantedGenesFound+" unplanted genes");
			}

			out.println("   model info:");
			out.println(model[index].infoString);
		}

		if (dataset.plantedModel==null)
		{
			out.println();
			out.println("No information about planted model available.");
		}
	}
}
