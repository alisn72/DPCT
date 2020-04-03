package bicat.util;

import java.io.File;

/**
 * <p>
 * Provides functionality to store a pareto set of models.
 * </p>
 * 
 * <p>
 * The objective values are <tt>s</tt> and <tt>k</tt>, which shall be
 * maximized. Models with <tt>s<=1</tt> or <tt>k<=1</tt> are not accepted.
 * </p>
 * 
 * <p>
 * The models are stored in a bidirectional linked list with dummy root.
 * </p>
 * 
 * @author Thomas Frech
 * @version 2004-07-22
 */
public class ParetoModelRecord{
	/**
	 * 
	 */
	// linked list of OPSMs (pareto optimal models), root is a dummy element
	private LinkListElement root = new LinkListElement(null);

	// record size (without root)
	private int size = 0;
		
	/**
	 * Constructor.
	 */
	public ParetoModelRecord() {
	}

	/**
	 * Combine this record with another record.
	 * 
	 * @param record
	 *            record
	 */
	public void add(ParetoModelRecord record) {
		if (record == null)
			return;

		Model[] model = record.getArray();
		for (int i = 0; i < model.length; i++)
			add(model[i]);
	}

	/**
	 * Add model to the record, if it is pareto optimal.
	 * 
	 * @param model
	 *            model
	 */
	public void add(Model model) {
		// do not accept trivial models
		if (model == null || model.lengthOfArrayOfTissueIndices <= 1 || model.lengthArrayOfGeneIndices <= 1)
			return;

		for (LinkListElement cursor = root.next; cursor != null; cursor = cursor.next) {
			// is new model dominated by model in record?
			if ((cursor.model.lengthOfArrayOfTissueIndices > model.lengthOfArrayOfTissueIndices && cursor.model.lengthArrayOfGeneIndices >= model.lengthArrayOfGeneIndices)
					|| (cursor.model.lengthArrayOfGeneIndices > model.lengthArrayOfGeneIndices && cursor.model.lengthOfArrayOfTissueIndices >= model.lengthOfArrayOfTissueIndices)) {
				return;
			}
			// is new model equal to model in record?
			else if (cursor.model.lengthOfArrayOfTissueIndices == model.lengthOfArrayOfTissueIndices
					&& cursor.model.lengthArrayOfGeneIndices == model.lengthArrayOfGeneIndices) {
				if (model.isEqualTo(cursor.model))
					return;
			}
			// does new model dominate model in record?
			else if ((model.lengthOfArrayOfTissueIndices > cursor.model.lengthOfArrayOfTissueIndices && model.lengthArrayOfGeneIndices >= cursor.model.lengthArrayOfGeneIndices)
					|| (model.lengthArrayOfGeneIndices > cursor.model.lengthArrayOfGeneIndices && model.lengthOfArrayOfTissueIndices >= cursor.model.lengthOfArrayOfTissueIndices)) {
				removeModel(cursor);
				System.out.println("model removed");
			}
		}
		insertModel(model);
		//System.out.println("model inserted");
	}

	// Subfunction of <tt>add(model)</tt>: insert model into linked list.
	private void insertModel(Model model) {
		LinkListElement newElement = new LinkListElement(model);
		newElement.next = root.next;
		if (newElement.next != null)
			newElement.next.previous = newElement;
		newElement.previous = root;
		root.next = newElement;
		size++;
	}

	// Subfunction of <tt>add(model)</tt>: remove model from linked list.
	private void removeModel(LinkListElement element) {
		element.previous.next = element.next;
		if (element.next != null)
			element.next.previous = element.previous;
		size--;
	}

	/**
	 * Get model array.
	 * 
	 * @return model array
	 */
	public Model[] getArray() {
		Model[] array = new Model[size];
		int index = 0;

		for (LinkListElement element = root.next; element != null; element = element.next)
			array[index++] = element.model;

		return array;
	}

	/**
	 * <p>
	 * Create model record string.
	 * </p>
	 * 
	 * <p>
	 * Record format:
	 * </p>
	 * 
	 * <pre>
	 *  --------------------------------------------------
	 *  model 1
	 *  --------------------------------------------------
	 *  model 2
	 *  --------------------------------------------------
	 *  model 3
	 *  --------------------------------------------------
	 *  ...
	 *  --------------------------------------------------
	 *  model x
	 *  --------------------------------------------------
	 * </pre>
	 * 
	 * @return model record string
	 */
	public String toString() {
		String BR = (char) 13 + "" + (char) 10;

		StringBuffer buffer = new StringBuffer();

		for (LinkListElement element = root.next; element != null; element = element.next) {
			buffer.append("--------------------------------------------------"
					+ BR);
			buffer.append(element.model.toString());
		}

		buffer
				.append("--------------------------------------------------"
						+ BR);

		return buffer.toString();
	}

	/**
	 * <p>
	 * Save all models to files. The models are given the name 'model_'+
	 * index+'.txt'.
	 * </p>
	 * 
	 * @param directoryname
	 *            directory name where all models are saved to
	 */
	public void save(String directoryname) {
		if (Option.printMessages)
			System.out.print("Saving all pareto-optimal models ... ");

		// create result directory
		try {
			File file = new File(directoryname);
			if (!file.exists()) {
				if (!file.mkdir())
					throw new Exception("Directory could not be created!");
			} else {
				if (file.isFile()) {
					file.delete();
					if (!file.mkdir())
						throw new Exception("Directory could not be created!");
				} else if (file.list().length > 0) {
					char answer = 'y';

					if (Option.askBeforeOverwritingFiles) {
						System.out
								.print("WARNING: Directory is not empty! Empty now? (y/n): ");
						answer = (char) System.in.read();
						System.in.read();
					}

					if (answer == 'y') {
						if (Option.printMessages)
							System.out.print("Deleting old files ... ");
						File[] list = file.listFiles();
						for (int i = 0; i < list.length; i++)
							list[i].delete();
						if (Option.printMessages)
							System.out.println("done");
					}

					if (Option.printMessages)
						System.out
								.print("Saving all pareto-optimal models ... ");
				}
			}
		} catch (Exception e) {
			if (Option.printErrors)
				System.out.println("ERROR: " + e.toString());
			return;
		}

		// save all models to the result directory
		int index = 1;
		for (LinkListElement element = root.next; element != null; element = element.next) {
			element.model.save(directoryname + "/" + "model_" + (index++)
					+ ".txt");
		}

		if (Option.printMessages)
			System.out.println("done");
	}

	public boolean anyModelEquals(Model model) {
		for (LinkListElement element = root.next; element != null; element = element.next)
			if (element.model.equals(model))
				return true;

		return false;
	}

	public boolean anyModelContains(Model submodel) {
		for (LinkListElement element = root.next; element != null; element = element.next)
			if (element.model.contains(submodel))
				return true;

		return false;
	}
	
	public int getSize(){
		return size;
	}
	public LinkListElement getRoot(){
		return root;
	}

}
