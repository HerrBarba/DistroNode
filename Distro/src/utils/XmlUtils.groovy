package utils

import java.io.File;

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

class XmlUtils {

	private XmlUtils() {} // Prevent instantiation
	
	static final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
	
	static Schema schema
	static Validator validator
    static Node rootNode
	
	static {
		File xsd = new File ("." + File.separator + "MainXSD.xsd")
		schema = factory.newSchema(xsd)
		validator = schema.newValidator()
	}
	
	static void setSchema(File xsd) {
		schema = factory.newSchema(xsd)
		validator = schema.newValidator()
	}
	
	static boolean validateMessage(String message) {
		def reader = new StringReader(message)
		def source = new StreamSource(reader)
		validator.validate(source)
	}
	
	static void parseMessage(String message) {
		rootNode = new XmlParser().parse(new StringReader(message))
	}

	static String getNodeId() {
		return node2String(rootNode.configuracion.expediente)
	}

	static String getTimestamp() {
		return node2String(rootNode.configuracion.tiempo)
	}
	
	// Sender info
	static String getSenderId() {
		return node2String(rootNode.emisor.expediente)
	}
	
	static String getSenderTimestamp() {
		return node2String(rootNode.emisor.tiempo)
	}
	
	static String getSenderIp() {
		return node2String(rootNode.emisor.direccion.ip)
	}	
	
	static String getSenderPort() {
		return node2String(rootNode.emisor.direccion.puerto)
	}
	
	// Response info
	
	static String getResponseId() {
		return rootNode.respuesta.getAt(0).attribute('id')
	}
	
	static String getResponseTime() {
		return node2String(rootNode.respuesta.tiempo)
	}
	
	static String getRequestId() {
		return rootNode.solicitud.getAt(0).attribute('id')
	}
	
	static String node2String(NodeList node) {
		return (node == null) ? "" : node.text()
	}
}
