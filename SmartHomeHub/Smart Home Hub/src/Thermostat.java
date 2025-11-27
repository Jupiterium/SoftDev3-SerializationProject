/* Concrete class for a Thermostat device; implements IDevice interface */
public class Thermostat implements IDevice 
{
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private boolean isOn;
    private double temperature; // Unique attribute for Thermostat

    public Thermostat(int id, String name) 
    {
        this.id = id;
        this.name = name;
        this.isOn = false; // Default state is OFF
        this.temperature = 21.38; 
    }

    @Override
    public void turnOn() {
        this.isOn = true;
    }

    @Override
    public void turnOff() {
        this.isOn = false;
    }

    @Override
    public boolean isOn() {
        return isOn;
    }
    
    // Getter for the unique temperature field
    public double getTemperature() {
        return temperature;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "Thermostat";
    }

    @Override
    public String getStatusString() 
    {
        // Returns a custom status string showing temperature
        return isOn ? "Active (" + temperature + "°C)" : "Standby";
    }
    
    @Override
    public String toString() {
        return "Thermostat [ID=" + id + ", Name=" + name + ", Temp=" + temperature + "]";
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
}