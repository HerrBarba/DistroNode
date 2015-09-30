package connection

import utils.FileUtils
import utils.XmlUtils

class Client {
	
	String ip = "localhost"
	int port = 4444
	
    public void connect() {
        def client = new Socket(ip, port)

		client.withStreams { InputStream input, OutputStream output ->
			// Read message
			def message = FileUtils.getContentFromReader(input.newReader())
			
			// Validate message
			//XmlUtils.validateMessage(message)
			
			// Parse message
			XmlUtils.parseMessage(message)
			String nodeId = XmlUtils.getSenderId()
			String timestamp = XmlUtils.getSenderTimestamp()
			
			// Confirm reception
			println "Received message from: Node-($nodeId), Timestamp-($timestamp)."
		}
    }
}