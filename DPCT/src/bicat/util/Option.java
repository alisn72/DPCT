package bicat.util;

/**
 * This class contains option flags which have to be set at compile time.
 * The option flags were introduced to keep the command line parameter list
 * short while making programmers life easier, as message level and other
 * options can be changed without editing much program code.
 *
 * @author Thomas Frech
 * @version 2004-07-22
 */
public class Option
{
	/** print messages in main method of an algorithm */
	public static boolean printMainMessages = true;

	/** print detailed messages while running an algorithm */
	public static boolean printMessages = false;

	/** print progress messages while running an algorithm */
	public static boolean printProgress = false;

	/** print warnings */
	public static boolean printWarnings = true;

	/** print errors */
	public static boolean printErrors = true;

	/** save results */
	public static boolean saveResults = true;

	/** ask user for confirmation before overwriting result files */
	public static boolean askBeforeOverwritingFiles = false;

	/** extend dataset filename with dataset size, planted OPSM size */
	public static boolean extendDatasetFilenames = true;

	/** initialize random generator with predefined value */
	public static boolean initializeRandomGenerator = false;

	/** do not calculate huge binomials and factorials */
	public static boolean ignoreXMathOverflow = true;
}
