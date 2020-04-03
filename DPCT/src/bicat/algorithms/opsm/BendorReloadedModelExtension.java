package bicat.algorithms.opsm;

/**
 * Provides functionality to store a model extension with its score and to
 * realize the extension, if it belongs to the best ones.
 *
 * @author Thomas Frech
 * @version 2004-07-22
 */
public final class BendorReloadedModelExtension extends BendorReloadedModelEvaluation
{
	// model to extend
	public BendorReloadedModel model;

	// extension
	public int t;


	/**
	 * Constructor.
	 *
	 * @param model model to extend
	 * @param t extension column index
	 */
	public BendorReloadedModelExtension(BendorReloadedModel model, int t)
	{
		if (model.isComplete())
			throw new RuntimeException("Model already complete.");

		this.model = model;
		this.t = t;
		p = model.getScoreForExtension(t);
	}

	// realize evaluated model
	public BendorReloadedModel realize()
	{
		BendorReloadedModel child = new BendorReloadedModel(model);
		child.extend(t);
		return child;
	}
}
