package organization

import main.GlobalConfig
import main.NodeConfig
import connection.Client
import connection.MessageType

class StateChooser {
	private StateChooser(){} // prevent instantiation
	
	public static void findState() {
		Thread.start() {
			while (NodeConfig.instance.state.size() < 3) {
				for (int d = 1; d < 25; d++) {
					findStateInDistance(d)			
					if (NodeConfig.instance.state.size() == 5) return
				}
			}
		}
	}
	
	private static void findStateInDistance(int d) {
		int x = NodeConfig.instance.x
		int y = NodeConfig.instance.y
		
		for (int dx = -d; dx <= d; dx += d) {
			if (x + dx < 1 || x + dx > 25) continue
			
			for (int dy = -d; dy <= d; dy += d) {
				if (y + dy < 1 || y + dy > 25) continue
				
				String id = GlobalConfig.instance.positions[x + dx][y + dy]
				if (!id.equals("N/A")) {
					if (NodeConfig.instance.state.size() == 5) return
					
					String ip = GlobalConfig.instance.nodes.get(id).ip
					Client.unicast(ip, MessageType.STATE_REQUEST, "A1")
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
