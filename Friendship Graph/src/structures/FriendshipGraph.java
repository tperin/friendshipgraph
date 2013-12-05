package structures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author Sesh Venugopal. May 31, 2013.
 */
public class FriendshipGraph {
	int size;
	ArrayList<Person> people;
	HashMap<String,Integer> indexes;

	
	public FriendshipGraph(String file) throws FileNotFoundException {
		
		Scanner sc = new Scanner(new File(file));
		size = Integer.parseInt(sc.nextLine().trim());
		people = new ArrayList<Person>(size);
		indexes = new HashMap<String,Integer>(size);
		
		for (int i = 0; i < size; i++) {
			String line = sc.nextLine().trim().toLowerCase();
			String[] data = line.split("\\|");
			if (data.length < 3) {
				people.add(new Person(data[0], null, null));
			}
			else {
				people.add(new Person(data[0], data[2], null));
			}
			indexes.put(data[0], i);
		}
		
		while (sc.hasNextLine()) {
			String line = sc.nextLine().trim().toLowerCase();
			String[] t = line.split("\\|");
			int i1 = indexes.get(t[0]);
			int i2 = indexes.get(t[1]);
			
			people.get(i1).friends = new Friend(i2, people.get(i1).friends);
			people.get(i2).friends = new Friend(i1, people.get(i2).friends);
		}
		
	}
	
	public FriendshipGraph(ArrayList<Person> people) {
		
	}
	
	public int nameToIndex(String name) {
		return indexes.get(name);
	}
	public String indexToName(int index) {
		return people.get(index).name;
	}
	
	public FriendshipGraph peopleAtSchool(String school) {
		
		return null;
	}
	
	public void print() {
		System.out.println(size);
		
		for (int i = 0; i < size; i++) {
			String line = "";
			line += indexToName(i) + "|";
			if (people.get(i).school == null) {
				line += "n";
			}
			else {
				line += "y|";
				line += people.get(i).school;
			}
			System.out.println(line);
		}
		
		for (int j = 0; j < size; j++) {
			Friend ptr = people.get(j).friends;
			while (ptr != null) {
				if (j < ptr.index) {
					System.out.println(people.get(j).name + "|" + indexToName(ptr.index));
				}
				ptr = ptr.next;
			}
		}
	}

}
