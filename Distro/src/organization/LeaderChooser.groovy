package organization

import javax.xml.namespace.QName

import main.GlobalConfig
import main.NodeConfig
import webapp.Sdo2015PortType
import webapp.Sdo2015Service
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
				
				Sdo2015PortType sdo2015 = createSdo2015Call(nodeId)
				
				if (sdo2015.leader()) {
					NodeConfig.instance.leader = nodeId
				}
				
				return !NodeConfig.instance.leader.equals("")
			}
		}
	}	
	
	private static Sdo2015PortType createSdo2015Call(String node) {
		String ip = GlobalConfig.instance.nodes.get(node).ip
		int port = GlobalConfig.instance.PORT
		QName qName = new QName("http://${ip}:${port}/sdo2015", "Sdo2015Service")
		Sdo2015Service sdo2015Service = new Sdo2015Service(qName)
		Sdo2015PortType sdo2015 = sdo2015Service.getSdo2015Port()
		
		return sdo2015
	}
}
