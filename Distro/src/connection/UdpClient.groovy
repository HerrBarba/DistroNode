package connection

import main.GlobalConfig
import main.NodeConfig
import utils.*

class UdpClient {
	
	private static void connect(String ip, int port, MessageType type, String message, boolean waitForResponse = true) {
		byte[] sendData = new byte[1024]
		byte[] receiveData = new byte[1024]
		
		// Initialize Socket
		DatagramSocket clientSocket = new DatagramSocket()
		clientSocket.setSoTimeout(GlobalConfig.instance.TIMEOUT)
		InetAddress IPAddress = InetAddress.getByName(ip)
		
		// Send message
		sendData = message.getBytes()
		long start = System.nanoTime()
		
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port)
		clientSocket.send(sendPacket)
		
		if (ip.equals(GlobalConfig.MULTICAST_IP)) return // Multicast does not wait for response
		
		// Wait for response
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length)
		try {
			clientSocket.receive(receivePacket)
		} catch (SocketTimeoutException e) { 
			//e.printStackTrace()
			return
		}
		
		String response = new String(receivePacket.getData()).trim()		
		processResponse(response, start)
			
		clientSocket.close()
		Logger.instance.writeLog('UdpClient ' + type, message, response)
	}
		
	public static void unicast(String ip = 'localhost', int port = GlobalConfig.PORT, MessageType type, 
		  String reqId, boolean waitForResponse = true) {
		String message = XmlUtils.createMessage(type, reqId)
		connect(ip, port, type, message, waitForResponse)
	}
		
	public static void multicast(MessageType type, String reqId) {
		String message = XmlUtils.createMessage(type, reqId)
		connect(GlobalConfig.MULTICAST_IP, GlobalConfig.PORT, type, message, false)
	}
	
	public static void multicast(ResponseType type, String reqId) {
		String response = XmlUtils.createResponse(type, reqId)
		connect(GlobalConfig.MULTICAST_IP, GlobalConfig.PORT, type, response, false)
	}

	private static void processResponse(String response, long start) {
		//XmlUtils.validateMessage()
		try {
			XmlUtils.parseResponse(response)
		} catch (Exception e) {
			e.printStackTrace()
			return
		}
		ResponseType type = XmlUtils.getResponseType()
		
		switch(type) {
			case ResponseType.TIME_RESPONSE:
				long tRound = System.nanoTime() - start
				long tMin = 0
				long tExtra = (tRound - tMin) / 2
				
				Clock.instance.startClock XmlUtils.getSenderTimestamp()
				Clock.instance.add tExtra
				break
				
			case ResponseType.CONFIG_RESPONSE:
				String id = XmlUtils.getSenderId()
				int x = XmlUtils.getNodePosX()
				int y = XmlUtils.getNodePosY()
				String ip = XmlUtils.getSenderIp()
				int techCap = XmlUtils.getTechCap()
				
				GlobalConfig.instance.positions[x][y] = id
				GlobalConfig.instance.nodes.put(id, [ip, techCap, x, y])
				break
				
			case ResponseType.JOIN_RESPONSE:
				NodeConfig.instance.state.add(XmlUtils.getSenderId())
				break
				
			case ResponseType.CONSOLIDATE_RESPONSE:
				NodeConfig.instance.state.add(XmlUtils.getSenderId())
				break
				
			case ResponseType.LEADER_RESPONSE:
				NodeConfig.instance.leader = XmlUtils.getSenderId()
				break
		}
	}
}
