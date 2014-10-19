package classes;

import java.util.List;

public class Id {

	private String dmdlogId;
	private String logId;
	private List<String> physIds;
	
	public Id(String dmdlogId, String logId, List<String> physIds) {
		this.dmdlogId = dmdlogId;
		this.logId = logId;
		this.physIds = physIds;
	}
	
	public String getDmdlogId() {
		return dmdlogId;
	}
	public void setDmdlogId(String dmdlogId) {
		this.dmdlogId = dmdlogId;
	}
	public String getLogId() {
		return logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}
	public List<String> getPhysIds() {
		return physIds;
	}
	public void setPhysIds(List<String> physIds) {
		this.physIds = physIds;
	}

	@Override
	public String toString() {
		return "Id [dmdlogId=" + dmdlogId + ", logId=" + logId + ", physIds=" + physIds + "]";
	}
	
	
	
}
