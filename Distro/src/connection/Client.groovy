package connection

import utils.*

class Client {
	
	private String connect(String ip, int port, String message) {		
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
		Logger.instance.writeLog("TCP Client Node Request", message, response)
		return response		
	}
	
	public void unicast(String ip = 'localhost', int port = 4444, String reqId) {
		String message = XmlUtils.createMessage(MessageType.NODE_REQUEST, reqId)
		String response = connect(ip, port, message)
				
		// Parse response
		try {
			XmlUtils.parseResponse(response)
		} catch (Exception e) {
			e.printStackTrace()
			return
		}
		String nodeId = XmlUtils.getSenderId()
		String timestamp = XmlUtils.getSenderTimestamp()
		println "Received message from: Node-($nodeId), Timestamp-($timestamp)."
	}
}