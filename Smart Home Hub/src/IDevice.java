import java.io.Serializable;

/// Interface defining the common behavior for all smart devices; extends Serializable for RMI and File Storage

public interface IDevice extends Serializable {
	
	// Basic control methods
	void turnOn();
	void turnOff();
	boolean isOn();

	// Getters for device info
	int getId();
	String getName();
	String getType(); // Used to write the type of the device (i.e. Light)

	// Set a new name for a device
	void setName(String name);

	// String representation for console debugging/logging
	String getStatusString();
}