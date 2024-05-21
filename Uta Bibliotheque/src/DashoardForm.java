import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashoardForm extends JFrame {
    private JButton btnAdherent;
    private JButton btnReservations;
    private JButton btnEmprunt;
    private JButton btnStock;
    private JButton btnLivre;
    private JButton btnDeconnection;
    private JPanel dashboardPanel;
    private JLabel lbAdmin;
    private JLabel lbNbrLivre;
    private JLabel lbNbrAdherent;
    private JLabel lblNbrreserve;
    private JLabel lbNbrEmprunt;
    private JLabel lbNbrRetour;
    private JLabel lblNbrNonRetour;
    private JPanel chartPanel;

    public DashoardForm() {
        // Default constructor
    }

    public DashoardForm(User user) {
        setTitle("Dashboard");
        setContentPane(dashboardPanel);
        setMinimumSize(new Dimension(825, 660));
        setSize(1200, 700);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        if (user != null) {
            lbAdmin.setText("Admin : " + user.nom + " " + user.prenom);
            setLocationRelativeTo(null);
            setVisible(true);
        } else {
            dispose();
        }

        btnDeconnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(dashboardPanel);
                new LoginForm(parent);
            }
        });




    }

    private boolean connectToDatabase() {
        boolean hasRegisterUser = false;

        final String MYSQL_SERVER_URL = "jdbc:mysql://localhost/";
        final String DB_URL = "jdbc:mysql://localhost/UTA_Bibliotheque?serverTimeZone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS UTA_Bibliotheque");

            statement.close();
            conn.close();

            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS administrateur("
                    + "Id_Admin INT(255) NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    + "Nom varchar(60) NOT NULL,"
                    + "Prénom varchar(60) NOT NULL,"
                    + "Adresse varchar(60) NOT NULL,"
                    + "Numéro varchar(20) DEFAULT NULL,"
                    + "Mail varchar(60) NOT NULL,"
                    + "Mot_de_Passe varchar(20) NOT NULL,"
                    + "Photo varchar(255) DEFAULT 'default.jpg'"
                    + ")";
            statement.executeUpdate(sql);

            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM administrateur");

            if (resultSet.next()) {
                int numUsers = resultSet.getInt(1);
                if (numUsers > 0) {
                    hasRegisterUser = true;
                }
            }

            statement.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasRegisterUser;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DashoardForm(null); // For testing purposes, no user is passed
            }
        });
    }
}
