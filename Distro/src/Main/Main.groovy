package main

import connection.*
import ui.MainWindow
import utils.Clock
import utils.FileUtils
import utils.XmlUtils

class Main {
	static void main(def args) {
		// Read initial config
		File initialConfig = FileUtils.fromPath("." + File.separator + "config.xml")
		String message = FileUtils.getContentFromFile(initialConfig)
		
		// Validate and parse initial config
		//XmlUtils.validateMessage(message)
		XmlUtils.parseMessage(message)
		
		// Set initial config
		NodeConfig.instance.id = XmlUtils.getNodeId()
		Clock.instance.startClock(XmlUtils.getTimestamp())
		Server.instance.enableTCP()
		UdpServer.instance.enableUDP()
		new MainWindow().startUI()
	}
}
