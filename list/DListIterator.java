/* DListIterator.java */

package list;
import java.util.Iterator;

/**
 *  A DListIterator defines an iterator for DList
 *
 **/

public class DListIterator implements Iterator {

    private ListNode curr;

    public DListIterator(ListNode first) {
        this.curr = first;
    }

    public boolean hasNext() {
        if (curr.isValidNode()) {
            return true;
        }
        else {
            return false;
        }
    }

    public DListNode next() {
        try {
            ListNode temp = curr;
            curr = curr.next();
            return ((DListNode) temp);
        }
        catch (InvalidNodeException i) {
            System.out.println("Caught an InvalidNodeException in DListIterator that shouldn't ever happen.");
            return null;
        }
    }

    public void remove() {
        System.out.println("DListIterator.remove() is not supported");
    }
}