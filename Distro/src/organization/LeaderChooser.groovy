package organization

import main.GlobalConfig
import main.NodeConfig
import connection.Client
import connection.MessageType


class LeaderChooser {
	private LeaderChooser() {} // prevent instantiation
	
	public static void pickLeader() {
		Thread.start() {
			boolean highest = true
			NodeConfig.instance.state.each { nodeId ->
				int techCap = GlobalConfig.instance.nodes.get(nodeId).techCap
				if (techCap > NodeConfig.instance.techCap) {
					highest = false					
					String ip = GlobalConfig.instance.nodes.get(id).ip
					Client.unicast(ip, MessageType.LEADER_REQUEST, "A1")
				}
			}
			
			if (highest) {
				Client.multicast(MessageType.LEADER_REQUEST)
				NodeConfig.instance.leader = NodeConfig.instance.id
			}
		}
	}
}
