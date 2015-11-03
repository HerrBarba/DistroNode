package organization

public enum Status {
	UNDECIDED('Undecided'),
	SEARCHING("Searching"),
	ORGANIZED("Organized"),
	DECIDED("Decided")
	
	private final name;
	
	private Status(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}