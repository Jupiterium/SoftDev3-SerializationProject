import java.io.*;
import java.util.ArrayList;

/* Singleton Pattern: Manages the central list of devices and handles Serialization */
public class HomeHub 
{
    // The single static instance
    private static HomeHub instance;
    
    // Data storage
    private ArrayList<IDevice> devices;
    private int nextId = 1; // Simple auto-incrementing ID counter
    private final String FILE_NAME = "smart_home.ser";
    
    // Private constructor (prevents instantiation from outside)
    private HomeHub() 
    {
        devices = new ArrayList<>();
        loadState(); // Load data from file immediately upon creation
    }
    
    // The public accessor to get the static instance
    public static synchronized HomeHub getInstance() 
    {
        if (instance == null) 
        {
            instance = new HomeHub();
        }
        return instance;
    }
    
    
    
    // --- Core System Logic ---
    // Add a device to the list and save
    public void addDevice(IDevice device) 
    {
        if (device != null) 
        {
            devices.add(device);
            saveState(); // Auto-save on change
            System.out.println("Hub: Added " + device.getName());
        }
    }
    
    // Remove a device by ID and save
    public void removeDevice(int id) 
    {
        devices.removeIf(d -> d.getId() == id);
        saveState();
        System.out.println("Hub: Removed device ID " + id);
    }
    
    // Toggle on/off
    public void toggleDevice(int id) 
    {
        for (IDevice d : devices) 
        {
            if (d.getId() == id) 
			{
				if (d.isOn()) { d.turnOff();} 
				else { d.turnOn(); }
				break;
            }
        }
        saveState(); 
    }
    
    // Turn off all devices
    public void turnAllOff() {
        for(IDevice d : devices) d.turnOff();
        saveState(); 
        System.out.println("Hub: All devices turned OFF.");
    }
    
    // Turn on all devices
    public void turnAllOn() {
        for(IDevice d : devices) d.turnOn();
        saveState(); 
        System.out.println("Hub: All devices turned ON.");
    }
    
    public void updateDeviceName(int id, String newName) {
        for (IDevice d : devices) {
            if (d.getId() == id) {
                d.setName(newName); // Update the device's name
                saveState();
                System.out.println("Hub: Renamed device " + id + " to " + newName);
                return;
            }
        }
    }
    
	// Return the list (for the RMI service to send to client)
	public ArrayList<IDevice> getDevices() {
		return devices;
	}
	
	// Helper method to generate unique IDs
	public int generateId() {
		return nextId++;
	}
    
    // --- Serialization ---
    private void saveState() 
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) 
        {
            oos.writeObject(devices); // Write the list
            oos.writeInt(nextId);     // Write the ID counter so we don't reuse IDs
            System.out.println("Hub: State saved.");
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadState() 
    {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) 
        {
            devices = (ArrayList<IDevice>) ois.readObject();
            nextId = ois.readInt();
            System.out.println("Hub: State loaded. Devices: " + devices.size());
        } 
        catch (FileNotFoundException e) // See if there's no file and catch
        {
            System.out.println("Hub: No save file found. Starting fresh.");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}