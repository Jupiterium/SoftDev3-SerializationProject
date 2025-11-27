import java.io.*;
import java.util.ArrayList;

/// Singleton Pattern (the brain): Manages the central list of devices and handles Serialization/Deserialization

public class HomeHub {
	
	// The single static instance
	private static HomeHub instance;

	// Data storage
	private ArrayList<IDevice> devices;
	private int nextId = 1; // ID counter, which is incremented for consistency
	private final String FILE_NAME = "smart_home.ser"; // The serialized/deserialized file that will store our data

	// Private constructor to prevent instantiation from outside
	private HomeHub() {
		devices = new ArrayList<>();
		loadState(); // Load data from file immediately upon creation
	}

	// The public accessor to get the static instance
	public static synchronized HomeHub getInstance() {
		if (instance == null) {
			instance = new HomeHub();
		}
		return instance;
	}

	
	/* Core System Logic */
	
	// Add a device to the list and save
	public void addDevice(IDevice device) {
		if (device != null) {
			devices.add(device);
			saveState(); // Auto-save on change
			System.out.println("Hub: Added " + device.getName());
		}
	}

	// Remove a device by ID
	public void removeDevice(int id) {
		devices.removeIf(d -> d.getId() == id);
		saveState();
		System.out.println("Hub: Removed device ID " + id);
	}

	// Toggle on/off
	public void toggleDevice(int id) {
		for (IDevice d : devices) {
			if (d.getId() == id) {
				if (d.isOn()) {
					d.turnOff();
				} else {
					d.turnOn();
				}
				break;
			}
		}
		saveState();
	}

	// Turn off all devices
	public void turnAllOff() {
		for (IDevice d : devices)
			d.turnOff();
		saveState();
		System.out.println("Hub: All devices turned OFF.");
	}

	// Turn on all devices
	public void turnAllOn() {
		for (IDevice d : devices)
			d.turnOn();
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

	
	
	// Persistence Layer
	
	/*
	* Serialization: Saves the current state of the system to the file (smart_home.ser).
    * This is called automatically after every 'write' operation (Add, Remove, Toggle)
    * to ensure data is never lost if the server stops unexpectedly.
    */
	private void saveState() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
			oos.writeObject(devices); // Write the list
			oos.writeInt(nextId); // Write the ID counter so we don't reuse IDs
			System.out.println("Hub: State saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
    * Deserialization: Loads the data from the file back into memory.
    * Called once, inside the private constructor when the server starts.
    */
	@SuppressWarnings("unchecked") // Removes the warning caused by the casting 
	private void loadState() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
			// Read the data
			devices = (ArrayList<IDevice>) ois.readObject(); 
			nextId = ois.readInt();
			System.out.println("Hub: State loaded. System contains " + devices.size() + " devices.");
		} catch (FileNotFoundException e) { 
			// See if there's no file and catch
			System.out.println("Hub: Saved file not found. Creating a new one...");
		} catch (Exception e) {
			// Catch other exceptions and print them
			e.printStackTrace();
		}
	}
}