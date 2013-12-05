package structures;

public class Friend {
	String name;
	String school;
	Friend next;
	int index;
	
	public Friend (String name, String school, Friend next) {
		this.name = name;
		this.school = school;
		this.next = next;
	}
	
	public Friend (int index, Friend next) {
		this.index = index;
		this.next = next;
	}
}
