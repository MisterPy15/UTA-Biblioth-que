import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog {
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton connectionButton;
    private JButton btnInscription;
    private JPanel loginPanel;
    public User user;

    public LoginForm(JFrame parent) {
        super(parent);
        setTitle("Authentification");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(815, 415));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        connectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());

                user = getAuthentificateUser(email, password);

                if (user != null) {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Connexion réussie, bon retour " + user.nom + " " + user.prenom,
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new DashoardForm(user); // Passer l'utilisateur authentifié au Dashboard
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Email ou mot de passe incorrect",
                            "Attention",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnInscription.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterForm(parent);
            }
        });

        setVisible(true);
    }

    private User getAuthentificateUser(String email, String password) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/UTA_Bibliotheque?serverTimeZone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM administrateur WHERE Mail=? AND Mot_de_Passe=?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user = new User();
                        user.nom = resultSet.getString("Nom");
                        user.prenom = resultSet.getString("Prénom");
                        user.adresse = resultSet.getString("Adresse");
                        user.numero = resultSet.getString("Numéro");
                        user.Mail = resultSet.getString("Mail");
                        user.motDePasse = resultSet.getString("Mot_de_Passe");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        return user;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm(null);
            }
        });
    }
}
