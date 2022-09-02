package binplus.foodiswill.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 20,March,2021
 */
public class CityModel {
    String city_id,city_name,cityAdmin,adminPassword,status,city_image,stop_order,cod,gateway_status,stop_order_image,stop_order_notification,stop_order_title;
     boolean selected =false;
    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCityAdmin() {
        return cityAdmin;
    }

    public void setCityAdmin(String cityAdmin) {
        this.cityAdmin = cityAdmin;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity_image() {
        return city_image;
    }

    public void setCity_image(String city_image) {
        this.city_image = city_image;
    }

    public String getStop_order() {
        return stop_order;
    }

    public void setStop_order(String stop_order) {
        this.stop_order = stop_order;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getGateway_status() {
        return gateway_status;
    }

    public void setGateway_status(String gateway_status) {
        this.gateway_status = gateway_status;
    }

    public String getStop_order_image() {
        return stop_order_image;
    }

    public void setStop_order_image(String stop_order_image) {
        this.stop_order_image = stop_order_image;
    }

    public String getStop_order_notification() {
        return stop_order_notification;
    }

    public void setStop_order_notification(String stop_order_notification) {
        this.stop_order_notification = stop_order_notification;
    }

    public String getStop_order_title() {
        return stop_order_title;
    }

    public void setStop_order_title(String stop_order_title) {
        this.stop_order_title = stop_order_title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
