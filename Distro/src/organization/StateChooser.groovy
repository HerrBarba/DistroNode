package organization

import javax.xml.namespace.QName;

import main.GlobalConfig
import main.NodeConfig
import webapp.ArrayOfRecords
import webapp.Sdo2015PortType
import webapp.Sdo2015Service
import connection.MessageType
import connection.UdpClient

class StateChooser {
	private StateChooser(){} // prevent instantiation
	
	public static void findState() {
		while (true) {
			ArrayList<String> found = new ArrayList(3)
			ArrayList<String> united = new ArrayList(3)
			ArrayList<String> state = new ArrayList(3)

			// Search for possible nodes to conform a new state
			if (NodeConfig.instance.status.equals(Status.UNDECIDED)) {
				for (int d = 1; d < 25; d++) {
					findStateInDistance(d, found)
					
					if (found.size() >= 5) {						
						NodeConfig.instance.status = Status.SEARCHING
						break
					}
				}
			}				
			
			// Ask nodes to join state 
			if (NodeConfig.instance.status.equals(Status.SEARCHING)) {
				for (node in found) {
					Sdo2015PortType sdo2015 = createSdo2015Call(node)
					ArrayOfRecords joinedNodes = createArrayOfRecords(united)
					
					if (sdo2015.wannaJoin(joinedNodes)) {
						united.add(node)
					}						
				}
				
				// Not enough nodes want to join, restart
				if (united.size() < 3) {
					NodeConfig.instance.status = Status.UNDECIDED
					continue
				}

				NodeConfig.instance.status = Status.ORGANIZED
			}

			// Consolidate a new state	
			if (NodeConfig.instance.status.equals(Status.ORGANIZED)) {
				NodeConfig.instance.status = Status.DECIDED
				for (node in united) {
					Sdo2015PortType sdo2015 = createSdo2015Call(node)
					ArrayOfRecords newState = createArrayOfRecords(state)
					
					if (sdo2015.consolidate(newState)) {
						state.add(node)
					}

					if (state.size() == 2) {				
						state.add(NodeConfig.instance.id)
					}

					if (state.size() == 5) {
						break
					}
				}

				// Confirm State
				if (united.size() >= 3) {
					for (node in state) {
						Sdo2015PortType sdo2015 = createSdo2015Call(node)
						ArrayOfRecords finalState = createArrayOfRecords(state)							
						sdo2015.confirmState(finalState)
					}
					break
				}

				// Not enough nodes in state, restart
				if (united.size() < 3) {
					NodeConfig.instance.status = Status.UNDECIDED
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
	
	private static ArrayOfRecords createArrayOfRecords(List records) {
		ArrayOfRecords arrayOfRecords = new ArrayOfRecords()
		records.each { arrayOfRecords.getRecord().add(it) }
		return arrayOfRecords
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
