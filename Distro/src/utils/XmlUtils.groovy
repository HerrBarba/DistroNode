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
	
	static void parseMessage(String message) throws Exception {
		rootNode = new XmlParser().parse(new StringReader(message))
	}
	
	static void parseResponse(String response) throws Exception {
		parseMessage(response)
	}

	// Node config
	static String getNodeId() {
		return node2String(rootNode.configuracion.id)
	}
	
	static int getNodePosX() {
		return Integer.parseInt(rootNode.configuracion.posicion.getAt(0).attribute('x'))
	}
	
	static int getNodePosY() {
		return Integer.parseInt(rootNode.configuracion.posicion.getAt(0).attribute('y'))
	}
	
	static int getTechCap() {
		return Integer.parseInt(node2String(rootNode.configuracion.capacidad))
	}
	
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
	
	
	// Response/Request info
	static boolean isRequest() {
		return rootNode.solicitud.isEmpty() == false
	}
	
	static String getResponseId() {
		return rootNode.respuesta.getAt(0).attribute('id')
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
	
	// ------------- NODE INFO --------------
	public static String createNodeRequest(String reqId) {
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
			solicitud(id:reqId, tipo:'tcp') {}
		}

		return sw.toString()
	}
	
	public static String createNodeResponse(String resId) {
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
			respuesta(id:resId, tipo:'tcp') {}
		}

		return sw.toString()
	}
	
	// ------------- TIME --------------
	public static String createTimeRequest(String reqId) {
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
			solicitud(id:reqId, tipo:'tiempo') {}
		}

		return sw.toString()
	}
		
	public static String createTimeResponse(String resId) {
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
			respuesta(id:resId, tipo:'tiempo') {}
		}

		return sw.toString()
	}
	
	// ------------- CONFIG --------------
	public static String createConfigRequest(String reqId) {
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
			solicitud(id:reqId, tipo:'configuracion') {}
			configuracion() {
				id NodeConfig.instance.id
				tiempo Clock.instance.time
				posicion(x:NodeConfig.instance.x, y:NodeConfig.instance.y)
				capacidad NodeConfig.instance.techCap
			}
		}

		return sw.toString()
	}

	public static String createConfigResponse(String resId) {
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
			respuesta(id:resId, tipo:'configuracion') {}			
			configuracion() {
				id NodeConfig.instance.id
				tiempo Clock.instance.time
				posicion(x:NodeConfig.instance.x, y:NodeConfig.instance.y)
				capacidad NodeConfig.instance.techCap
			}
		}

		return sw.toString()
	}
	
	// ------------- STATE --------------
	public static String createJoinStateRequest(String reqId) {
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
			solicitud(id:reqId, tipo:'unir') {}
		}

		return sw.toString()
	}

	public static String createJoinStateResponse(String resId) {
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
			respuesta(id:resId, tipo:'unir') {}
		}

		return sw.toString()
	}
	
	public static String createConsolidateStateRequest(String reqId) {
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
			solicitud(id:reqId, tipo:'consolidar') {}
		}

		return sw.toString()
	}

	public static String createConsolidateStateResponse(String resId) {
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
			respuesta(id:resId, tipo:'consolidar') {}
		}

		return sw.toString()
	}
	
	// ------------- LEADER --------------
	public static String createLeaderRequest(String reqId) {
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
			solicitud(id:reqId, tipo:'lider') {}
		}

		return sw.toString()
	}

	public static String createLeaderResponse(String resId) {
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
			respuesta(id:resId, tipo:'lider') {}
		}

		return sw.toString()
	}
	
	// XML CONVENIENCE METHODS
	public static String createMessage(MessageType type, String reqId) {
		switch(type) {
			case MessageType.NODE_REQUEST:
				return createNodeRequest(reqId)
			case MessageType.TIME_REQUEST:
				return createTimeRequest(reqId)
			case MessageType.CONFIG_REQUEST:
				return createConfigRequest(reqId)
			case MessageType.JOIN_REQUEST:
				return createJoinStateRequest(reqId)
			case MessageType.CONSOLIDATE_REQUEST:
				return createConsolidateStateRequest(reqId)
			case MessageType.LEADER_REQUEST:
				return createLeaderRequest(reqId)
			default:
				return ""
		}
	}
	
	public static String createResponse(message) {
		try {
			parseMessage(message)
		} catch (Exception e) {
			e.printStackTrace()
			return ''
		}				
		createResponse(getResponseType(true))
	}
			
	public static String createResponse(ResponseType type, String resId = getRequestId()) {	
		switch(type) {
			case ResponseType.NODE_RESPONSE:
				return createNodeResponse(resId)
			case ResponseType.TIME_RESPONSE:
				return createTimeResponse(resId)
			case ResponseType.CONFIG_RESPONSE:
				return createConfigResponse(resId)
			case ResponseType.JOIN_RESPONSE:
				return createJoinStateResponse(resId)
			case ResponseType.CONSOLIDATE_RESPONSE:
				return createConsolidateStateResponse(resId)
			case ResponseType.LEADER_RESPONSE:
				return createLeaderResponse(resId)
			default:
				return ""
		}
	}
	
	public static MessageType getMessageType() {
		String type = rootNode.solicitud.getAt(0).attribute('tipo')
				
		switch(type) {
			// Node
			case 'tcp':
				return MessageType.NODE_REQUEST
				
			// Time
			case 'tiempo':
				return MessageType.TIME_REQUEST
			
			// Config
			case 'configuracion':
				return MessageType.CONFIG_REQUEST
				
			// Join State
			case 'unir':
				return MessageType.JOIN_REQUEST
				
			// Consolidate State
			case 'consolidar':
				return MessageType.CONSOLIDATE_REQUEST
			
			// Leader
			case 'lider':
				return MessageType.LEADER_REQUEST
				
			default:
				return MessageType.INVALID
		}
	}
	
	public static ResponseType getResponseType(boolean willCreate = false) {
		NodeList response = willCreate ? rootNode.solicitud : rootNode.respuesta
		String type = response.getAt(0).attribute('tipo')
		
		switch(type) {
			// Node
			case 'tcp':
				return ResponseType.NODE_RESPONSE
				
			// Time
			case 'tiempo':
			    return ResponseType.TIME_RESPONSE
			
			// Config
			case 'configuracion':
				return ResponseType.CONFIG_RESPONSE
				
			// Join State
			case 'unir':
				return ResponseType.JOIN_RESPONSE
				
			// Consolidate State
			case 'consolidar':
				return ResponseType.CONSOLIDATE_RESPONSE
			
			// Leader
			case 'lider':
				return ResponseType.LEADER_RESPONSE
				
			default:
				return ResponseType.INVALID
		}
	}
}
