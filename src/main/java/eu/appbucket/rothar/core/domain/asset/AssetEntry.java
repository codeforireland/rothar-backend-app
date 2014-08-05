package eu.appbucket.rothar.core.domain.asset;

import java.util.Date;

import eu.appbucket.rothar.web.domain.asset.AssetData;

public class AssetEntry {

	private Integer assetId;
	private Integer userId;
	private Integer statusId;
	private String uuid;
	private Integer major;
	private Integer minor;
	private String description;
	private Date created;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public Integer getAssetId() {
		return assetId;
	}

	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}
	
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public Integer getStatusId() {
		return statusId;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getMajor() {
		return major;
	}

	public void setMajor(Integer major) {
		this.major = major;
	}

	public Integer getMinor() {
		return minor;
	}

	public void setMinor(Integer minor) {
		this.minor = minor;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assetId == null) ? 0 : assetId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssetEntry other = (AssetEntry) obj;
		if (assetId == null) {
			if (other.assetId != null)
				return false;
		} else if (!assetId.equals(other.assetId))
			return false;
		return true;
	}


	public static AssetEntry fromAssetData(AssetData data) {
		AssetEntry entry = new AssetEntry();
		entry.setAssetId(entry.getAssetId());
		entry.setStatusId(data.getStatus().getStatusId());
		entry.setCreated(data.getCreated());
		entry.setUserId(data.getUserId());
		entry.setMajor(data.getMajor());
		entry.setMinor(data.getMinor());
		entry.setUuid(data.getUuid());
		return entry;
	}	
}
