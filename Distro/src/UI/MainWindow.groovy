package UI

import groovy.swing.SwingBuilder
import groovy.beans.Bindable
import static javax.swing.JFrame.EXIT_ON_CLOSE
import java.awt.*
import javax.swing.JButton

@Bindable
class NodeReport {
	String resources, problems, communication, organization, status
	String toString() { "address[resources=$resources, problems=$problems, organization=$organization, communication=$communication, status=$status]" }
}

def networkEnabled = true, resolutionEnabled = true

def report = new NodeReport(
	resources: 'Nothing', 
	problems: '0', 
	communication: 'No connections made',
	organization: 'None',
	status: 'Fine'
	)

def swingBuilder = new SwingBuilder()
swingBuilder.edt {  // edt method makes sure UI is build on Event Dispatch Thread.
	lookAndFeel 'nimbus'
	frame(title: 'Address', size: [600, 700],
	show: true, locationRelativeTo: null,
	defaultCloseOperation: EXIT_ON_CLOSE) {
		borderLayout(vgap: 5)

		panel(constraints: BorderLayout.CENTER,
		border: compoundBorder([emptyBorder(10), titledBorder('Reporting:')])) {
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

		panel(constraints: BorderLayout.SOUTH,
		border: compoundBorder([emptyBorder(10), titledBorder('Controls:')])) {		 
			tableLayout {
				tr {
					td {
						label 'Network:'
					}
					td {
						button 'Disable', id: 'network',
						actionPerformed: { 
							networkEnabled = !networkEnabled
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
		
		// Binding of textfield's to address object.
//		bean report,
//		resources: bind { resources.text },
//		problems: bind { problems.text },
//		communication: bind { communication.text },
//		organization: bind { organization.text },
//		status: bind { status.text }
		
	}
}