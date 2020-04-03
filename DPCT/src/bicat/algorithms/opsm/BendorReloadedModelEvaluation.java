package bicat.algorithms.opsm;

/**
 * Serves as common superclass of <tt>BendorReloadedModelInitialization</tt> and
 * <tt>BendorReloadedModelExtension</tt>.
 *
 * @author Thomas Frech
 * @version 2004-07-22
 */
public abstract class BendorReloadedModelEvaluation
{
	// score p
	public double p = 0;

	// realize evaluated model
	public abstract BendorReloadedModel realize();
}
