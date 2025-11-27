import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/* RMI Interface: Defines the methods the Client can call on the Server; extends Remote */
public interface ISmartHomeService extends Remote {
    
    // Adds a new device (Client sends type/name -> Server Factory creates it)
    void addNewDevice(String type, String name) throws RemoteException;
    
    // Removes a device by ID
    void removeDevice(int id) throws RemoteException;
    
    // Toggles a device's state
    void toggleDeviceState(int id) throws RemoteException;
    
    // Toggles all devices off	
    void turnAllOff() throws RemoteException;
    
    // Toggles all devices off	
    void turnAllOn() throws RemoteException;
    
    // Allows editing the name of a device
    void updateDeviceName(int id, String newName) throws RemoteException;
    
    // Retrieves the current list of devices to display in the GUI
    ArrayList<IDevice> getAllDevices() throws RemoteException;
}