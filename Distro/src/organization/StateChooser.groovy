package organization

import main.GlobalConfig
import main.NodeConfig
import connection.Client
import connection.MessageType
import connection.UdpClient

class StateChooser {
	private StateChooser(){} // prevent instantiation
	
	public static void findState() {
		Thread.start() {
			if (NodeConfig.instance.status.equals(Status.UNDECIDED)) {				
				ArrayList<String> found = new ArrayList(3)
				for (int d = 1; d < 25; d++) {
					findStateInDistance(d, found)
					
					if (found.size() >= 3) {						
						NodeConfig.instance.status = Status.SEARCHING
						break
					}
				}
				
				if (NodeConfig.instance.status.equals(Status.SEARCHING)) {
					ArrayList<String> united = new ArrayList(3)
					for (node in found) {						
						String ip = GlobalConfig.instance.nodes.get(node).ip
						UdpClient.unicast(ip, MessageType.JOIN_REQUEST, "S1")
						
						if (united.size() >= 3) {
							NodeConfig.instance.status = Status.ORGANIZED
							break
						}
					}
					
					if (NodeConfig.instance.status.equals(Status.ORGANIZED)) {
						ArrayList<String> state = new ArrayList(3)
						for (node in united) {
							String ip = GlobalConfig.instance.nodes.get(node).ip
							UdpClient.unicast(ip, MessageType.CONSOLIDATE_REQUEST, "S1")
							
							if (state.size() >= 3) {
								NodeConfig.instance.status = Status.DECIDED
								NodeConfig.instance.state = state
								break
							}
						}
						
						
					}
				}
			}
		}
	}
	
	private static void findStateInDistance(int d, List found) {
		int x = NodeConfig.instance.x
		int y = NodeConfig.instance.y
		
		for (int dx = -d; dx <= d; dx += d) {
			if (x + dx < 1 || x + dx > 25) continue
			
			for (int dy = -d; dy <= d; dy += d) {
				if (y + dy < 1 || y + dy > 25) continue
				
				String id = GlobalConfig.instance.positions[x + dx][y + dy]
				if (!id.equals("N/A")) {
					found.add(id)
				}
			}
		}
	}
	
	private int distance(int x, int y) {
		int sqrX = Math.pow((NodeConfig.instance.x - x), 2)
		int sqrY = Math.pow((NodeConfig.instance.y - y), 2)
		return Math.sqrt(sqrX + sqrY)
	}
}
