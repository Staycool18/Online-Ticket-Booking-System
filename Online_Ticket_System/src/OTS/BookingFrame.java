package OTS;

import javax.swing.*;
import java.sql.*;

public class BookingFrame extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JTextField seatsField;
    JButton confirmBtn;
    int userId, showId, availableSeats;

    public BookingFrame(int userId, int showId, int availableSeats) {
        this.userId = userId;
        this.showId = showId;
        this.availableSeats = availableSeats;

        setTitle("Book Tickets");
        setSize(300, 180);
        setLocationRelativeTo(null);
        setVisible(true);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel seatLabel = new JLabel("Seats to Book:");
        seatLabel.setBounds(30, 30, 100, 25);
        add(seatLabel);

        seatsField = new JTextField();
        seatsField.setBounds(140, 30, 100, 25);
        add(seatsField);

        confirmBtn = new JButton("Confirm Booking");
        confirmBtn.setBounds(70, 80, 150, 30);
        add(confirmBtn);

        confirmBtn.addActionListener(e -> bookTickets());

        setVisible(true);
    }

    private void bookTickets() {
        int seats;
        try {
            seats = Integer.parseInt(seatsField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            return;
        }

        if (seats <= 0 || seats > availableSeats) {
            JOptionPane.showMessageDialog(this, "Invalid seat number.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);  // Start transaction

            // Insert booking into the bookings table
            PreparedStatement insertBooking = conn.prepareStatement(
                    "INSERT INTO bookings (user_id, show_id, seats_booked, booking_time) VALUES (?, ?, ?, NOW())");
            insertBooking.setInt(1, userId);
            insertBooking.setInt(2, showId);
            insertBooking.setInt(3, seats);
            insertBooking.executeUpdate();

            // Update available seats in the shows table
            PreparedStatement updateSeats = conn.prepareStatement(
                    "UPDATE shows SET available_seats = available_seats - ? WHERE show_id = ?");
            updateSeats.setInt(1, seats);
            updateSeats.setInt(2, showId);
            updateSeats.executeUpdate();

            conn.commit();  // Commit the transaction
            JOptionPane.showMessageDialog(this, "Booking Successful!");

            // Get movie and show details to display on ticket
            PreparedStatement ticketStmt = conn.prepareStatement(
                    "SELECT m.title, s.show_time FROM shows s JOIN movies m ON s.movie_id = m.movie_id WHERE s.show_id = ?");
            ticketStmt.setInt(1, showId);
            ResultSet rs = ticketStmt.executeQuery();

            if (rs.next()) {
                String movieTitle = rs.getString("title");
                String showTime = rs.getString("show_time");

                String ticket = "🎟️ Ticket Details\n\n"
                              + "Movie: " + movieTitle + "\n"
                              + "Show Time: " + showTime + "\n"
                              + "Seats Booked: " + seats + "\n"
                              + "User ID: " + userId;

                JOptionPane.showMessageDialog(this, ticket, "Your Ticket", JOptionPane.INFORMATION_MESSAGE);
            }

            this.dispose();  // Close the booking frame
            new MovieListFrame(userId);  // Open the movie list frame

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during booking: " + e.getMessage());
        }
    }
}
