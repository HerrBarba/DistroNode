package connection

public enum ResponseType {
	NODE_RESPONSE("Node Response"),
	TIME_RESPONSE("Time Response"),
	CONFIG_RESPONSE("Config Response"),
	JOIN_RESPONSE("Join Response"),
	CONSOLIDATE_RESPONSE("Consolidate Response"),
	LEADER_RESPONSE("Leader Response"),
	INVALID("Invalid Response")
	
	private final name;
	
	private ResponseType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}