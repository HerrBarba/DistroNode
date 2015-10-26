package connection

import utils.*

class UdpClient {	
	
	private void connect(String ip, int port, String message) {		
		
		byte[] sendData = new byte[1024]
		byte[] receiveData = new byte[1024]
		
		// Initialize Socket
		DatagramSocket clientSocket = new DatagramSocket()
		InetAddress IPAddress = InetAddress.getByName(ip)
		
		// Send message
		sendData = message.getBytes()
		
		long start = System.nanoTime()
		
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port)
		clientSocket.send(sendPacket)
		
		// Wait for response
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length)
		clientSocket.receive(receivePacket)
		
		long tRound = System.nanoTime() - start
		long tMin = 0
		long tExtra = (tRound - tMin) / 2
		
		String response = new String(receivePacket.getData()).trim()
		
		// Update 
		//XmlUtils.validateMessage()
		XmlUtils.parseMessage(response)
		Clock.instance.startClock XmlUtils.getResponseTime()
		Clock.instance.add tExtra
		
		clientSocket.close()
		Logger.instance.writeLog('UdpClient Time Request', message, response)
	}
	
	public void unicast(String ip = 'localhost', int port = 4444, String reqId) {
		String message = XmlUtils.createMessage(MessageType.TIME_REQUEST, reqId)
		connect(ip, port, message)
	}
}
