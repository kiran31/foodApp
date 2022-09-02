package binplus.foodiswill.Model;

public class PincodeModel {
    String society_id ;
    String society_name;
    String pincode ;
    String deli_charge ;

    public PincodeModel()
    {
    }

    public PincodeModel(String society_id, String society_name, String pincode, String deli_charge) {
        this.society_id = society_id;
        this.society_name = society_name;
        this.pincode = pincode;
        this.deli_charge = deli_charge;
    }

    public String getSociety_id() {
        return society_id;
    }

    public void setSociety_id(String society_id) {
        this.society_id = society_id;
    }

    public String getSociety_name() {
        return society_name;
    }

    public void setSociety_name(String society_name) {
        this.society_name = society_name;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getDeli_charge() {
        return deli_charge;
    }

    public void setDeli_charge(String deli_charge) {
        this.deli_charge = deli_charge;
    }
}
