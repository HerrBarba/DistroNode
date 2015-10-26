package main

import solver.MasterSolver
import ui.MainWindow
import utils.Clock
import utils.FileUtils
import utils.XmlUtils
import connection.*

class Main {
	static void main(def args) {
		init()
		new MainWindow().startUI()
	}
	
	private static void init() {
		// Read initial config
		File initialConfig = FileUtils.fromPath("." + File.separator + "config.xml")
		String message = FileUtils.getContentFromFile(initialConfig)
		
		// Validate and parse initial config
		//XmlUtils.validateMessage(message)
		XmlUtils.parseMessage(message)
		
		// Set initial config
		NodeConfig.instance.id = XmlUtils.getNodeId()
		NodeConfig.instance.techCap = new Random().nextInt(50) + 1
		NodeConfig.instance.x = XmlUtils.getNodePosX()
		NodeConfig.instance.y = XmlUtils.getNodePosY()
		Clock.instance.startClock(XmlUtils.getTimestamp())
		
		// Start servers
		Server.instance.enableTCP()
		UdpServer.instance.enableUDP()
		
		// Send initial multicast message
		//new Client().multicast(MessageType.CONFIG_MULTICAST)
		
		// Start problem solvers
		MasterSolver.instance.startSolving()
	} 
}
