import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;

public class RegisterForm extends JDialog {
    private JTextField tfName;
    private JTextField tfFirstname;
    private JTextField tfAdresse;
    private JTextField tfNumero;
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JButton btnConnection;
    private JPanel registerPanel;
    private JLabel lbPhoto;
    private JButton btnPhotographer;

    private String photoPath; // Nouveau champ pour le chemin de la photo
    public User user;

    public RegisterForm(JFrame parent) {
        super(parent);
        setTitle("Créé Un Compte");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(1134, 615));
        setSize(1134, 615);
        setResizable(false);
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterUser(parent);
            }
        });

        btnConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginForm(parent);
            }
        });

        // Ajouter une photo
        btnPhotographer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPhoto();
            }
        });

        setVisible(true);
    }
    private void selectPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            photoPath = selectedFile.getAbsolutePath();
            ImageIcon imageIcon = new ImageIcon(photoPath);
            Image image = imageIcon.getImage();
            Image resizedImage = image.getScaledInstance(lbPhoto.getWidth(), lbPhoto.getHeight(), Image.SCALE_SMOOTH);
            lbPhoto.setIcon(new ImageIcon(resizedImage));
        } else {
            System.out.println("Aucun fichier sélectionné.");
        }
    }

    private void RegisterUser(JFrame parent) {
        String nom = tfName.getText();
        String prenom = tfFirstname.getText();
        String adresse = tfAdresse.getText();
        String numero = tfNumero.getText();
        String mail = tfEmail.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());

        if (nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty() || numero.isEmpty() || mail.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || photoPath == null) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tout les Champs et Sélectionnez une Photo", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Les Mots de passe ne correcpondent pas", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        user = addUserToDatabase(nom, prenom, adresse, numero, mail, password, photoPath);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Inscription Réussis", "Succès", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginForm(parent);
        } else {
            JOptionPane.showMessageDialog(this, "Echec de l'inscription", "Attention", JOptionPane.ERROR_MESSAGE);
        }
    }

    private User addUserToDatabase(String nom, String prenom, String adresse, String numero, String mail, String password, String photoPath) {
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost/UTA_Bibliotheque?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO administrateur (Nom, Prénom, Adresse, Numéro, Mail, Mot_de_Passe, Photo) VALUES (?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, prenom);
            preparedStatement.setString(3, adresse);
            preparedStatement.setString(4, numero);
            preparedStatement.setString(5, mail);
            preparedStatement.setString(6, password);
            preparedStatement.setString(7, photoPath); // Enregistrement du chemin de la photo

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                user = new User();
                user.nom = nom;
                user.prenom = prenom;
                user.adresse = adresse;
                user.numero = numero;
                user.Mail = mail;
                user.motDePasse = password;
                user.photoPath = photoPath;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    public static void main(String[] args) {
        new RegisterForm(null);
    }
}
