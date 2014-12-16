package eu.appbucket.rothar.core.domain.report;

import java.util.Date;

public class ReportDateFilter extends ReportListFilter {
	
	private Date date;
	
	protected ReportDateFilter() {
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Date getDate() {
		return date;
	};
	
	public static class Builder extends ReportListFilter.Builder {
		
		private Date date;
		
		public Builder forDate(Date date) {
			this.date = date; 
			return this;
		}
		
		public ReportDateFilter build() {
			super.build();
			ReportListFilter superFilter = new ReportListFilter.Builder().build();
			ReportDateFilter filter = new ReportDateFilter();
			filter.setDate(this.date);
			return filter;
		}
	};
}
