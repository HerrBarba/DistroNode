package Main

import java.io.File;

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

class Client {
	
	static File xsd = new File ("." + File.separator + "MainXSD.xsd");
	
    static void main(def args) {
        def client = new Socket("localhost", 4444)

		client.withStreams { InputStream input, OutputStream output ->
			// Read message
			def message = getMessageFromReader(input.newReader())
			
			// Validate message
			validateMessage(new StringReader(message))
			
			// Parse message
			def rootNode = new XmlParser().parse(new StringReader(message))
			String nodeId = getNodeId(rootNode)
			String timestamp = getTimestamp(rootNode)
			
			// Confirm reception
			println "Received message from: Node-($nodeId), Timestamp-($timestamp)."
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