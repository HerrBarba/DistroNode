package main

import groovy.beans.Bindable

@Singleton
@Bindable
class GlobalConfig {	
	static final String MULTICAST_IP = '230.0.0.1'
	static final int PORT = 4444
	static final int TIMEOUT = 2000
	String[][] positions = initPositions()
	HashMap<String/*nodeId*/, Node> nodes = new HashMap<>()
	
	private String[][] initPositions() {		
		String[][] positions = new String[26][26]
		positions[0][0] = ""
		(1..<26).each { x ->
			(1..<26).each { y ->
				positions[x][y] = "N/A"
			}
		}
		
		(1..<26).each { x ->
			positions[x][0] = x
		}
		
		(1..<26).each { y ->
			positions[0][y] = y
		}
		
		return positions
	}
}

class Node {
	String ip
	short techCap
	int x, y
}