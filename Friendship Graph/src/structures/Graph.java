package structures;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


class Neighbor {
    public int vertexNum;
    public Neighbor next;
    
    public Neighbor(int vnum, Neighbor nbr) {
            this.vertexNum = vnum;
            next = nbr;
    }
}

class Vertex {
    String name;
    Neighbor adjList;
    String school;
    public int dfsnum;
    public int backnum;
    
    Vertex(String name, String school, Neighbor neighbors) {
            this.name = name;
            this.school = school;
            this.adjList = neighbors;
    }
    public void addNeighbor(Neighbor nbr) {
		nbr.next = adjList;
		adjList = nbr;
    }
}

public class Graph {

	public ArrayList<Vertex> adjList; //used to store all the vertexes
	HashMap<String,Integer> indexes; //used for quick conversion from person to index number
	
	//this constructor is for subgraphs
	public Graph() {
		adjList = new ArrayList<Vertex>();
		indexes = new HashMap<String,Integer>();
	}
	
	public Graph(String file) throws FileNotFoundException {
		
		Scanner sc = new Scanner(new File(file));
		int size = Integer.parseInt(sc.nextLine().trim()); //first line is graph size
		adjList = new ArrayList<Vertex>(size);
		indexes = new HashMap<String,Integer>(size);
		
		for (int i = 0; i < size; i++) {
			String line = sc.nextLine().trim().toLowerCase();
			String[] data = line.split("\\|");
			if (data.length < 3) { //they have no school
				adjList.add(new Vertex(data[0], null, null));
			}
			else { //they have a school
				adjList.add(new Vertex(data[0], data[2], null));
			}
			indexes.put(data[0], i);
		}
		
		while (sc.hasNextLine()) { //go through edges
			String line = sc.nextLine().trim().toLowerCase();
			String[] t = line.split("\\|");
			int i1 = indexes.get(t[0]);
			int i2 = indexes.get(t[1]);
			
			adjList.get(i1).addNeighbor(new Neighbor(i2, null));
			adjList.get(i2).addNeighbor(new Neighbor(i1, null));
		}
	}

	public void addVertex(Vertex v) {
		int index = adjList.size();
		adjList.add(new Vertex(v.name, v.school, null));
		indexes.put(v.name,index);
	}
	
	public void addVertex(String name, String school, Neighbor nbr) {
		int index = adjList.size();
		adjList.add(new Vertex(name, school, nbr));
		indexes.put(name, index);
	}
	
	private int nameToIndex(String name) {
		return indexes.get(name);
	}
	
	public String indexToName(int index) {
		return adjList.get(index).name;
	}
	
	public Graph studentsAtSchool(String schoolName) {
		int size = adjList.size();
		Graph subgraph = new Graph();
		int counter = 0;
		for (int i = 0; i < size; i++) { //add students from school
			if (adjList.get(i).school != null && adjList.get(i).school.equals(schoolName)) {
				Vertex old = adjList.get(i);
				Vertex newVertex = new Vertex(old.name, old.school, null);
				subgraph.adjList.add(newVertex);
				subgraph.indexes.put(old.name, counter);
				counter++;
			}
		}
		for (int i = 0; i < subgraph.adjList.size(); i++) { //add edges for that school
			Vertex newRef = subgraph.adjList.get(i);
			Vertex origRef = adjList.get(nameToIndex(newRef.name));
			
			Neighbor friendPtr = origRef.adjList;
			while (friendPtr != null) {
				Vertex friend = adjList.get(friendPtr.vertexNum);
				if (friend.school != null && friend.school.equals(schoolName)) {
					int newIndex = subgraph.nameToIndex(friend.name);
					newRef.addNeighbor(new Neighbor(newIndex, null));
				}
				friendPtr = friendPtr.next;
			}
		}
		return subgraph;
	}
	
	public boolean hasEdge(Vertex v1, Vertex v2) {
		Neighbor v1friends = v1.adjList;
		while (v1friends != null) {
			if (indexToName(v1friends.vertexNum).equals(v2.name)) {
				return true;
			}
			v1friends = v1friends.next;
		}
		return false;
	}
	
	public Graph graphWithoutVertex(String name) { //generates graph without the given person
		int index = nameToIndex(name);
		Graph subgraph = new Graph();
		for (int i = 0; i < adjList.size(); i++) {
			if (i != index) {
				subgraph.addVertex(adjList.get(i));
			}
		}
		for (int i = 0; i < subgraph.adjList.size(); i++) {
			String person = subgraph.adjList.get(i).name;
			Neighbor ptr = adjList.get(nameToIndex(person)).adjList;
			while (ptr != null) {
				if (ptr.vertexNum != index) {
					String nName = indexToName(ptr.vertexNum);
					int newIndex = subgraph.nameToIndex(nName);
					subgraph.adjList.get(i).addNeighbor(new Neighbor(newIndex, null));
				}
				ptr = ptr.next;
			}
		}
		return subgraph;
	}
	
	public boolean isConnector(int index) { //junk
		Vertex person = adjList.get(index);
		int cliquesWith = getCliques(person.school).size();
		int cliquesWithout = graphWithoutVertex(person.name).getCliques().size();
		return false;
	}

	public void dfsTopsort() { //not done
		boolean[] visited = new boolean[adjList.size()];
		boolean[] backed = new boolean[adjList.size()];
		
		for (int v=0; v < visited.length; v++) {
			visited[v] = false;
			backed[v] = false;
			adjList.get(v).dfsnum = 0;
			adjList.get(v).backnum = 0;
		}
		int num = 0;
		ArrayList<Vertex> connectors = new ArrayList<Vertex>();
		for (int v=0; v < visited.length; v++) {
			if (!visited[v]) {
				System.out.println("Starting at " + indexToName(v));
				dfsTopsort(v, visited, num, connectors);
			}
		}
		
		for (Vertex v : connectors) {
			System.out.println(v.name);
		}
		
	}
	
	//still need to fix check for start point connector
	private void dfsTopsort(int v, boolean[] visited, int num, ArrayList<Vertex> connectors) { //not done
		visited[v] = true;
		//System.out.println("Visiting " + indexToName(v));
		adjList.get(v).dfsnum = num;
		adjList.get(v).backnum = num;
		//System.out.println(indexToName(v) + " " + adjList.get(v).dfsnum);
		num++;
		
		for (Neighbor e=adjList.get(v).adjList; e != null; e=e.next) {
			if (!visited[e.vertexNum]) {
				dfsTopsort(e.vertexNum, visited, num, connectors);
				if (adjList.get(v).dfsnum > adjList.get(e.vertexNum).backnum) {
					adjList.get(v).backnum = Math.min(adjList.get(v).backnum,adjList.get(e.vertexNum).backnum);
				}
				else if (!connectors.contains(adjList.get(v))) connectors.add(adjList.get(v));
			}
			else {
				//System.out.println("Been here before " + indexToName(e.vertexNum));
				adjList.get(v).backnum = Math.min(adjList.get(v).backnum, adjList.get(e.vertexNum).dfsnum);
			}
		}
	}
	
	public ArrayList<Vertex> getConnectors() { //junk
		
		return null;
	}
	
	public ArrayList<Graph> getCliques(String schoolName) {
		return studentsAtSchool(schoolName).getCliques();
	}
	
	public ArrayList<Graph> getCliques() {
		boolean[] visited = new boolean[adjList.size()];
		ArrayList<Graph> cliques = new ArrayList<Graph>();
		for (int v=0; v < visited.length; v++) {
            visited[v] = false;
		}
        for (int v=0; v < visited.length; v++) {
            if (!visited[v]) {
            		Graph clique = new Graph();
                    dfs(v, visited, clique);
                    cliques.add(clique);
            }
        }
        for (int i = 0; i < cliques.size(); i++) { //this is all just adding edges for each clique
        	Graph clique = cliques.get(i);
        	for (int j = 0; j < clique.adjList.size(); j++) {
        		Vertex person = clique.adjList.get(j);
        		int origIndex = nameToIndex(person.name);
        		Neighbor ptr = adjList.get(origIndex).adjList;
        		while (ptr != null) {
        			String neighborName = indexToName(ptr.vertexNum);
        			int cliqueIndex = clique.nameToIndex(neighborName);
        			person.addNeighbor(new Neighbor(cliqueIndex, null));
        			//System.out.println(neighborName + " " + cliqueIndex);
        			ptr = ptr.next;
        		}
        	}
        }
		return cliques;
	}
	
	private void dfs(int v, boolean[] visited, Graph clique) { //used for getting cliques
		visited[v] = true;
		clique.addVertex(adjList.get(v).name, adjList.get(v).school, null);
		for (Neighbor e=adjList.get(v).adjList; e != null; e=e.next) {
			if (!visited[e.vertexNum]) {
				//System.out.println(adjList.get(v).name + "--" + adjList.get(e.vertexNum).name);
				dfs(e.vertexNum, visited, clique);
			}
		}
	}
	
	public void print() {
		int size = adjList.size();
		System.out.println(size);
		
		for (int i = 0; i < size; i++) {
			String line = "";
			line += indexToName(i) + "|";
			if (adjList.get(i).school == null) {
				line += "n";
			}
			else {
				line += "y|";
				line += adjList.get(i).school;
			}
			System.out.println(line);
		}
		
		for (int j = 0; j < size; j++) {
			Neighbor ptr = adjList.get(j).adjList;
			while (ptr != null) {
				if (j < ptr.vertexNum) {
					System.out.println(adjList.get(j).name + "|" + indexToName(ptr.vertexNum));
				}
				ptr = ptr.next;
			}
		}
	}

}
