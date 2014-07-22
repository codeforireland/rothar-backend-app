package eu.appbucket.rothar.core.domain.report;

import java.util.Date;

import eu.appbucket.rothar.web.domain.report.ReportData;

public class ReportEntry {

	private String assetId;
	private double latitude;
	private double longitude;
	private Date created;

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
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
	
	public static ReportEntry fromReportEntry(ReportData reportEntry) {
		ReportEntry reportData = new ReportEntry();
		reportData.setAssetId(reportEntry.getAssetId());
		reportData.setLongitude(reportEntry.getLongitude());
		reportData.setLatitude(reportEntry.getLatitude());
		reportData.setCreated(reportEntry.getCreated());
		return reportData;
	}
}
