/*
 * 
 * Child process for SpellCheck (does spellchecking and returns incorrect words)
 * 
 * */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

public class SpellCheckChild {
	public static void main(String[] args) throws IOException {

		ObjectOutputStream objos = new ObjectOutputStream(System.out);
		ObjectInputStream ois = new ObjectInputStream(System.in);

		TreeSet<String> ts = new TreeSet<String>(); // for storing dictionary
		LinkedList<String> ll = new LinkedList<String>(); // for storing words to be checked

		try {
			ts = (TreeSet<String>) ois.readObject();
			while (true) {
				String str = (String) ois.readObject();
				ll.add(str);

			}

		} catch (Throwable t) {
			ois.close();
		}

		ArrayList<String> al = new ArrayList<String>();
		try {
			objos.flush();

			// checks words against dictionary, then adds to misspelled words
			// ArrayList al
			while (!ll.isEmpty()) {
				String str = ll.removeFirst();
				if (!ts.contains(str))
					al.add(str);
			}
			objos.writeObject(al); // sends misspelled words ArrayList to parent
		} catch (Throwable t) {

		}
		System.exit(0);
	}
}
