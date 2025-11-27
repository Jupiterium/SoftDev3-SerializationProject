/* Concrete class for a Camera device; implements IDevice interface */
public class Camera implements IDevice
{
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private boolean isOn;
    
    public Camera(int id, String name) 
    {
        this.id = id;
        this.name = name;
        this.isOn = false; // Default state is OFF
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
        return "Camera"; 
    }

    @Override
    public String getStatusString() {
        return isOn ? "Recording" : "Idle";
    }
    
    @Override
    public String toString() {
        return "Camera [ID=" + id + ", Name=" + name + ", On=" + isOn + "]";
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
