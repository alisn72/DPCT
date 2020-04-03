package bicat.algorithms.opsm;

import bicat.biclustering.Bicluster;
import bicat.util.LinkListElement;
import bicat.util.ParetoModelRecord;

import java.util.LinkedList;


public class Pmr2listConverter {
	
	public LinkedList list = new LinkedList();
	
	
	/**
	 * Constructor.
	 */
	public Pmr2listConverter() {
		
	}

	public LinkedList convert(ParetoModelRecord pmr){
		System.out.println("convert ComputedBiclusters list...");
		LinkListElement current = pmr.getRoot();
		for (int i = 0; i < pmr.getSize(); i++) {
			current = current.next;
			Bicluster currentBicluster = new Bicluster(i, current.model.arrayOfGeneIndices, current.model.arrayOfTissueIndices);
			list.add(currentBicluster);
		}
		System.out.println("OPSM LinkedList: "+ list.toString());
		return list;
		
	}

	
}
