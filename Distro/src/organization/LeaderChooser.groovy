package organization

import main.GlobalConfig
import main.NodeConfig
import connection.MessageType
import connection.ResponseType
import connection.UdpClient


class LeaderChooser {
	private LeaderChooser() {} // prevent instantiation
	
	public static void pickLeader() {
		Thread.start() {
			NodeConfig.instance.leader = ""
			NodeConfig.instance.state.sort{	GlobalConfig.instance.nodes.get(it).get(1)
			}.reverse().find { nodeId ->
				println GlobalConfig.instance.nodes.get(nodeId).get(1)		
				String ip = GlobalConfig.instance.nodes.get(nodeId).get(0)
				
				if (ip.equals(NodeConfig.instance.ip)) {
					UdpClient.multicast(ResponseType.LEADER_RESPONSE, "L1")
					NodeConfig.instance.leader = NodeConfig.instance.id
				}
				
				UdpClient.unicast(ip, 4444, MessageType.LEADER_REQUEST, "L1", false)
				Thread.sleep(GlobalConfig.instance.TIMEOUT)
				return !NodeConfig.instance.leader.equals("")
			}
		}
	}
}
