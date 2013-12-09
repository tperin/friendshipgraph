package app;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import structures.Graph;

/**
 * Runner for Friendship Graph project
 * @author Timothy Perin
 * @author Tyng-An Lee
 *
 */
public class Friends {
	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("Enter file");
		Scanner sc = new Scanner(System.in);
		String file = sc.nextLine();
		
		Graph fg = null;
		try {
			fg = new Graph(file);
		} catch (FileNotFoundException e) {
			System.out.println("Invalid file");
			System.exit(0);
		}

		int choice;
		do {
			printMenu();
			choice = Integer.parseInt(sc.nextLine());
			
			if (choice == 1) { //students from school
				System.out.println("Enter school name");
				String school = sc.nextLine().toLowerCase();
				Graph subgraph = fg.studentsAtSchool(school);
				subgraph.print();
			}
			else if (choice == 2) { //shortest path
				System.out.println("Enter person 1");
				String p1 = sc.nextLine();
				System.out.println("Enter person 2");
				String p2 = sc.nextLine();
				String chain = fg.getShortestPath(p1, p2);
				if (chain == null) System.out.println("No connection exists between these two people");
				else System.out.println(chain);
			}
			else if (choice == 3) { //cliques at school
				System.out.println("Enter school name");
				String school = sc.nextLine().toLowerCase();
				ArrayList<Graph> cliques = fg.getCliques(school);
				for (int i = 0; i < cliques.size(); i++) {
					System.out.println("Clique " + (i+1));
					cliques.get(i).print();
				}
			}
			else if (choice == 4) { //connectors
				ArrayList<String> connectors = fg.getConnectors();
				for (String s : connectors) System.out.println(s);
			}
			else if (choice > 5 || choice < 1) System.out.println("Invalid entry");
		} while (choice != 5);
		
		//if we get here then the user chose to quit
		System.out.println("Exiting program");
		sc.close();
	}
	
	private static void printMenu() {
		System.out.println("Choose an option");
		System.out.println("[1] Students at school");
		System.out.println("[2] Shortest intro chain");
		System.out.println("[3] Cliques at school");
		System.out.println("[4] Connectors");
		System.out.println("[5] Quit");
	}
}
