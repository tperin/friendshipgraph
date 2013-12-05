package app;

import java.io.FileNotFoundException;

import structures.FriendshipGraph;

public class Friends {
	public static void main(String[] args) throws FileNotFoundException {
		FriendshipGraph fg = new FriendshipGraph("data.txt");
		fg.print();
	}
}
