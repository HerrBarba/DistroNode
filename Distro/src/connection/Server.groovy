package connection

import com.sun.org.apache.xml.internal.security.utils.XMLUtils;

import utils.*

@Singleton
class Server {
	
	ServerSocket server = new ServerSocket(4444)
	
    public void enableTCP() {
		Thread.start {
	        while (true) {
	            server.accept { Socket socket ->
	                println "Starting connection..."
					String message, response
					
	                socket.withStreams { InputStream input, OutputStream output ->
						
						// Read message
						message = FileUtils.getContentFromReader(input.newReader())
						response = XmlUtils.createResponse(message)
						
	                    // Send response
						output << response
	                }

	                println "Request complete."
				    Logger.instance.writeLog("TCP Server Node Request ", message, response)
	            }
	        }
		}
    }
}