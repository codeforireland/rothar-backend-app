package eu.appbucket.rothar.core.domain.report;

public class ReportEntryFilter {
	
	private int offset;
	private int limit;
	private String sort;
	private String order;
	private String assetId;
	
	private ReportEntryFilter() {
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}


	public static class Builder {
		
		private int offset;
		private int limit;
		private String sort;
		private String order;
		private String assetId;
		
		public Builder fromOffset(int offset) {
			this.offset = offset;
			return this;
		}
		
		public Builder withLimit(int limit) {
			this.limit = limit; 
			return this;
		}
		
		public Builder sortBy(String sort) {
			this.sort = sort; 
			return this;
		}
		
		public Builder orderBy(String order) {
			this.order = order; 
			return this;
		}
		
		public Builder forAsset(String assetId) {
			this.assetId = assetId; 
			return this;
		}
		
		public ReportEntryFilter build() {
			ReportEntryFilter filter = new ReportEntryFilter();
			filter.setLimit(this.limit);
			filter.setOffset(this.offset);
			filter.setOrder(this.order);
			filter.setSort(this.sort);
			filter.setAssetId(this.assetId);
			return filter;
		}
	};
}
