package bicat.util;

/**
 * Element of a linked model list.
 *
 * @author Thomas Frech
 * @version 2004-07-22
 */
public class LinkListElement
{
	/** payload */
	public Model model;

	/** previous element in linked list */
	public LinkListElement previous;

	/** next element in linked list */
	public LinkListElement next;

	/**
	 * Constructor.
	 *
	 * @param model payload
	 */
	public LinkListElement(Model model)
	{
		this.model = model;
		this.previous = null;
		this.next = null;
	}
}
