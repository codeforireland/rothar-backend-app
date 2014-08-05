package eu.appbucket.rothar.web.domain.report;

import java.util.Date;

import eu.appbucket.rothar.core.domain.report.ReportEntry;

public class ReportData {

	private Integer assetId;
	private Integer reporterId;
	private double latitude;
	private double longitude;
	private Date created;
	private String url;

	public Integer getReporterId() {
		return reporterId;
	}

	public void setReporterId(Integer reporterId) {
		this.reporterId = reporterId;
	}

	public Integer getAssetId() {
		return assetId;
	}

	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public static ReportData fromReportEntry(ReportEntry data) {
		ReportData entry = new ReportData();
		entry.setAssetId(data.getAssetId());
		entry.setReporterId(data.getReporterId());
		entry.setLongitude(data.getLongitude());
		entry.setLatitude(data.getLatitude());
		entry.setCreated(data.getCreated());
		entry.setUrl(
				"https://maps.google.com/?q=" 
				+ data.getLatitude() 
				+ "," 
				+ data.getLongitude());
		return entry;
	}
}
