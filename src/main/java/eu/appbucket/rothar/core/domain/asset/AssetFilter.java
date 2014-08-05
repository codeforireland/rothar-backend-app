package eu.appbucket.rothar.core.domain.asset;


public class AssetFilter {
	
	private String userId;
	private int offset;
	private int limit;
	private String sort;
	private String order;
	
	private AssetFilter(String userId) {
		this.userId = userId;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public String getSort() {
		return sort;
	}
	
	public String getOrder() {
		return order;
	}
	
	public String getUserId() {
		return userId;
	}
	
	private void setOffset(int offset) {
		this.offset = offset;
	}

	private void setLimit(int limit) {
		this.limit = limit;
	}

	private void setSort(String sort) {
		this.sort = sort;
	}

	private void setOrder(String order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "AssetFilter [userId=" + userId + ", sort=" + sort + ", order="
				+ order + ", offset=" + offset + ", limit=" + limit + "]";
	}

	public static class Builder {
		
		private String userId;
		private int offset;
		private int limit;
		private String sort;
		private String order;
		
		public Builder starFrom(int offset) {
			this.offset = offset;
			return this;
		}
		
		public Builder limitTo(int limit) {
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
		
		public AssetFilter buildFilterForUser(String userId) {
			AssetFilter filter = new AssetFilter(userId);
			filter.setLimit(this.limit);
			filter.setOffset(this.offset);
			filter.setSort(this.sort);
			filter.setOrder(this.order);
			return filter;
		}
	}
}
