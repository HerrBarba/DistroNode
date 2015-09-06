package Main

import java.text.SimpleDateFormat
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

class Server {
    static void main(def args) {

        def server = new ServerSocket(4444)
        while (true) {
            server.accept { Socket socket ->
                println "Waiting for connection..."
                socket.withStreams { InputStream input, OutputStream output ->
                    
                    // Send message
                    def now = Utils.formatDate2String(new Date())
                    output << "<Node><NodeId>is682778</NodeId><Timestamp>$now</Timestamp></Node>"
                }
                println "Request complete."
            }
        }
    }
}