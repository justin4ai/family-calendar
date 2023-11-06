import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DateBox extends JPanel {
    String day;
    Color color;
    int width;
    int height;
    String month;

    public DateBox(String day, Color color, int width, int height) {
        this.day = day;
        this.color = color;
        this.width = width;
        this.height = height;
        this.month = "";
        setPreferredSize(new Dimension(width, height));

        // Initialize an empty dialog
        JDialog dialog = new JDialog();

        dialog.setSize(300, 200); // Set the size of the dialog (adjust as needed)
        dialog.setLocationRelativeTo(this); // Position the dialog relative to the DateBox
        dialog.setModal(true); // Make the dialog modal, blocking interaction with the parent window

		JPanel eventListPanel = new JPanel();
        eventListPanel.setLayout(new BoxLayout(eventListPanel, BoxLayout.Y_AXIS));


////////////////
		JPanel eventPanel1 = new JPanel();
        eventPanel1.setLayout(new BoxLayout(eventPanel1, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Event Name: ");
        JLabel startTimeLabel = new JLabel("Start Time: ");
        JLabel endTimeLabel = new JLabel("End Time: ");
        JLabel locationLabel = new JLabel("Location: ");
        JLabel descriptionLabel = new JLabel("Description: ");

        eventPanel1.add(nameLabel);
        eventPanel1.add(startTimeLabel);
        eventPanel1.add(endTimeLabel);
        eventPanel1.add(locationLabel);
        eventPanel1.add(descriptionLabel);

        eventListPanel.add(eventPanel1);
        eventListPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing between events



		JPanel eventPanel2 = new JPanel();
        eventPanel2.setLayout(new BoxLayout(eventPanel2, BoxLayout.Y_AXIS));

        JLabel nameLabel2 = new JLabel("Event Name: ");
        JLabel startTimeLabel2 = new JLabel("Start Time: ");
        JLabel endTimeLabel2 = new JLabel("End Time: ");
        JLabel locationLabel2 = new JLabel("Location: ");
        JLabel descriptionLabel2 = new JLabel("Description: ");

        eventPanel2.add(nameLabel2);
        eventPanel2.add(startTimeLabel2);
        eventPanel2.add(endTimeLabel2);
        eventPanel2.add(locationLabel2);
        eventPanel2.add(descriptionLabel2);

        eventListPanel.add(eventPanel2);
        eventListPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing between events
		JScrollPane scrollPane = new JScrollPane(eventListPanel);
////////////////
		
		dialog.add(scrollPane);
		

        // Add a mouse listener to open the dialog when clicked
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dialog.setTitle(String.format("%s.%s", month, day)); // API extracting month and day will be updated
                dialog.setVisible(true); // Open the dialog when the panel is clicked
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(color);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.yellow);
        g.drawString(day, 10, 20);
    }
}
