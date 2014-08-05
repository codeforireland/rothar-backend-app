package eu.appbucket.rothar.web.domain.asset;

public enum AssetStatus {

	STOLEN(2),
	RECOVERED(3),
	WITH_OWNER(1),
	DEACTIVATED(4);
	
	private int statusId;
	
	private AssetStatus(int statusId) {
		this.statusId = statusId;
	}
	
	public int getStatusId() {
		return this.statusId;
	}
	
	public static AssetStatus getStatusEnumById(int statusId) {
		switch(statusId) {
	        case  1: return WITH_OWNER;
	        case  2: return STOLEN;
	        case  3: return RECOVERED;
	        case  4: return DEACTIVATED;
		}
		return null;
	}
}
