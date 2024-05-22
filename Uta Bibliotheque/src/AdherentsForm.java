import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdherentsForm extends JFrame {
    private JTextField tfNom;
    private JTextField tfPrenom;
    private JTextField tfEmail;
    private JTable table1;
    private JButton btnCreer;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton rechercherButton;
    private JTextField tfRecherche;
    private JPanel dateOfBirthPanel;
    private JPanel registrationDatePanel;
    private JLabel lbPhoto;
    private JPanel AdherentPanel;
    private JButton btnAjouterPhoto;

    private JDateChooser dateOfBirthChooser;
    private JDateChooser registrationDateChooser;

    private Connection con;
    private PreparedStatement pst;
    private String photoPath; // Champ pour le chemin de la photo

    public AdherentsForm() {
        setTitle("Gestion Des Adherents");
        setContentPane(AdherentPanel);
        setMinimumSize(new Dimension(964, 741));
        setSize(964, 741);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Initialiser les JDateChooser
        dateOfBirthChooser = new JDateChooser();
        registrationDateChooser = new JDateChooser();

        // Ajouter les JDateChooser aux panneaux correspondants
        dateOfBirthPanel.setLayout(new BorderLayout());
        dateOfBirthPanel.add(dateOfBirthChooser, BorderLayout.CENTER);

        registrationDatePanel.setLayout(new BorderLayout());
        registrationDatePanel.add(registrationDateChooser, BorderLayout.CENTER);

        btnAjouterPhoto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPhoto();
            }
        });

        connect();
        btnCreer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAdherent();
            }
        });
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/UTA_Bibliotheque?useSSL=false&serverTimezone=UTC", "root", "");
            System.out.println("Succès");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void createAdherent() {
        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        Date dateNaiss = dateOfBirthChooser.getDate();
        String email = tfEmail.getText();
        Date dateInscription = registrationDateChooser.getDate();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || dateNaiss == null || dateInscription == null || photoPath == null) {
            JOptionPane.showMessageDialog(this, "Svp Remplissez Tous les Champs et Sélectionnez une Photo", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDateNaiss = dateFormat.format(dateNaiss);
        String formattedDateInscription = dateFormat.format(dateInscription);

        try {
            String query = "INSERT INTO adhérent (Nom, Prénom, Date_de_Naissance, Email, Date_d_Inscription, Photo) VALUES (?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, formattedDateNaiss);
            pst.setString(4, email);
            pst.setString(5, formattedDateInscription);
            pst.setString(6, photoPath); // Enregistrement du chemin de la photo
            pst.executeUpdate();


            JOptionPane.showMessageDialog(this,
                                            "Inscription Réussis",
                                        "Succès",
                                            JOptionPane.INFORMATION_MESSAGE);



        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
            JOptionPane.showMessageDialog(this,
                    "Aucune Photo Sélectionner",
                    "Attention",
                    JOptionPane.ERROR_MESSAGE);        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdherentsForm();
            }
        });
    }
}
