package main

import groovy.beans.Bindable

import organization.Status

@Singleton
@Bindable
class NodeConfig {	
	final String ip = java.net.InetAddress.getLocalHost().getHostAddress()
	final int port = 4444
	
	String id // Node identifier
	short techCap // Node technological capacity	
	int x, y // "Map" position
	int water = 0 // Water counter
	int metal = 0 // Metal counter
	int food = 0 // Food counter
	
	ArrayList<String> state = Arrays.asList('IS682778', 'is682049')
	String leader
	String status = Status.UNDECIDED
}
