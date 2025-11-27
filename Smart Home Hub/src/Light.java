/// Concrete class for a Light type device; implements IDevice interface

public class Light implements IDevice {
	
	private static final long serialVersionUID = 1L;
	
	// Private attributes
	private int id;
	private String name;
	private boolean isOn;

	// Constructor: Sets up the light with a specific ID and Name
	public Light(int id, String name) {
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
		return "Light";
	}
	
	@Override
	public String getStatusString() {
		return isOn ? "ON" : "OFF"; // For a light, it can either be ON or OFF
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Light [ID=" + id + ", Name=" + name + ", On=" + isOn + "]";
	}
}