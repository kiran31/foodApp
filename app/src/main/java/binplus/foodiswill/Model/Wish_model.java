package binplus.foodiswill.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Wish_model {

    String product_id;
    String product_name;
    String cat_id;
    String product_description;
    String product_attribute;
    String price;
    String product_images;
    String stock;
    String unit_value;
    String unit;
    String increament;
    String rewards;
    String title;
    String mrp;


    String category_id;
    String deal_price;
    String start_date;
    String start_time;
    String end_date;
    String end_time;
    String product_image;
    String product_name_arb;
    String product_description_arb;
    String status;
    String in_stock;
    String size;
    String color;
    String product_name_hindi;
    String city;



    @SerializedName("sub_cat")
    ArrayList<Category_subcat_model> category_sub_datas;

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getProduct_attribute() {
        return product_attribute;
    }

    public void setProduct_attribute(String product_attribute) {
        this.product_attribute = product_attribute;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct_images() {
        return product_images;
    }

    public void setProduct_images(String product_images) {
        this.product_images = product_images;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getUnit_value() {
        return unit_value;
    }

    public void setUnit_value(String unit_value) {
        this.unit_value = unit_value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIncreament() {
        return increament;
    }

    public void setIncreament(String increament) {
        this.increament = increament;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getDeal_price() {
        return deal_price;
    }

    public void setDeal_price(String deal_price) {
        this.deal_price = deal_price;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_name_arb() {
        return product_name_arb;
    }

    public void setProduct_name_arb(String product_name_arb) {
        this.product_name_arb = product_name_arb;
    }

    public String getProduct_description_arb() {
        return product_description_arb;
    }

    public void setProduct_description_arb(String product_description_arb) {
        this.product_description_arb = product_description_arb;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIn_stock() {
        return in_stock;
    }

    public void setIn_stock(String in_stock) {
        this.in_stock = in_stock;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getProduct_name_hindi() {
        return product_name_hindi;
    }

    public void setProduct_name_hindi(String product_name_hindi) {
        this.product_name_hindi = product_name_hindi;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ArrayList<Category_subcat_model> getCategory_sub_datas() {
        return category_sub_datas;
    }

    public void setCategory_sub_datas(ArrayList<Category_subcat_model> category_sub_datas) {
        this.category_sub_datas = category_sub_datas;
    }
}
