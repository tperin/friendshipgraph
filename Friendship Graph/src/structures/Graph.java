package structures;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


class Neighbor {
    public int index;
    public Neighbor next;
    
    public Neighbor(int index) {
    	this.index = index;
    	next = null;
    }
    public Neighbor(int index, Neighbor nbr) {
            this.index = index;
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
    public boolean equals(Vertex v) {
    	return this.name == v.name && this.school == v.school;
    }
}

public class Graph {

	public ArrayList<Vertex> adjList; //used to store all the vertexes
	HashMap<String,Integer> indexes; //used for quick conversion from person to index number
	
	/**
	 * Constructor with no parameters used to make subgraphs
	 */
	public Graph() {
		adjList = new ArrayList<Vertex>();
		indexes = new HashMap<String,Integer>();
	}
	
	/**
	 * Constructor used to create initial graph from file
	 * @param file
	 * @throws FileNotFoundException
	 */
	public Graph(String file) throws FileNotFoundException {
		
		Scanner sc = new Scanner(new File(file));
		int size = Integer.parseInt(sc.nextLine()); //first line is graph size
		adjList = new ArrayList<Vertex>(size);
		indexes = new HashMap<String,Integer>(size);
		
		for (int i = 0; i < size; i++) {
			String line = sc.nextLine().toLowerCase();
			String[] data = line.split("\\|"); //split the line by the pipe token
			
			if (data.length < 3) { //they have no school
				adjList.add(new Vertex(data[0], null, null));
			}
			else { //they have a school
				adjList.add(new Vertex(data[0], data[2], null));
			}
			indexes.put(data[0], i);
		}
		
		while (sc.hasNextLine()) { //go through edges/friendships
			String line = sc.nextLine().toLowerCase();
			String[] t = line.split("\\|");
			int i1 = indexes.get(t[0]);
			int i2 = indexes.get(t[1]);
			
			adjList.get(i1).addNeighbor(new Neighbor(i2));
			adjList.get(i2).addNeighbor(new Neighbor(i1));
		}
		sc.close();
	}

	/**
	 * Adds vertex to graph, really just used for subgraphs since the initial graph has a set size
	 * @param v
	 */
	public void addVertex(Vertex v) {
		int index = adjList.size(); //index for new addition will be the size of the graph
		adjList.add(new Vertex(v.name, v.school, null));
		indexes.put(v.name,index);
	}
	
	public void addVertex(String name, String school, Neighbor nbr) {
		int index = adjList.size();
		adjList.add(new Vertex(name, school, nbr));
		indexes.put(name, index);
	}
	
	/**
	 * Converts name string to its index from the hash table
	 * @param name
	 * @return vertex's index
	 */
	private int nameToIndex(String name) {
		return indexes.get(name);
	}
	
	/**
	 * Converts index to name string
	 * @param index
	 * @return name of given index
	 */
	public String indexToName(int index) {
		return adjList.get(index).name;
	}
	
	/**
	 * Creates a new subgraph for students from given school
	 * @param schoolName
	 * @return subgraph
	 */
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
				Vertex friend = adjList.get(friendPtr.index);
				if (friend.school != null && friend.school.equals(schoolName)) {
					int newIndex = subgraph.nameToIndex(friend.name);
					newRef.addNeighbor(new Neighbor(newIndex));
				}
				friendPtr = friendPtr.next;
			}
		}
		return subgraph;
	}

	/**
	 * Returns list of connectors from graph using dfs
	 * Note: still need to implement check for starting point connectors
	 */
	public ArrayList<String> getConnectors() { 
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
				dfsTopsort(v, visited, num, connectors);
			}
		}
		ArrayList<String> result = new ArrayList<String>(connectors.size());
		for (Vertex v : connectors) result.add(v.name);
		return result;
		
	}
	
	//still need to fix check for start point connector
	/**
	 * DFS used for finding connectors
	 */
	private void dfsTopsort(int v, boolean[] visited, int num, ArrayList<Vertex> connectors) { //not done
		visited[v] = true;
		adjList.get(v).dfsnum = num;
		adjList.get(v).backnum = num;
		num++;
		
		for (Neighbor e=adjList.get(v).adjList; e != null; e=e.next) {
			if (!visited[e.index]) {
				dfsTopsort(e.index, visited, num, connectors);
				if (adjList.get(v).dfsnum > adjList.get(e.index).backnum) {
					adjList.get(v).backnum = Math.min(adjList.get(v).backnum,adjList.get(e.index).backnum);
				}
				else if (!connectors.contains(adjList.get(v))) connectors.add(adjList.get(v));
			}
			else {
				//if we get here we have already visited this vertex
				adjList.get(v).backnum = Math.min(adjList.get(v).backnum, adjList.get(e.index).dfsnum);
			}
		}
	}
	
	/**
	 * Gets shortest path between two people on the graph
	 * @param source
	 * @param target
	 * @return string representation of shortest path, null if no such path exists
	 */
	public String getShortestPath(String source, String target) {
		Vertex vSource = adjList.get(nameToIndex(source));
		Vertex vTarget = adjList.get(nameToIndex(target));
		Queue<Vertex> q = new Queue<Vertex>();
		q.enqueue(vSource);
		
		HashMap<Vertex,Boolean> visited = new HashMap<Vertex,Boolean>();
		HashMap<Vertex, Vertex> parents = new HashMap<Vertex, Vertex>();
		visited.put(vSource, true);
		
		Vertex curr = null;
		while (!q.isEmpty()) {
			curr = q.dequeue();
			
			if (curr.equals(vTarget)) {
				break;
			}
			else {
				Neighbor ptr = curr.adjList;
				while (ptr != null) {
					Vertex nbr = adjList.get(ptr.index);
					if (!visited.containsKey(nbr)) {
						q.enqueue(nbr);
						visited.put(nbr, true);
						parents.put(nbr, curr);
					}
					ptr = ptr.next;
				}
			}
		}
		if (!curr.equals(vTarget)) {
			return null;
		}
		Vertex ptr = vTarget;
		String result = ptr.name;
		while (parents.get(ptr) != null) {
			result = parents.get(ptr).name + "--" + result;
			ptr = parents.get(ptr);
		}
		return result;
		
	}
	
	
	/**
	 * Returns ArrayList of graphs, each one is a clique at the given school
	 */
	public ArrayList<Graph> getCliques(String schoolName) {
		return studentsAtSchool(schoolName).getCliques();
	}
	
	/**
	 * Returns ArrayList of cliques from graph, only called by above method
	 */
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
        			String neighborName = indexToName(ptr.index);
        			int cliqueIndex = clique.nameToIndex(neighborName);
        			person.addNeighbor(new Neighbor(cliqueIndex));
        			//System.out.println(neighborName + " " + cliqueIndex);
        			ptr = ptr.next;
        		}
        	}
        }
		return cliques;
	}
	
	/**
	 * DFS used by clique method
	 */
	private void dfs(int v, boolean[] visited, Graph clique) { //used for getting cliques
		visited[v] = true;
		clique.addVertex(adjList.get(v).name, adjList.get(v).school, null);
		for (Neighbor e=adjList.get(v).adjList; e != null; e=e.next) {
			if (!visited[e.index]) {
				//System.out.println(adjList.get(v).name + "--" + adjList.get(e.vertexNum).name);
				dfs(e.index, visited, clique);
			}
		}
	}
	
	/**
	 * Prints graph in the format required by the assignment
	 */
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
				if (j < ptr.index) {
					System.out.println(adjList.get(j).name + "|" + indexToName(ptr.index));
				}
				ptr = ptr.next;
			}
		}
	}

}
