package ui

import static javax.swing.JFrame.EXIT_ON_CLOSE
import groovy.beans.Bindable
import groovy.swing.SwingBuilder
import groovy.swing.impl.TableLayout
import groovy.swing.impl.TableLayoutCell

import java.awt.*

import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JMenuBar
import javax.swing.JTabbedPane

import main.*
import solver.*
import utils.*
import connection.*

@Bindable
class NodeReport {
	String problems, communication, status
	String toString() { "address[problems=$problems, communication=$communication, status=$status]" }
}

class MainWindow {
	def networkEnabled = true, resolutionEnabled = true
	
	def report = new NodeReport(
		problems: '0', 
		communication: 'No connections made',
		status: 'Fine'
		)
	
	def swingBuilder = new SwingBuilder()	
	
	public void startUI() {
		swingBuilder.edt {  // edt method makes sure UI is build on Event Dispatch Thread.
			lookAndFeel 'nimbus'
			frame(title: 'Address', size: [600, 700],
			show: true, locationRelativeTo: null,
			defaultCloseOperation: EXIT_ON_CLOSE) {
				borderLayout(vgap: 5)
				
				createMenuBar()
				
				tabbedPane(id: 'tabs', tabLayoutPolicy:JTabbedPane.SCROLL_TAB_LAYOUT) {
					panel(name: 'Reporting', constraints: BorderLayout.CENTER,
							border: compoundBorder([emptyBorder(10)])) {
						tableLayout {
							tr {
								td { label 'Leader: ' }
								td { label text:bind(source:NodeConfig.instance, 'leader') }
							}							
							tr {
								td { label 'Tech Cap: ' }
								td { label text:bind(source:NodeConfig.instance, 'techCap') }
							}
							tr {
								td { label 'Water: ' }
								td { label text:bind(source:NodeConfig.instance, 'water') }
							}
							tr { 
								td { label 'Metal: ' }
								td { label text:bind(source:NodeConfig.instance, 'metal') }
							}
							tr {
								td { label 'Food: ' }
								td { label text:bind(source:NodeConfig.instance, 'food') }
							}
							tr {
								td { label 'Problems:' }
								td { textArea report.problems, id: 'problems', columns: 30, rows: 4 }
							}
							tr {
								td { label 'Communication:' }
								td { textArea report.communication, id: 'communication', columns: 30, rows: 4 }
							}
							tr {
								td { label 'General Status:' }
								td { textArea report.status, id: 'status', columns: 30, rows: 4 }
							}
						}
					}
							
					panel(name: 'Organization', constraints: BorderLayout.CENTER,
							border: compoundBorder([emptyBorder(10)])) {
						gridLayout(cols: 26, rows: 26)
						(0..<26).each { y ->
							(0..<26).each { x ->
								label(text:bind { GlobalConfig.instance.positions[x][y] },
									//text:bind(source:GlobalConfig.instance, "positions[${x}][${y}]"),
									//text:GlobalConfig.instance.positions[x][y],
									horizontalAlignment: JLabel.CENTER,
									border: compoundBorder([lineBorder(color: Color.BLACK)]))									
							}
						}
					}
			
					panel(name: 'Connections', constraints: BorderLayout.SOUTH,
							border: compoundBorder([emptyBorder(10)])) {
						tableLayout {
							tr {
								td { label 'IP address:' }
								td { textField '', id: 'ip', columns: 8 }
							}
							tr {
								td { label 'Port number:' }
								td { textField '', id: 'port', columns: 8 }
							}
							tr {
								td { label 'UDP:' }
								td { button 'Connect', id: 'udp',
									actionPerformed: {
										UdpClient.unicast(ip.text, Integer.parseInt(port.text), MessageType.TIME_REQUEST, 'A1')
									}
								}
							}
							tr {
								td { label 'TCP:' }
								td { button 'Connect', id: 'tcp',
									actionPerformed: {
										new Client().unicast(ip.text, Integer.parseInt(port.text), "NR-1")
									}
								}
							}
						}
					}
					
					panel(name: 'Controls', constraints: BorderLayout.SOUTH,
							border: compoundBorder([emptyBorder(10)])) {
						tableLayout {
							tr {
								td { label 'Network:' }
								td { button 'Disable', id: 'network',
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
								td { label 'Problem resolution:' }
								td { button 'Disable', id: 'resolution',
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
								td { label '8 Queens:' }
								td { button 'Solve', id: 'eightQ',
									actionPerformed: {
										new EightQueensSolver().findSolution()
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void proccessFile(File newConfig) {
	  	String message = FileUtils.getContentFromFile(newConfig)
		
		// Validate and parse initial config
		//XmlUtils.validateMessage(message)
		try {
			XmlUtils.parseMessage(message)
		} catch (Exception e) {
			e.printStackTrace()
			return
		}
		
		// Set initial config
		NodeConfig.instance.id = XmlUtils.getNodeId()
		Clock.instance.startClock(XmlUtils.getTimestamp())
		println Clock.instance.getTime()
	}
	
	private JMenuBar createMenuBar() {
		def openXmlConfig = new JFileChooser(
			dialogTitle: "Choose an XML file",
			fileSelectionMode: JFileChooser.FILES_ONLY)
		
		return swingBuilder.menuBar {
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
	}
}
