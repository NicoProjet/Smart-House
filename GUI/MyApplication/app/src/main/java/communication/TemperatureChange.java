package communication;


public class TemperatureChange {
    private int time;
    private double temperature;
    private String room;

    TemperatureChange(int time, double temperature, String room) {
        this.time=time;
        this.temperature=temperature;
        this.room=room;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
