package binplus.foodiswill.Model;



public class Socity_model {


    String socity_id;
    String socity_name;
    String pincode;

    String delivery_charge;

    public Socity_model() {
    }

    public Socity_model(String socity_id, String socity_name, String pincode, String delivery_charge) {
        this.socity_id = socity_id;
        this.socity_name = socity_name;
        this.pincode = pincode;
        this.delivery_charge = delivery_charge;
    }

    public String getSocity_id(){
        return socity_id;
    }

    public String getSocity_name(){
        return socity_name;
    }

    public String getPincode(){
        return pincode;
    }

    public String getDelivery_charge(){
        return delivery_charge;
    }

}
