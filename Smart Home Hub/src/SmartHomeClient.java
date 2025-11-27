import javax.swing.*;
import java.awt.*;
import java.rmi.*;
import java.util.ArrayList;

/// The Client: The main dashboard window that connects to the RMI server to visualize and control smart devices

public class SmartHomeClient extends JFrame {
	private static final long serialVersionUID = 1L;

	// RMI Server Reference
	private ISmartHomeService server;

	// UI Components
	private JPanel gridPanel;
	private JTextField nameField;
	private JComboBox<String> typeBox;

	public SmartHomeClient() {
		super("Smart Home Hub - Remote Dashboard");

		// Setup RMI connection
		connectToServer();

		// Window Setup
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 600);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(10, 10));

		// Sidebar container
		JPanel sidebarPanel = new JPanel(new BorderLayout());
		sidebarPanel.setPreferredSize(new Dimension(180, 0));
		sidebarPanel.setBorder(BorderFactory.createTitledBorder("Control Panel"));

		// Inner panel to stack the inputs and controls
		JPanel inputsPanel = new JPanel(new GridLayout(0, 1, 5, 10)); // Auto rows, 1 Column, 5px gap
		inputsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		// Initialize Components
		nameField = new JTextField();
		String[] types = { "Light", "Thermostat", "Camera" };
		typeBox = new JComboBox<>(types);

		// Initialize buttons
		JButton addBtn = new JButton("Add Device");
		JButton turnAllOffBtn = new JButton("Turn all devices OFF");
		JButton turnAllOnBtn = new JButton("Turn all devices ON");
		JButton refreshBtn = new JButton("Force Refresh");

		// Add components to the control panel
		inputsPanel.add(new JLabel("Device Name:"));
		inputsPanel.add(nameField);
		inputsPanel.add(new JLabel("Device Type:"));
		inputsPanel.add(typeBox);
		inputsPanel.add(Box.createVerticalStrut(10)); // Spacer
		inputsPanel.add(addBtn);
		inputsPanel.add(turnAllOffBtn);
		inputsPanel.add(turnAllOnBtn);
		inputsPanel.add(refreshBtn);

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
		addBtn.addActionListener(e -> addDevice());
		turnAllOffBtn.addActionListener(e -> turnAllOff());
		turnAllOnBtn.addActionListener(e -> turnAllOn());
		refreshBtn.addActionListener(e -> refreshDisplay());

		// Initial load
		refreshDisplay();
		setVisible(true);
	}

	// RMI Connection
	private void connectToServer() {
		try {
			server = (ISmartHomeService) Naming.lookup("rmi://localhost/SmartHomeService");
			System.out.println("Connected to Smart Home Server.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
					"Could not connect to the Hub server.\nCheck if SmartHomeServer is running.");
			System.exit(1);
		}
	}

	
	// Logic Methods
	
	// Add a new device using the text field and ask the server to create a new device
	private void addDevice() {
		String name = nameField.getText().trim();
		String type = (String) typeBox.getSelectedItem();

		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter a device name.");
			return;
		}

		try {
			server.addNewDevice(type, name);
			nameField.setText("");
			refreshDisplay();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Turn off all devices in the list
	private void turnAllOff() {
		try {
			server.turnAllOff();
			refreshDisplay();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Turn on all devices in the list
	private void turnAllOn() {
		try {
			server.turnAllOn();
			refreshDisplay();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Fetch the latest list from the server and redraw the UI
	private void refreshDisplay() {
		try {
			// Current list of devices on the server
			ArrayList<IDevice> devices = server.getAllDevices();
			
			// Clean the grid
			gridPanel.removeAll();
			
			// Apply the DeviceCard class
			for (IDevice d : devices) {
				// Create a new instance of the inner class for this specific device
	            DeviceCard newCard = new DeviceCard(d); 
	            
	            // Add the visual panel to the grid container
	            gridPanel.add(newCard);
			}
			
			// Refresh & redraw components
			gridPanel.revalidate();
			gridPanel.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// Device Card Class
	
	/*
	 * Inner class representing a single device card in the grid.
	 * It's handy because it will help to encapsulate the visual logic for each device.
	*/
	private class DeviceCard extends JPanel {
		private static final long serialVersionUID = 1L;

		public DeviceCard(IDevice device) {
			setLayout(new BorderLayout(5, 5));
			setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
			
			// Set background colour based on device type and state
			if (device.isOn()) {
				if (device.getType().equalsIgnoreCase("Light"))setBackground(new Color(255, 255, 153));
				else if (device.getType().equalsIgnoreCase("Thermostat"))setBackground(new Color(144, 238, 144));
				else if (device.getType().equalsIgnoreCase("Camera"))setBackground(new Color(255, 102, 102));
				else setBackground(Color.GREEN);
			} 
			else setBackground(Color.LIGHT_GRAY);
			
			// Labels for device info
			JLabel nameLabel = new JLabel(device.getName(), SwingConstants.CENTER);
			nameLabel.setFont(new Font("Dialog", Font.BOLD, 16));
			JLabel typeLabel = new JLabel(device.getType(), SwingConstants.CENTER);
			typeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
			JLabel statusLabel = new JLabel(device.getStatusString(), SwingConstants.CENTER);

			// Info Panel
			JPanel infoPanel = new JPanel(new GridLayout(3, 1));
			infoPanel.setOpaque(false);
			infoPanel.add(nameLabel);
			infoPanel.add(typeLabel);
			infoPanel.add(statusLabel);

			// Control Buttons Panel
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

			
			// ActionListener mapping
			
			// Toggle State button
			toggleBtn.addActionListener(e -> {
				try {
					server.toggleDeviceState(device.getId());
					refreshDisplay();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});

			// Edit Device Name button
			editBtn.addActionListener(e -> {
				// Show an Input Dialog window asking for the new name
				String newName = JOptionPane.showInputDialog(SmartHomeClient.this,
						"Enter new name for " + device.getName() + ":", device.getName());

				// If user didn't cancel and didn't leave it empty
				if (newName != null && !newName.trim().isEmpty()) {
					try {
						// Call the server
						server.updateDeviceName(device.getId(), newName.trim());
						// Refresh to see the change
						refreshDisplay();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});

			// Delete Device button
			deleteBtn.addActionListener(e -> {
				try {
					server.removeDevice(device.getId());
					refreshDisplay();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		}
	}

	// Main entry for the Client part of the application
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new SmartHomeClient());
	}
}