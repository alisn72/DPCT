package bicat.algorithms.opsm;

import bicat.util.OPSMDataset;
import bicat.util.XMath;
//import opsmextraction.util.*;

/**
 * Provides functionality to store a model initialization with its score and to
 * create the model, if it belongs to the best ones.
 *
 * @author Thomas Frech
 * @version 2004-07-22
 */
public final class BendorReloadedModelInitialization extends BendorReloadedModelEvaluation
{
	// dataset
	public OPSMDataset dataset;

	// model size
	public int s;

	// column with lowest rank
	public int ta;

	// column with highest rank
	public int tb;


	/**
	 * Constructor.
	 *
	 * @param dataset dataset
	 * @param s model size
	 * @param ta column index of lowest rank
	 * @param tb column index of highest rank
	 */
	public BendorReloadedModelInitialization(OPSMDataset dataset, int s, int ta, int tb)
	{
		this.dataset = dataset;
		this.s = s;
		this.ta = ta;
		this.tb = tb;
		p = getScore();
	}

	// get score of initial partial model
	private double getScore()
	{
		int n = dataset.nr_rows;
		int m = dataset.nr_col;
		int[][] D = dataset.rankedDataMatrix;

		// calculate exact value for p if initialized model is complete
		if (s==2)
		{
			int k = 0;

			for (int row=0; row<dataset.nr_rows; row++)
				if (D[row][ta]<D[row][tb])
					k++;

			return (double)k/(double)n;
		}

		// else do newton iteration to estimate p
		int gapSize = s - 2;
		double commonDenominator = XMath.binomial(m, s);
		double B = 1.0/(XMath.binomial(m, 2)*2);
		double[] A = new double[n];
		double[] diff = new double[n];
		double[] denominator = new double[n];
		double epsilon = BendorReloadedModel.epsilon;

		// calculate A[i] and diff[i]
		for (int i=0; i<n; i++)
		{
			int gi = D[i][tb] - D[i][ta] - 1;
			A[i] = XMath.binomial(gi, gapSize)/commonDenominator;
			diff[i] = A[i] - B;
		}

		// initial value for p
		double p = BendorReloadedModel.p0;

		// do at most 20 newton iteration steps to get p
		for (int iteration=0; iteration<20; iteration++)
		{
			double f = -n;
			double dfdp = 0;

			for (int i=0; i<n; i++)
			{
				denominator[i] = diff[i]*p + B;

				if (A[i]==0)
				{
				}
				else if (denominator[i]==0)
				{
					System.out.println("WARNING: Division by 0 in Newton iteration.");
					f = 0;
					dfdp = 1;
				}
				else
				{
					f += A[i]/denominator[i];
					dfdp -= A[i]*diff[i]/(denominator[i]*denominator[i]);
				}
			}

			if (dfdp==0)
			{
				return 0;
			}
			else
			{
				double q = p;

				p -= f/dfdp;

				if (Math.abs(f/dfdp)<epsilon)
					break;

				// special modification of newton algorithm, required to prevent from being caught in negative range
				if (p<0 && iteration<19)
					p = q/2;
			}
		}

		if (p<0 || p>1)
			p = 0;

		return p;
	}

	// realize evaluated model
	public BendorReloadedModel realize()
	{
		return new BendorReloadedModel(dataset,s,ta,tb);
	}
}
