package walmart;

public class RecommendedProductsPojo {
	
	
	
	private String productId;
	private String rateText;
	private Integer count;
	public String getProduct() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getRateText() {
		return rateText;
	}
	public void setRateText(String rateText) {
		this.rateText = rateText;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}

}
