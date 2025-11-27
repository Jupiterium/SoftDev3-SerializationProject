import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/// RMI Implementation: Connects network calls to the Singleton Hub and Factory; contains Main method

public class SmartHomeServer extends UnicastRemoteObject implements ISmartHomeService {
	private static final long serialVersionUID = 1L;
	private DeviceFactory factory; // Instance of the Factory

	// Constructor
	public SmartHomeServer() throws RemoteException {
		super();
		this.factory = new DeviceFactory();
	}

	// RMI Method Implementations
	@Override
	public void addNewDevice(String type, String name) throws RemoteException {
		// Get the Hub Singleton
		HomeHub hub = HomeHub.getInstance();

		// Generate a new ID
		int newId = hub.generateId();

		// Use Factory to create the object
		IDevice newDevice = factory.createDevice(type, newId, name);

		// Add to Hub
		hub.addDevice(newDevice);
	}

	@Override
	public void removeDevice(int id) throws RemoteException {
		HomeHub.getInstance().removeDevice(id);
	}

	@Override
	public void toggleDeviceState(int id) throws RemoteException {
		HomeHub.getInstance().toggleDevice(id);
	}

	@Override
	public void turnAllOff() throws RemoteException {
		HomeHub.getInstance().turnAllOff();
	}

	@Override
	public void turnAllOn() throws RemoteException {
		HomeHub.getInstance().turnAllOn();
	}

	@Override
	public void updateDeviceName(int id, String newName) throws RemoteException {
		HomeHub.getInstance().updateDeviceName(id, newName);
	}

	@Override
	public ArrayList<IDevice> getAllDevices() throws RemoteException {
		return HomeHub.getInstance().getDevices();
	}

	// Main Entry of the Server Program
	public static void main(String[] args) {
		try {
			// Start the RMI Registry on port 1099
			LocateRegistry.createRegistry(1099);

			// Create the server object
			SmartHomeServer server = new SmartHomeServer();

			// Bind it to a name so the client can find it
			Naming.rebind("SmartHomeService", server);

			System.out.println("Server is running and ready...");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}
}