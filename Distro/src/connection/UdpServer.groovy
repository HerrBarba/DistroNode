package connection

import java.net.ServerSocket;

import groovy.lang.Singleton;
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
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length)  
				server.receive(receivePacket)
				
				String message = new String(receivePacket.getData()).trim()
				println "RECEIVED: " + message
				
				XmlUtils.parseMessage(message)
				String resId = XmlUtils.getRequestId()
				
				InetAddress ip = receivePacket.getAddress()
				int port = receivePacket.getPort()
				
				def now = Clock.instance.time
				String response = "<sdo2015><respuesta id=\"$resId\"><tiempo>$now</tiempo></respuesta></sdo2015>"
				sendData = response.getBytes()
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port)    
				server.send(sendPacket)
			}
		}     
	}
}
