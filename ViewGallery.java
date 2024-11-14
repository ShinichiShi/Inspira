package project2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.*;

public class ViewGallery extends JFrame {
    String username;

    public ViewGallery(String username) {
        this.username = username;
        setTitle("Virtual Art Gallery");
        setLayout(new BorderLayout());

        // Load background image
        URL imgURL = getClass().getResource("resources1/gallery_bg1.jpg");
        ImageIcon backgroundImage = null;

        if (imgURL == null) {
            System.out.println("Background image resource not found! Please check the path.");
            setBackground(Color.BLACK);
        } else {
            backgroundImage = new ImageIcon(imgURL);
        }

        // Create a panel for the background
        JLabel bgLabel = new JLabel();
        if (backgroundImage != null) {
            bgLabel.setIcon(backgroundImage);
            bgLabel.setLayout(new BorderLayout()); // Use BorderLayout for bgLabel
        } else {
            bgLabel.setBackground(Color.WHITE);
            bgLabel.setOpaque(true);
        }

        // Create scrollable panel to display artworks
        JPanel artPanel = new JPanel();
        artPanel.setLayout(new BoxLayout(artPanel, BoxLayout.Y_AXIS)); // Stack artworks vertically
        artPanel.setOpaque(false); // Transparent panel
        artPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the panel

        // Create a JScrollPane for the artPanel
        JScrollPane scrollPane = new JScrollPane(artPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border

        // Customizing scroll bar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(30, 0)); // Increase width
        verticalScrollBar.setUnitIncrement(20); // Increase scroll speed

        // Add the scrollPane to the background label
        bgLabel.add(scrollPane, BorderLayout.CENTER);
        add(bgLabel, BorderLayout.CENTER);

        // Fetch artworks from the database
        fetchArtworks(artPanel);

        // Title Label with underline and different font
JLabel titleLabel = new JLabel("GALLERY OF MASTERS");
titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 45)); // Set font to Comic Sans MS, bold, size 40
titleLabel.setForeground(Color.black); // Set text color
titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the title
bgLabel.add(titleLabel, BorderLayout.NORTH); // Add title at the top of bgLabel

// Add underline effect
titleLabel.setText("<html><u>" + titleLabel.getText() + "</u></html>"); // Underline the text



        // Modern Back button
        JButton backButton = new JButton("Back");
        styleButton(backButton, new Color(70, 130, 180));
        backButton.addActionListener(e -> {
            new Home(username).setVisible(true);
            setVisible(false);
        });

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Make button panel transparent
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH); // Position at the bottom

        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void styleButton(JButton button, Color color) {
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(color); // Set button background color
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor
    }

    private void fetchArtworks(JPanel artPanel) {
        try (Conn c = new Conn();
             Connection conn = c.getConnection();
             Statement stmt = conn.createStatement()) {

            // Fetch artworks from the database
            String query = "SELECT * FROM artwork";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String title = rs.getString("title");
                String medium = rs.getString("medium");
                String price = rs.getString("price");
                String imagePath = rs.getString("image");

                // Use absolute path for the image
                ImageIcon artworkImage;
                try {
                    artworkImage = new ImageIcon(imagePath);
                    System.out.println(imagePath);

                } catch (Exception ex) {
                    System.out.println("Error loading image: " + imagePath);
                    continue; // Skip artwork if image can't be loaded
                }

                // Resize image for display
                Image scaledImage = artworkImage.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                // Panel for artwork details
                JPanel artworkPanel = new JPanel();
                artworkPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                artworkPanel.setOpaque(false);

                // Details inside a small black box
                JPanel detailsPanel = new JPanel();
                detailsPanel.setBackground(new Color(0, 0, 0, 200));
                detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
                detailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                JLabel artLabel = new JLabel("<html><font color='white' size='5'><b>Title:</b> " + title + "<br><b>Medium:</b> " + medium + "<br><b>Price:</b> " + price + "</font></html>");
                artLabel.setForeground(Color.WHITE);
                artLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                detailsPanel.add(artLabel);
                artworkPanel.add(detailsPanel);
                artworkPanel.add(imageLabel);

                // Hover effect for preview
                imageLabel.addMouseListener(new MouseAdapter() {
                    JDialog previewDialog = null;

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        System.out.println("Mouse entered the image!");
                        previewDialog = new JDialog(ViewGallery.this, "Artwork Preview", true);
                        previewDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        previewDialog.setLayout(new BorderLayout());

                        // Full-size image preview
                        Image fullSizeScaledImage = artworkImage.getImage().getScaledInstance(600, 600, Image.SCALE_SMOOTH);
                        JLabel previewLabel = new JLabel(new ImageIcon(fullSizeScaledImage));
                        previewDialog.add(previewLabel, BorderLayout.CENTER);

                        // Full-screen button
                        JButton maximizeButton = new JButton("Full Screen");
                        styleButton(maximizeButton, new Color(34, 139, 34));
                        maximizeButton.addActionListener(e1 -> showFullScreen(artworkImage));

                        previewDialog.add(maximizeButton, BorderLayout.SOUTH);
                        previewDialog.pack();
                        previewDialog.setLocationRelativeTo(ViewGallery.this);
                        previewDialog.setVisible(true);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (previewDialog != null) {
                            previewDialog.dispose();
                        }
                    }
                });

                artPanel.add(artworkPanel);
            }
            artPanel.revalidate();
            artPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showFullScreen(ImageIcon artworkImage) {
        JDialog fullScreenDialog = new JDialog(ViewGallery.this, "Full Screen", true);
        fullScreenDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        fullScreenDialog.setLayout(new BorderLayout());

        Image fullScreenImage = artworkImage.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH
        );

        JLabel fullScreenLabel = new JLabel(new ImageIcon(fullScreenImage));
        fullScreenDialog.add(fullScreenLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        styleButton(closeButton, new Color(220, 20, 60));
        closeButton.addActionListener(e -> fullScreenDialog.dispose());

        fullScreenDialog.add(closeButton, BorderLayout.SOUTH);
        fullScreenDialog.setUndecorated(true);
        fullScreenDialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        fullScreenDialog.setLocationRelativeTo(null);
        fullScreenDialog.setVisible(true);
    }


    public static void main(String[] args) {
        new ViewGallery("user1");
    }
}
