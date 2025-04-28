package OTS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class MovieListFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTable movieTable;
    JButton bookBtn;
    int userId;

    public MovieListFrame(int userId) {
        this.userId = userId;

        setTitle("Movies & Shows");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        movieTable = new JTable();
        JScrollPane scroll = new JScrollPane(movieTable);
        scroll.setBounds(20, 20, 550, 250);
        add(scroll);

        bookBtn = new JButton("Book Ticket");
        bookBtn.setBounds(220, 290, 140, 30);
        add(bookBtn);

        loadData();

        bookBtn.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow != -1) {
                int showId = (int) movieTable.getValueAt(selectedRow, 0);
                int availableSeats = (int) movieTable.getValueAt(selectedRow, 5);
                this.dispose();
                new BookingFrame(userId, showId, availableSeats);
            }
        });

        setVisible(true);
    }

    private void loadData() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[] {
            "Show ID", "Movie Title", "Genre", "Duration", "Show Time", "Available Seats"
        });

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT s.show_id, m.title, m.genre, m.duration, s.show_time, s.available_seats " +
                 "FROM shows s JOIN movies m ON s.movie_id = m.movie_id ORDER BY s.show_id ASC")
        ) {
            // Store rows temporarily
            java.util.List<Object[]> rows = new java.util.ArrayList<>();
            while (rs.next()) {
                rows.add(new Object[] {
                    rs.getInt("show_id"),
                    rs.getString("title"),
                    rs.getString("genre"),
                    rs.getInt("duration"),
                    rs.getString("show_time"),
                    rs.getInt("available_seats")
                });
            }

            // Shuffle all columns except show_id (preserving order)
            java.util.Collections.shuffle(rows, new java.util.Random());

            // Sort by show_id again but inject shuffled movie data
            rows.sort(java.util.Comparator.comparingInt(row -> (int) row[0]));

            for (Object[] row : rows) {
                model.addRow(row);
            }

            movieTable.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data");
        }
    }
}
