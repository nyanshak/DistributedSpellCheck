/*
 * 
 * Program takes 3 arguments: # of processes, input file (to be checked), and dictionary file.
 * It splits the work into the appropriate number of processes and checks for misspelled words.
 * 
 * */

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

public class SpellCheck {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		String error = "Usage:  java SpellCheck #processes inFile dictionaryFile";
		int processes = 0;
		if (args.length == 3) {
			// processes the arguments
			try {
				processes = Integer.parseInt(args[0]);
				if (processes <= 0) {
					System.out
							.println("#processes argument must be greater than 0.");
					System.exit(-1);
				}
				if (processes > 8) {
					System.out
							.println("#processes argument must be 8 or fewer.");
					System.exit(-1);
				}

			} catch (NumberFormatException ex) {
				System.out.println(error);
				return;
			}
			String dictFile = args[2];
			String inFile = args[1];
			if ((new File(dictFile)).exists() & (new File(inFile)).exists()) {

				Runtime rt = Runtime.getRuntime();
				Process[] proc = new Process[processes];
				ObjectInputStream[] ois = new ObjectInputStream[processes];
				ObjectOutputStream[] oos = new ObjectOutputStream[processes];
				System.out.println("\nRunning with parameters:\n\tProcesses: " + processes + "\n\tDocument: " +
									args[1] + "\n\tDictionary File: " + args[2]+ "\n\nMisspelled Words:");
				// starts specified # of processes
				for (int i = 0; i < processes; i++) {
					proc[i] = rt.exec("java SpellCheckChild");
				}

				// opens proper # of output & input streams
				for (int i = 0; i < processes; i++) {
					ois[i] = new ObjectInputStream(proc[i].getInputStream());
					oos[i] = new ObjectOutputStream(proc[i].getOutputStream());
				}

				// TreeSet used instead of HashSet to show improvement (HashSet too fast)
				TreeSet<String> ts = new TreeSet<String>(); // <- Words from Dictionary file go here
				
				Scanner sc = new Scanner(new File(dictFile));
				while (sc.hasNext()) {
					ts.add(sc.next());
				}

				// gives the children the dictionary file
				for (int i = 0; i < processes; i++) {
					oos[i].writeObject(ts);
				}
				Scanner reader = new Scanner(new File(inFile));

				int m = 0;
				// splits document among child processes
				while (reader.hasNext()) {
					oos[m].writeObject(reader.next());
					if (m == (processes - 1)) {
						m = 0;
					} else
						m++;
				}

				// closes output streams || housekeeping
				for (int i = 0; i < processes; i++) {
					oos[i].close();
				}

				// gets misspelled words back from children
				ArrayList<String> al = new ArrayList<String>();
				for (int i = 0; i < processes; i++) {
					al.addAll(0, (ArrayList<String>) ois[i].readObject());
				}
				System.out.println("\t" + al + "\n");
				
				// closes input streams || housekeeping
				for (int i = 0; i < processes; i++) {
					ois[i].close();
					proc[i] = null;
				}

			} else {
				if (!(new File(dictFile)).exists()) {
					System.out.println("Dictionary file (" + dictFile
							+ ") could not be found");

				}
				if (!(new File(inFile)).exists()) {
					System.out.println("Document to be spellchecked (" + inFile
							+ ") could not be found");
				}
				System.exit(-1);
			}
		} else {
			System.out.println(error);
			return;
		}
	}
}
