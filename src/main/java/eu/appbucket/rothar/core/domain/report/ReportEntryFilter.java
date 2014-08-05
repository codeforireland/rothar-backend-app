package eu.appbucket.rothar.core.domain.report;

public class ReportEntryFilter {
	
	private int offset;
	private int limit;
	private String sort;
	private String order;
	private Integer assetId;
	private Integer userId;
	
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
	
	public Integer getAssetId() {
		return assetId;
	}

	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	public static class Builder {
		
		private int offset;
		private int limit;
		private String sort;
		private String order;
		private Integer assetId;
		private Integer userId;
		
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
		
		public Builder forAsset(Integer assetId) {
			this.assetId = assetId; 
			return this;
		}
		
		public Builder ownerBy(Integer userId) {
			this.userId = userId; 
			return this;
		}
		
		public ReportEntryFilter build() {
			ReportEntryFilter filter = new ReportEntryFilter();
			filter.setLimit(this.limit);
			filter.setOffset(this.offset);
			filter.setOrder(this.order);
			filter.setSort(this.sort);
			filter.setAssetId(this.assetId);
			filter.setUserId(this.userId);
			return filter;
		}
	};
}
