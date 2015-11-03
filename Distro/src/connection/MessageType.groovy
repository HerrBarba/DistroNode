package connection

public enum MessageType {
	NODE_REQUEST('Node Request'),
	TIME_REQUEST("Time Request"),
	CONFIG_REQUEST("Config Request"),
	JOIN_REQUEST("Join Request"),
	CONSOLIDATE_REQUEST("State Request"),
	LEADER_REQUEST("Leader Request"),
	INVALID("Invalid Request")	
	
	private final name;
	
	private MessageType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}