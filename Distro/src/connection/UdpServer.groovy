package connection

import main.GlobalConfig
import main.NodeConfig
import utils.Logger
import utils.XmlUtils



class UdpServer {
	
	private static MulticastSocket socket
	private static boolean isEnabled
	private static InetAddress ip
	static {
		socket = new MulticastSocket(GlobalConfig.PORT)		
		//socket.setSoTimeout(GlobalConfig.instance.TIMEOUT)
	}
	
	private static void startListening() {
		Thread.start {			
			while (isEnabled) {
				println "Waiting for connection..."
				
				// Read message
				byte[] receiveData = new byte[1024]
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length)				
				// try {
					socket.receive(receivePacket)
				//} catch (SocketTimeoutException e) { 
					//e.printStackTrace()
					//continue
				//}
				
				// Process message
				String message = new String(receivePacket.getData()).trim()
				println message
				//XmlUtils.validateMessage(message)
				try {
					XmlUtils.parseMessage(message)
				} catch (Exception e) {
					e.printStackTrace()
					continue
				}
				
				
				if (XmlUtils.isRequest()) {					
					ip = receivePacket.getAddress()
					int port = Integer.parseInt(XmlUtils.getSenderPort())
					
					MessageType type = processMessage(message)
					
					// Send response
					String response = XmlUtils.createResponse(message)
					byte[] sendData = response.getBytes()
					//InetAddress ip = InetAddress.getByName(XmlUtils.getSenderIp()
					
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port)
					socket.send(sendPacket)
	
					println "Request complete."
					Logger.instance.writeLog('Udp Server ' + type, message, response)
				} else {
					ResponseType type = processResponse(message)
					Logger.instance.writeLog('Udp Server ' + type, message, '')
				}
			}
		}	
	}
	
	public static void disable() {
		isEnabled = false
		socket.leaveGroup(InetAddress.getByName(GlobalConfig.MULTICAST_IP))
	}
	
	public static void enable() {
		isEnabled = true
		SocketAddress socketAddress = new InetSocketAddress(GlobalConfig.MULTICAST_IP, GlobalConfig.PORT)
		NetworkInterface networkInterface = NetworkInterface.getByName("wlan0")
		
		socket.joinGroup(socketAddress, networkInterface)
		//socket.joinGroup(InetAddress.getByName(GlobalConfig.MULTICAST_IP))
		startListening()
	}
	
	private static MessageType processMessage(String message) {
		MessageType type = XmlUtils.getMessageType()
		
		switch (type) {
			case MessageType.CONFIG_REQUEST:
				saveConfig()
				return MessageType.CONFIG_REQUEST
				
			case MessageType.JOIN_REQUEST:
				NodeConfig.instance.state.add(XmlUtils.getSenderId())
				return MessageType.JOIN_REQUEST
					
			case MessageType.CONSOLIDATE_REQUEST:
				NodeConfig.instance.state.add(XmlUtils.getSenderId())
				return MessageType.JOIN_REQUEST
				
			case MessageType.LEADER_REQUEST:
				return MessageType.LEADER_REQUEST
		}
	}
	
	private static ResponseType processResponse(String response) {
		ResponseType type = XmlUtils.getResponseType()
		
		switch (type) {
			case ResponseType.CONFIG_RESPONSE:
				saveConfig()
				return ResponseType.CONFIG_RESPONSE
				
			case ResponseType.LEADER_RESPONSE:
				NodeConfig.instance.leader = XmlUtils.getSenderId()
				println "HABEMUS PAPAM: ${XmlUtils.getSenderId()}"
				return ResponseType.LEADER_RESPONSE
		}
	}
	
	private static void saveConfig() {
		String id = XmlUtils.getSenderId()
		int x = XmlUtils.getNodePosX()
		int y = XmlUtils.getNodePosY()
		println "NO MMS LLEGO: " + x + ", " + y
		String ip = ip.getHostAddress()
		int techCap = XmlUtils.getTechCap()
		
		GlobalConfig.instance.positions[x][y] = id
		GlobalConfig.instance.nodes.put(id, [ip, techCap, x, y])
	}
}
