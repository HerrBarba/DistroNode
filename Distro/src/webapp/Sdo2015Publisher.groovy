package webapp

import javax.xml.ws.Endpoint

import main.GlobalConfig
import main.NodeConfig

public class Sdo2015Publisher {
	private Sdo2015Publisher() { } //Prevent instantiation

	public static void startWebService() {
		Thread.start {
			String ip = NodeConfig.instance.ip
			int port = GlobalConfig.instance.PORT
			println "Starting web service at http://${ip}:${port}/sdo2015"
			Endpoint.publish("http://${ip}:${port}/sdo2015", new Sdo2015())
		}
	}
}