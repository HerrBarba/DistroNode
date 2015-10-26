package connection

import utils.*

@Singleton
class UdpServer {	
	
	DatagramSocket server = new DatagramSocket(4444)
	boolean isEnabled
	
	public void enableUDP() {
		isEnabled = true
		byte[] receiveData = new byte[1024]
		byte[] sendData = new byte[1024]
		
		Thread.start {
			while(isEnabled) {
	            println "Waiting for connection..."
				
				// Read message
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length)  
				server.receive(receivePacket)
				
				String message = new String(receivePacket.getData()).trim()

				// Send response
				String response = XmlUtils.createResponse(message)
				sendData = response.getBytes()
				InetAddress ip = receivePacket.getAddress()
				int port = receivePacket.getPort()
				
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port)
				server.send(sendPacket)

				println "Request complete."
				Logger.instance.writeLog('UdpServer Time Request', message, response)
			}
		}     
	}
}
