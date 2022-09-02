package binplus.foodiswill.Model;

public class ProductVariantModel {


//"product_attribute":"[{\"id\":\"568\",\"attribute_name\":\"10KG\",\"attribute_value\":\"355\"," +
//		"\"attribute_mrp\":\"355\",\"deal_price\":null,\"deal_qty\":null,\"is_deal\":\"0\"}]
//    String product_id;
    String id,attribute_name,attribute_value,attribute_mrp ;
    String deal_price,deal_qty,is_deal;

    public ProductVariantModel() {
    }

//    public ProductVariantModel(String id, String product_id, String attribute_name, String attribute_value, String attribute_mrp) {
//        this.id = id;
//        this.product_id = product_id;
//        this.attribute_name = attribute_name;
//        this.attribute_value = attribute_value;
//        this.attribute_mrp = attribute_mrp;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public String getProduct_id() {
//        return product_id;
//    }
//
//    public void setProduct_id(String product_id) {
//        this.product_id = product_id;
//    }

    public String getAttribute_name() {
        return attribute_name;
    }

    public void setAttribute_name(String attribute_name) {
        this.attribute_name = attribute_name;
    }

    public String getAttribute_value() {
        return attribute_value;
    }

    public void setAttribute_value(String attribute_value) {
        this.attribute_value = attribute_value;
    }

    public String getAttribute_mrp() {
        return attribute_mrp;
    }

    public void setAttribute_mrp(String attribute_mrp) {
        this.attribute_mrp = attribute_mrp;
    }

    public String getDeal_price() {
        return deal_price;
    }

    public void setDeal_price(String deal_price) {
        this.deal_price = deal_price;
    }

    public String getDeal_qty() {
        return deal_qty;
    }

    public void setDeal_qty(String deal_qty) {
        this.deal_qty = deal_qty;
    }

    public String getIs_deal() {
        return is_deal;
    }

    public void setIs_deal(String is_deal) {
        this.is_deal = is_deal;
    }
}
