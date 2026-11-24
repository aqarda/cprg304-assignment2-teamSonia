package implementations;

/**
 * A single node in a doubly linked list (DLL), used as the building block
 * for {@link MyDLL} in CPRG 304 at SAIT.
 * <p>
 * Each node stores a {@link String} element and maintains two directional
 * references: a predecessor ({@code pred}) pointing to the node immediately
 * before it in the chain, and a successor ({@code succ}) pointing to the
 * node immediately after it. A {@code null} predecessor indicates the node
 * is the head of the list; a {@code null} successor indicates it is the tail.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code DLLNode<E>}), allowing any object type to be stored in the node.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its usages.
 * </p>
 */
public class MyDLLNode
{
    private String element;
    private MyDLLNode pred;
    private MyDLLNode succ;

    /**
     * Creates a node with no neighbors.
     *
     * @param element the string to store
     * @throws NullPointerException if the element is null
     */
    public MyDLLNode(String element)
    {
        setElement(element);
    }

    /**
     * Returns the stored string.
     *
     * @return the node's element
     */
    public String getElement()
    {
        return element;
    }

    /**
     * Replaces the stored string.
     *
     * @param element the new string to store
     * @throws NullPointerException if the element is null
     */
    public void setElement(String element)
    {
        if (element == null)
        {
            throw new NullPointerException("Element must not be null.");
        }
        this.element = element;
    }

    /**
     * Returns the node that precedes this node.
     *
     * @return the previous node, or null at the head
     */
    public MyDLLNode getPred()
    {
        return pred;
    }

    /**
     * Updates the link to the previous node.
     *
     * @param pred the previous node, or null at the head
     */
    public void setPred(MyDLLNode pred)
    {
        this.pred = pred;
    }

    /**
     * Returns the node that follows this node.
     *
     * @return the next node, or null at the tail
     */
    public MyDLLNode getSucc()
    {
        return succ;
    }

    /**
     * Updates the link to the next node.
     *
     * @param succ the next node, or null at the tail
     */
    public void setSucc(MyDLLNode succ)
    {
        this.succ = succ;
    }
}