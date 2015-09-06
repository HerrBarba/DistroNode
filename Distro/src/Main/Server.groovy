package Main

import java.text.SimpleDateFormat
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

class Server {
    static File xsd = new File ("." + File.separator + "MainXSD.xsd");
    static void main(def args) {

        def server = new ServerSocket(4444)
        while (true) {
            server.accept { Socket socket ->
                println "Waiting for connection..."
                socket.withStreams { InputStream input, OutputStream output ->
                    
                    def message = getMessageFromReader(input.newReader())
                    // Validate message
                    validateMessage(new StringReader(message))
                    
                    // Parse message
                    def rootNode = new XmlParser().parse(new StringReader(message))
                    String nodeId = getNodeId(rootNode)
                    String timestamp = getTimestamp(rootNode)
                    println "Received message from: Node-($nodeId), Timestamp-($timestamp)."
                    
                    // Send response
                    def now = Utils.formatDate2String(new Date())
                    output << "<Node><NodeId>is682778</NodeId><Timestamp>$now</Timestamp></Node>"
                }
                println "processing/thread complete."
            }
        }
    }
    
    static boolean validateMessage(Reader message) {    
        
        // Create schema from XSD file
        def factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        def schema = factory.newSchema(xsd)
        def validator = schema.newValidator()
        
        // Use schema to validate message
        def source = new StreamSource(message)
        validator.validate(source)
    }
    
    static String getNodeId(Node rootNode) {
        return rootNode.NodeId.text()
    }        
    
    static String getTimestamp(Node rootNode) {
        return rootNode.Timestamp.text()
    }
    
    static String getMessageFromReader(Reader reader) {
        return reader.readLines().iterator().join("\n");
    }
}