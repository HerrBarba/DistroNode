package connection

import utils.*

class UdpClient {
	
	String ip = "localhost"
	int port = 4444
	
	public void connect() {		
		
		byte[] sendData = new byte[1024]
		byte[] receiveData = new byte[1024]
		
		DatagramSocket clientSocket = new DatagramSocket()
		InetAddress IPAddress = InetAddress.getByName(ip)
		
		def reqId = "A1"
		String message = "<sdo2015><solicitud id=\"$reqId\"><tiempo/></solicitud></sdo2015>"
		sendData = message.getBytes()
		
		long start = System.nanoTime()
		
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port)
		clientSocket.send(sendPacket)
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length)
		clientSocket.receive(receivePacket)
		
		long tRound = System.nanoTime() - start
		long tMin = 0
		long tExtra = (tRound - tMin) / 2
		
		String response = new String(receivePacket.getData()).trim()
		//XmlUtils.validateMessage()
		XmlUtils.parseMessage(response)
		Clock.instance.startClock(XmlUtils.getResponseTime())
		Clock.instance.add tExtra
		
		println "FROM SERVER:" + XmlUtils.getResponseTime()
		clientSocket.close()
	}
}
