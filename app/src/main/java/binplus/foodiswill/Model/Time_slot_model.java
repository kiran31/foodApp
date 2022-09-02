package binplus.foodiswill.Model;



public class Time_slot_model {

    String id;
    String from_time;
    String to_time;
    String slot;

    public Time_slot_model() {
    }

    public Time_slot_model(String id, String from_time, String to_time, String slot) {
        this.id = id;
        this.from_time = from_time;
        this.to_time = to_time;
        this.slot = slot;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFrom_time(String from_time) {
        this.from_time = from_time;
    }

    public void setTo_time(String to_time) {
        this.to_time = to_time;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getFrom_time(){
        return from_time;
    }

    public String getTo_time(){
        return to_time;
    }

    public String getSlot(){
        return slot;
    }

}
