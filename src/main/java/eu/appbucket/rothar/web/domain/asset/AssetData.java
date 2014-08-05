package eu.appbucket.rothar.web.domain.asset;

import java.util.Date;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;

public class AssetData {

	private Integer assetId;
	private Integer userId;
	private Date created;
	private String description;
	private AssetStatus status;
	private String uuid;
	private Integer major;
	private Integer minor;
	
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getAssetId() {
		return assetId;
	}

	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}
	
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AssetStatus getStatus() {
		return status;
	}

	public void setStatus(AssetStatus status) {
		this.status = status;
	}

	public static AssetData fromAssetEntry(AssetEntry entry) {
		AssetData data = new AssetData();
		data.setAssetId(entry.getAssetId());
		data.setStatus(AssetStatus.getStatusEnumById(entry.getStatusId()));
		data.setUserId(entry.getUserId());
		data.setCreated(entry.getCreated());
		data.setDescription(entry.getDescription());
		data.setMajor(entry.getMajor());
		data.setMinor(entry.getMinor());
		data.setUuid(entry.getUuid());
		return data;
	}
}
