package main

import organization.LeaderChooser
import organization.StateChooser

import solver.MasterSolver
import ui.MainWindow
import utils.Clock
import utils.FileUtils
import utils.XmlUtils
import webapp.Sdo2015Publisher
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
		try {
			XmlUtils.parseMessage(message)
		} catch (Exception e) {
			e.printStackTrace()
			return
		}
		
		// Set initial config
		int x = XmlUtils.getNodePosX()
		int y = XmlUtils.getNodePosY()
		String id = XmlUtils.getNodeId()
		NodeConfig.instance.id = id
		NodeConfig.instance.techCap = new Random().nextInt(50) + 1
		NodeConfig.instance.x = x
		NodeConfig.instance.y = y
		Clock.instance.startClock(XmlUtils.getTimestamp())
		
		// Start servers
		Server.instance.enableTCP()
		UdpServer.enable()
		
		// Send initial multicast message
		UdpClient.multicast(MessageType.CONFIG_REQUEST, "MC-1")

		// Start problem solvers
		MasterSolver.instance.startSolving()
		Sdo2015Publisher.startWebService()
		
		Thread.start {
			StateChooser.findState()
			LeaderChooser.pickLeader()
		}
	} 
}
