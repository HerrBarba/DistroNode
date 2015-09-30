package ui

import groovy.swing.SwingBuilder
import groovy.beans.Bindable
import static javax.swing.JFrame.EXIT_ON_CLOSE
import java.awt.*
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JTabbedPane

import connection.*
import utils.*
import main.*
import solver.*

@Bindable
class NodeReport {
	String resources, problems, communication, organization, status
	String toString() { "address[resources=$resources, problems=$problems, organization=$organization, communication=$communication, status=$status]" }
}

@Bindable
class NodeTime {
	String time
}

class MainWindow {
	def networkEnabled = true, resolutionEnabled = true
	
	def report = new NodeReport(
		resources: 'Nothing', 
		problems: '0', 
		communication: 'No connections made',
		organization: 'None',
		status: 'Fine'
		)
	
	def time = new NodeTime(time: Clock.instance.getTime())
	
	def swingBuilder = new SwingBuilder()
	
	def openXmlConfig = new JFileChooser(
		dialogTitle: "Choose an XML file",
		fileSelectionMode: JFileChooser.FILES_ONLY)		
	
	public void startUI() {
		swingBuilder.edt {  // edt method makes sure UI is build on Event Dispatch Thread.
			lookAndFeel 'nimbus'
			frame(title: 'Address', size: [600, 700],
			show: true, locationRelativeTo: null,
			defaultCloseOperation: EXIT_ON_CLOSE) {
				borderLayout(vgap: 5)
				
				menuBar {
					menu ('File', mnemonic: 'F') {
						menuItem 'OpenConfig', mnemonic:'O', actionPerformed: {
								int result = openXmlConfig.showOpenDialog()
								if (result == JFileChooser.APPROVE_OPTION) {									
									proccessFile(openXmlConfig.getSelectedFile())
								}
							}
						menuItem 'Close'
					}
					menu('Connect', mnemonic: 'C') {
						menuItem 'New'
						menuItem 'Open'
					}
					menu('About', mnemonic: 'A') {
						menuItem 'New'
						menuItem 'Open'
					}
				}
				
				tabbedPane(id: 'tabs', tabLayoutPolicy:JTabbedPane.SCROLL_TAB_LAYOUT) {
					panel(name: 'Reporting', constraints: BorderLayout.CENTER,
							border: compoundBorder([emptyBorder(10)])) {
						tableLayout {						
							tr {
								td {
									label 'Resources:'
								}
								td {
									textArea report.resources, id: 'resources', columns: 30, rows: 4
								}
							}
							tr {
								td {
									label 'Problems:'
								}
								td {
									textArea report.problems, id: 'problems', columns: 30, rows: 4
								}
							}
							tr {
								td {
									label 'Communication:'
								}
								td {
									textArea report.communication, id: 'communication', columns: 30, rows: 4
								}
							}
							tr {
								td {
									label 'Organization:'
								}
								td {
									textArea report.organization, id: 'organization', columns: 30, rows: 4
								}
							}
							tr {
								td {
									label 'General Status:'
								}
								td {
									textArea report.status, id: 'status', columns: 30, rows: 4
								}
							}
						}
					}
			
					panel(name: 'Connections', constraints: BorderLayout.SOUTH,
							border: compoundBorder([emptyBorder(10)])) {
						tableLayout {
							tr {
								td {
									label 'IP address:'
								}
								td {
									textField '', id: 'ip', columns: 8
								}
							}
							tr {
								td {
									label 'Port number:'
								}
								td {
									textField '', id: 'port', columns: 8
								}
							}
							tr {
								td {
									label 'UDP:'
								}
								td {
									button 'Connect', id: 'udp',
									actionPerformed: {
										new UdpClient(ip: ip.text, port: Integer.parseInt(port.text)).connect()
									}
								}
							}
							tr {
								td {
									label 'TCP:'
								}
								td {
									button 'Connect', id: 'tcp',
									actionPerformed: {
										new Client(ip: ip.text, port: Integer.parseInt(port.text)).connect()
									}
								}
							}
						}
					}
					
					panel(name: 'Controls', constraints: BorderLayout.SOUTH,
							border: compoundBorder([emptyBorder(10)])) {
						tableLayout {
							tr {
								td {
									label 'Network:'
								}
								td {
									button 'Disable', id: 'network',
									actionPerformed: {
										networkEnabled = !networkEnabled
										if (networkEnabled) {
											Server.instance.enableTCP()
											network.text = 'Disable'
										} else {
											Server.instance.disableTCP()
											network.text = 'Enable'
										}
										network.text = (networkEnabled ? 'Disable' : 'Enable')
									}
								}
							}
							tr {
								td {
									label 'Problem resolution:'
								}
								td {
									button 'Disable', id: 'resolution',
									actionPerformed: {
										resolutionEnabled = !resolutionEnabled
										resolution.text = (resolutionEnabled ? 'Disable' : 'Enable')
									}
								}
							}
						}
					}
					
					panel(name: 'Problems', constraints: BorderLayout.SOUTH,
							border: compoundBorder([emptyBorder(10)])) {
						tableLayout {
							tr {
								td {
									label '8 Queens:'
								}
								td {
									button 'Solve', id: 'eightQ',
									actionPerformed: {
										new EightQueensSolver().findSolution()
									}
								}
							}
						}
					}
				}
				// Binding of textfield's to address object.
		//		bean report,
		//		resources: bind { resources.text },
		//		problems: bind { problems.text },
		//		communication: bind { communication.text },
		//		organization: bind { organization.text },
		//		status: bind { status.text }				
			}
		}
	}
	
	private void proccessFile(File newConfig) {
	  	String message = FileUtils.getContentFromFile(newConfig)
		
		// Validate and parse initial config
		//XmlUtils.validateMessage(message)
		XmlUtils.parseMessage(message)
		
		// Set initial config
		NodeConfig.instance.id = XmlUtils.getNodeId()
		Clock.instance.startClock(XmlUtils.getTimestamp())
		println Clock.instance.getTime()
	}
}
