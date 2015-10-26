package utils

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

import connection.MessageType;
import connection.ResponseType

import main.NodeConfig

class XmlUtils {

	private XmlUtils() {} // Prevent instantiation
	
	static final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
	static final String REQUEST = "solicitud"
	static final String RESPONSE = "respuesta"
	
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

	// Node config
	static String getNodeId() {
		return node2String(rootNode.configuracion.expediente)
	}
	
	static int getNodePosX() {
		return Integer.parseInt(rootNode.configuracion.posicion.getAt(0).attribute('x'))
	}
	
	static int getNodePosY() {
		return Integer.parseInt(rootNode.configuracion.posicion.getAt(0).attribute('y'))
	}	
	
	//
	static String getTimestamp() {
		return node2String(rootNode.configuracion.tiempo)
	}
	
	// Sender info
	static String getSenderId() {
		return node2String(rootNode.emisor.id)
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
	
	static int getSenderTechCap() {
		return Integer.parseInt(node2String(rootNode.emisor.capacidad))
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
	
	static String createSolverMsgXml(String id, int[] solution) {
		def sw = new StringWriter()
		def xml = new groovy.xml.MarkupBuilder(sw)
		xml.sdo2015 {
			respuesta(id:"$id") {				
				ochoreinas {
					posicion(x:"0", y:"${solution[0]}")
					posicion(x:"1", y:"${solution[1]}")
					posicion(x:"2", y:"${solution[2]}")
					posicion(x:"3", y:"${solution[3]}")
					posicion(x:"4", y:"${solution[4]}")
					posicion(x:"5", y:"${solution[5]}")
					posicion(x:"6", y:"${solution[6]}")
					posicion(x:"7", y:"${solution[7]}")
				}
			}
		}

		return sw.toString()
	}
	
	static String createServerResponseXml(String ipAddr, String port) {
		def sw = new StringWriter()
		def xml = new groovy.xml.MarkupBuilder(sw)
		xml.sdo2015 {
			emisor {
				expediente NodeConfig.instance.id
				tiempo Clock.instance.time
				direccion {
					ip ipAddr
					puerto port
				}
				tipoDeMensaje "respuesta"
			}
			respuesta(id:resId) {
				tiempo Clock.instance.time
			}
		}

		return sw.toString()
	}
	
	static String createNodeResponse(String resId) {
		def sw = new StringWriter()
		def xml = new groovy.xml.MarkupBuilder(sw)
		xml.sdo2015 {
			emisor {
				id NodeConfig.instance.id
				tiempo Clock.instance.time
				direccion {
					ip NodeConfig.instance.ip
					puerto NodeConfig.instance.port
				}
				tipoDeMensaje RESPONSE
			}
			respuesta(id:resId) {}
		}

		return sw.toString()
	}
	
	static String createTimeResponse(String resId) {
		def sw = new StringWriter()
		def xml = new groovy.xml.MarkupBuilder(sw)
		xml.sdo2015 {
			emisor {
				id NodeConfig.instance.id
				tiempo Clock.instance.time
				direccion {
					ip NodeConfig.instance.ip
					puerto NodeConfig.instance.port
				}
				tipoDeMensaje RESPONSE
			}
			respuesta(id:resId) {
				tiempo Clock.instance.time
			}
		}

		return sw.toString()
	}
	
	private static String createNodeRequest(String reqId) {
		def sw = new StringWriter()
		def xml = new groovy.xml.MarkupBuilder(sw)
		xml.sdo2015 {
			emisor {
				id NodeConfig.instance.id
				tiempo Clock.instance.time
				direccion {
					ip NodeConfig.instance.ip
					puerto NodeConfig.instance.port
				}
				tipoDeMensaje REQUEST
			}
			solicitud(id:reqId) {}
		}

		return sw.toString()
	}
	
	private static String createTimeRequest(String reqId) {
		def sw = new StringWriter()
		def xml = new groovy.xml.MarkupBuilder(sw)
		xml.sdo2015 {
			emisor {
				id NodeConfig.instance.id
				tiempo Clock.instance.time
				direccion {
					ip NodeConfig.instance.ip
					puerto NodeConfig.instance.port
				}
				tipoDeMensaje REQUEST
			}
			solicitud(id:reqId) {
				tiempo()
			}
		}

		return sw.toString()
	}
	
	private static String createMulticastRequest(String reqId) {
		def sw = new StringWriter()
		def xml = new groovy.xml.MarkupBuilder(sw)
		xml.sdo2015 {
			emisor {
				id NodeConfig.instance.id
				tiempo Clock.instance.time
				direccion {
					ip NodeConfig.instance.ip
					puerto NodeConfig.instance.port
				}
				tipoDeMensaje REQUEST
			}
			solicitud(id:reqId) {}
		}

		return sw.toString()
	}
	
	public static String createMessage(MessageType type, String reqId) {
		switch(type) {
			case MessageType.NODE_REQUEST:
				return createNodeRequest(reqId)
			case MessageType.TIME_REQUEST:
				return createTimeRequest(reqId)
			case MessageType.CONFIG_MULTICAST:
				return createMulticastRequest(reqId)
			default:
				return ""
		}
	}
	
	public static String createResponse(message) {
		parseMessage(message)		
		createResponse(getResponseType())
	}
	
	public static String createResponse(ResponseType type) {		
		switch(type) {
			case ResponseType.NODE_RESPONSE:
				return createNodeResponse(getRequestId())
			case ResponseType.TIME_RESPONSE:
				return createTimeResponse(getRequestId())
			case ResponseType.CONFIG_RESPONSE:
			default:
				return ""
		}
	}
	
	private static ResponseType getResponseType() {
		if (!rootNode.solicitud.tiempo.isEmpty()) {
			return ResponseType.TIME_RESPONSE
		}

		if (getSenderIp() == "230.0.0.1") {
			return ResponseType.CONFIG_RESPONSE
		}

		return ResponseType.NODE_RESPONSE
	}
}
