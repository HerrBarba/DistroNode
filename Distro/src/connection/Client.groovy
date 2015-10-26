package connection

import utils.*

class Client {
	
	private String connect(String ip, int port, MessageType type, String message) {		
		def client = new Socket(ip, port)
			
		// Validate message
		//XmlUtils.validateMessage(message)
		
		String response
		
		client.withStreams { InputStream input, OutputStream output ->
			// Send request
			output << message
			
			// Read response
			response = FileUtils.getContentFromReader(input.newReader())
		}
		
		// Update log
		Logger.instance.writeLog("Client " + type, message, response)
		return response		
	}
	
	public void unicast(String ip = 'localhost', int port = 4444, MessageType type, String reqId) {
		String message = XmlUtils.createMessage(type, reqId)
		String response = connect(ip, port, type, message)
		
		
		if (type == MessageType.NODE_REQUEST) {
			// Parse response
			XmlUtils.parseMessage(response)
			String nodeId = XmlUtils.getSenderId()
			String timestamp = XmlUtils.getSenderTimestamp()
			println "Received message from: Node-($nodeId), Timestamp-($timestamp)."
		}
	}
	
    public void multicast(MessageType type) {
		String message = XmlUtils.createMessage(type , "INIT")
		String response = connect("230.0.0.1", 4444, type, message)
    }
}