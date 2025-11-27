import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.util.ArrayList;

public class SmartHomeGUI extends JFrame 
{
    private static final long serialVersionUID = 1L;
    
    // RMI Server Reference
    private ISmartHomeService server;
    
    // UI Components
    private JPanel gridPanel; 
    private JTextField nameField;
    private JComboBox<String> typeBox;
    
    public SmartHomeGUI() 
    {
        super("Smart Home Hub - Remote Dashboard");
        
        // Setup RMI connection
        connectToServer();
        
        // Window Setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600); // Made it slightly wider to accommodate sidebar
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        
        // Sidebar container
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(180, 0)); // Fixed width of 220px
        sidebarPanel.setBorder(BorderFactory.createTitledBorder("Control Panel"));
        
        // Inner panel to stack the inputs and controls
        JPanel inputsPanel = new JPanel(new GridLayout(0, 1, 5, 10)); // 1 Column, Auto rows, 5px gap
        inputsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        // Initialize Components
        nameField = new JTextField();
        String[] types = {"Light", "Thermostat", "Camera"}; 
        typeBox = new JComboBox<>(types);
        
        JButton addButton = new JButton("Add Device");
        JButton turnAllOffButton = new JButton("Turn all devices OFF");
        JButton turnAllOnButton = new JButton("Turn all devices ON");
        JButton refreshButton = new JButton("Force Refresh"); 
        
        // Add components to the control panel
        inputsPanel.add(new JLabel("Device Name:"));
        inputsPanel.add(nameField);
        inputsPanel.add(new JLabel("Device Type:"));
        inputsPanel.add(typeBox);
        inputsPanel.add(Box.createVerticalStrut(10)); // Spacer
        inputsPanel.add(addButton); 
        inputsPanel.add(turnAllOffButton);
        inputsPanel.add(turnAllOnButton);
        inputsPanel.add(refreshButton);
        
        // Add the input stack to the top of the sidebar
        // This is done so the buttons don't stretch to fill the whole height of the window
        sidebarPanel.add(inputsPanel, BorderLayout.NORTH);

        
        // Grid Panel for displaying devices
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 3 Columns
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add Panels to Main Frame
        add(sidebarPanel, BorderLayout.WEST); // Sidebar on the Left
        add(scrollPane, BorderLayout.CENTER); // Grid in the Middle
        
        // Action Listeners
        addButton.addActionListener(e -> addDevice());
        refreshButton.addActionListener(e -> refreshDisplay());
        turnAllOffButton.addActionListener(e -> turnAllOff());
        turnAllOnButton.addActionListener(e -> turnAllOn());
        
        // Initial load
        refreshDisplay();
        setVisible(true);
    }
    
    // --- RMI Connection ---
    private void connectToServer() 
    {
        try {
            server = (ISmartHomeService)Naming.lookup("rmi://localhost/SmartHomeService");
            System.out.println("Connected to Smart Home Server.");
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Could not connect to the Hub server.\nCheck if SmartHomeServer is running.");
            System.exit(1);
        }   
    }

    // --- Logic Methods ---
    private void addDevice() {
        String name = nameField.getText().trim();
        String type = (String)typeBox.getSelectedItem();
        
        if(name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a device name. ");
            return;
        }
        
        try {
            server.addNewDevice(type, name); 
            nameField.setText(""); 
            refreshDisplay(); 
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private void turnAllOff() {
        try {
            server.turnAllOff(); 
            refreshDisplay();    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void turnAllOn() {
        try {
            server.turnAllOn(); 
            refreshDisplay();    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void refreshDisplay() {
        try {
            ArrayList<IDevice> devices = server.getAllDevices();
            gridPanel.removeAll();
            for (IDevice d : devices) {
                gridPanel.add(new DeviceCard(d));
            }
            gridPanel.revalidate();
            gridPanel.repaint();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    // --- Device Card Class ---
    private class DeviceCard extends JPanel {
        public DeviceCard(IDevice device) {
            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            
            if (device.isOn()) {
                if(device.getType().equalsIgnoreCase("Light")) setBackground(new Color(255, 255, 153)); 
                else if (device.getType().equalsIgnoreCase("Thermostat")) setBackground(new Color(144, 238, 144)); 
                else if (device.getType().equalsIgnoreCase("Camera")) setBackground(new Color(255, 102, 102)); 
                else setBackground(Color.GREEN); 
            } else { 
                setBackground(Color.LIGHT_GRAY); 
            }

            JLabel nameLabel = new JLabel(device.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            JLabel statusLabel = new JLabel(device.getStatusString(), SwingConstants.CENTER);
            JLabel typeLabel = new JLabel(device.getType(), SwingConstants.CENTER);
            typeLabel.setFont(new Font("Arial", Font.ITALIC, 12));

            JPanel infoPanel = new JPanel(new GridLayout(3, 1));
            infoPanel.setOpaque(false);
            infoPanel.add(nameLabel);
            infoPanel.add(typeLabel);
            infoPanel.add(statusLabel);

            JPanel btnPanel = new JPanel(new FlowLayout());
            btnPanel.setOpaque(false);
            
            JButton toggleBtn = new JButton(device.isOn() ? "OFF" : "ON");
            JButton editBtn = new JButton("Edit");
            JButton deleteBtn = new JButton("Remove");
            
            btnPanel.add(toggleBtn);
            btnPanel.add(editBtn);
            btnPanel.add(deleteBtn);

            add(infoPanel, BorderLayout.CENTER);
            add(btnPanel, BorderLayout.SOUTH);

            toggleBtn.addActionListener(e -> {
                try { server.toggleDeviceState(device.getId()); refreshDisplay(); } 
                catch (Exception ex) { ex.printStackTrace(); }
            });
            
            editBtn.addActionListener(e -> {
                // Show a simple Input Dialog asking for the new name
                String newName = JOptionPane.showInputDialog(
                        SmartHomeGUI.this, 
                        "Enter new name for " + device.getName() + ":", 
                        device.getName()
                );
                
                // If user didn't cancel and didn't leave it empty
                if (newName != null && !newName.trim().isEmpty()) {
                    try {
                        // 3. Call the server
                        server.updateDeviceName(device.getId(), newName.trim());
                        // 4. Refresh to see the change
                        refreshDisplay();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            deleteBtn.addActionListener(e -> {
                try { server.removeDevice(device.getId()); refreshDisplay(); } 
                catch (Exception ex) { ex.printStackTrace(); }
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmartHomeGUI());
    }
}