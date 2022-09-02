package binplus.foodiswill.Model;

import com.google.gson.annotations.SerializedName;

public class ProductAttributeModel{

//"product_attribute":"[{\"id\":\"568\",\"attribute_name\":\"10KG\",\"attribute_value\":\"355\"," +
//		"\"attribute_mrp\":\"355\",\"deal_price\":null,\"deal_qty\":null,\"is_deal\":\"0\"}]
//
//

//	@SerializedName("city")
//	private String city;

//	@SerializedName("product_id")
//	private String productId;

	@SerializedName("deal_qty")
	private String deal_qty;

	@SerializedName("is_deal")
	private String is_deal;

	@SerializedName("deal_price")
	private String dealPrice;

	@SerializedName("attribute_name")
	private String attributeName;

	@SerializedName("id")
	private String id;

	@SerializedName("attribute_value")
	private String attributeValue;

	@SerializedName("attribute_mrp")
	private String attributeMrp;

//	@SerializedName("stock_value")
//	private String stockValue;

//	public String getCity(){
//		return city;
//	}
//
//	public String getProductId(){
//		return productId;
//	}

	public String getAttributeName(){
		return attributeName;
	}

	public String getId(){
		return id;
	}

	public String getAttributeValue(){
		return attributeValue;
	}

	public String getAttributeMrp(){
		return attributeMrp;
	}

	public String getDeal_qty() {
		return deal_qty;
	}

	public String getIs_deal() {
		return is_deal;
	}

	public String getDealPrice() {
		return dealPrice;
	}
	//	public String getStockValue(){
//		return stockValue;
//	}
//
//	public void setCity(String city) {
//		this.city = city;
//	}
//
//	public void setProductId(String productId) {
//		this.productId = productId;
//	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public void setAttributeMrp(String attributeMrp) {
		this.attributeMrp = attributeMrp;
	}

	public void setDeal_qty(String deal_qty) {
		this.deal_qty = deal_qty;
	}

	public void setIs_deal(String is_deal) {
		this.is_deal = is_deal;
	}

	public void setDealPrice(String dealPrice) {
		this.dealPrice = dealPrice;
	}

	@Override
	public String toString() {
		return "ProductAttributeModel{" +
				"deal_qty='" + deal_qty + '\'' +
				", is_deal='" + is_deal + '\'' +
				", dealPrice='" + dealPrice + '\'' +
				", attributeName='" + attributeName + '\'' +
				", id='" + id + '\'' +
				", attributeValue='" + attributeValue + '\'' +
				", attributeMrp='" + attributeMrp + '\'' +
				'}';
	}
}