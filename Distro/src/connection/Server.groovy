package connection

import java.text.SimpleDateFormat
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import utils.Clock;
import utils.DateUtils;

@Singleton
class Server {
	
	ServerSocket server = new ServerSocket(4444)
	boolean isEnabled
	
    public void enableTCP() {
		isEnabled = true
		Thread.start {
	        while (isEnabled) {
	            server.accept { Socket socket ->
	                println "Waiting for connection..."
	                socket.withStreams { InputStream input, OutputStream output ->
	                    
	                    // Send message
	                    def now = Clock.instance.time
	                    output << "<?xml version=\"1.0\" encoding=\"UTF-8\"?><sdo2015><emisor><expediente>is682778</expediente><tiempo>$now</tiempo><direccion><ip>127.0.0.1</ip><puerto>4444</puerto></direccion><tipoDeMensaje>respuesta</tipoDeMensaje></emisor></sdo2015>"
	                }
	                println "Request complete."
	            }
	        }
		}
    }
	
	public void disableTCP() {
		isEnabled = false
	}
}