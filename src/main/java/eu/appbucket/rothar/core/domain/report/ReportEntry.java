package eu.appbucket.rothar.core.domain.report;

import java.util.Date;

import eu.appbucket.rothar.web.domain.report.ReportData;

public class ReportEntry {

	private Integer assetId;
	private double latitude;
	private double longitude;
	private Date created;
	private Integer reporterId;
	
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
	
	public static ReportData fromReportData(ReportEntry data) {
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

	public static ReportEntry fromReportEntry(ReportData reportEntry) {
		ReportEntry reportData = new ReportEntry();
		reportData.setAssetId(reportEntry.getAssetId());
		reportData.setReporterId(reportEntry.getReporterId());
		reportData.setLongitude(reportEntry.getLongitude());
		reportData.setLatitude(reportEntry.getLatitude());
		reportData.setCreated(reportEntry.getCreated());
		return reportData;
	}
}
