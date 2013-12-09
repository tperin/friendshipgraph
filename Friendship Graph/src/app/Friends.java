package app;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import structures.Graph;

public class Friends {
	public static void main(String[] args) throws FileNotFoundException {
		Graph fg = new Graph("data.txt");
		fg.dfsTopsort();
		//dfs.print();
		
	}
}
