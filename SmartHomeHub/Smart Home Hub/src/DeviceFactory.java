/* Factory Pattern: Responsible for creating specific IDevice instances based on a string type */
public class DeviceFactory 
{
    // Returns a new device object based on the input string (Light vs Thermostat)
    public IDevice createDevice(String type, int id, String name) 
    {    
        // Switch on the type string 
        switch (type.toLowerCase()) {
            case "light":
                return new SmartLight(id, name);
            case "thermostat":
                return new Thermostat(id, name);
            case "camera":
            	return new Camera(id, name);
            	
            	// Can add as many devices as needed
            
            default:
                System.out.println("Unknown device type: " + type);
                return null;
        }
    }
}