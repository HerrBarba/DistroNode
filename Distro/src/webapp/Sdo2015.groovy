package webapp

import javax.jws.WebService

import main.GlobalConfig
import main.NodeConfig

import organization.Status

@WebService(endpointInterface = "webapp.Sdo2015PortType")
public class Sdo2015 implements Sdo2015PortType {

	@Override
	public boolean wannaJoin(ArrayOfRecords joinedNodes) {
		return !(NodeConfig.instance.status == Status.DECIDED || NodeConfig.instance.status == Status.ORGANIZED)
	}


	@Override
	public boolean consolidate(ArrayOfRecords newState) {
		if (NodeConfig.instance.status == Status.DECIDED)
			return false

		NodeConfig.instance.status = Status.DECIDED
		return true
	}


	@Override
	public void confirmState(ArrayOfRecords finalState) {		
		NodeConfig.instance.state == finalState.getRecord()
	}

	@Override
	public boolean leader() {
		return !(GlobalConfig.instance.nodes.keySet().findAll({it in NodeConfig.instance.state})
			.max({ GlobalConfig.instance.nodes.get(it).techCap }) > NodeConfig.instance.techCap)
	}
}