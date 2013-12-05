package structures;

public class Person {
	String name;
	String school;
	Friend friends;
	
	public Person(String name, String school, Friend friends) {
		this.name = name;
		this.school = school;
		this.friends = friends;
	}
}
