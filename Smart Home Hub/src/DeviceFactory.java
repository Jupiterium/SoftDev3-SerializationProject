/// Factory Pattern: Responsible for creating specific IDevice instances based on a string type

public class DeviceFactory {
	// Returns a new device object based on the input string
	public IDevice createDevice(String type, int id, String name) {
		
		// Switch on the type string
		switch (type.toLowerCase()) {
		case "light":
			return new Light(id, name);
		case "thermostat":
			return new Thermostat(id, name);
		case "camera":
			return new Camera(id, name);

		// Add as many device types as needed (expandable)

		default:
			System.out.println("Unknown device type: " + type);
			return null;
		}
	}
}