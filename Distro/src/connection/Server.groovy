package connection

import main.GlobalConfig
import main.NodeConfig
import utils.*

@Singleton
class Server {
	
	ServerSocket server = new ServerSocket(4444)
	boolean isEnabled
	
    public void enableTCP() {
		isEnabled = true
		Thread.start {
	        while (isEnabled) {
	            server.accept { Socket socket ->
	                println "Starting connection..."
					String message, response
					MessageType type
					
	                socket.withStreams { InputStream input, OutputStream output ->
						
						// Read message
						message = FileUtils.getContentFromReader(input.newReader())
						
	                    // Send response
						response = processMessage(message)
						output << response
	                }

	                println "Request complete."
				    Logger.instance.writeLog("Server " + type, message, response)
	            }
	        }
		}
    }
	
	public void disableTCP() {
		isEnabled = false
	}
	
	private String processMessage(String message) {
		XmlUtils.parseMessage(message)
		ResponseType type = XmlUtils.getResponseType()
		
		switch(type) {
			case ResponseType.CONFIG_RESPONSE:
				String id = XmlUtils.getSenderId()
			    int x = XmlUtils.getNodePosX()
			    int y = XmlUtils.getNodePosY()
				String ip = XmlUtils.getSenderIp()
				int techCap = XmlUtils.getSenderTechCap()
				
				GlobalConfig.instance.positions[x][y] = id
				GlobalConfig.instance.nodes.put(id, [ip, techCap, x, y])
				break
				
			case ResponseType.STATE_RESPONSE:
				NodeConfig.instance.state.add(XmlUtils.getSenderId())
				break
				
			case ResponseType.LEADER_RESPONSE:
				NodeConfig.instance.leader = XmlUtils.getSenderId()
				break
		}
		
		return XmlUtils.createResponse(type)
	}
}