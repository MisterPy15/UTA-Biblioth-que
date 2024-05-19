import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegisterForm extends JDialog{
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


    public RegisterForm(JFrame parent){
        super(parent);
        setTitle("Créé Un Compte");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(825, 660));
        setModal(true);
        setLocationRelativeTo(parent);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterUser();
            }
        });
        btnConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginForm(parent);
            }
        });

        setVisible(true);
    }

    private void RegisterUser() {
        String nom = tfName.getText();
        String prenom = tfFirstname.getText();
        String adresse = tfAdresse.getText();
        String numero = tfNumero.getText();
        String mail = tfEmail.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String ConfirmPassword = String.valueOf(pfConfirmPassword.getPassword());


        if (nom.isEmpty()|| prenom.isEmpty() || adresse.isEmpty() || numero.isEmpty() || mail.isEmpty() || password.isEmpty() || ConfirmPassword.isEmpty()){

            JOptionPane.showMessageDialog(this,
                                        "Svp Remplissez Tout les Champs",
                                        "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(ConfirmPassword)){

            JOptionPane.showMessageDialog(this,
                    "Les Mots de passe ne correcpondent pas",
                    "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }

        user = addUserToDatabase(nom, prenom, adresse, numero, mail, password);

        if(user != null){
            dispose();
        }else {
            JOptionPane.showMessageDialog(this,
                    "Echec de l'inscription",
                    "Attention", JOptionPane.ERROR_MESSAGE);
        }
    }

    public User user;
    private User addUserToDatabase(String nom, String prenom, String adresse, String numero, String mail, String password){
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/UTA_Bibliotheque?serverTimeZone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";


        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO administrateur(Nom, Prénom, Adresse, Numéro, Mail, Mot_de_Passe)" + "VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement prepareStatement = conn.prepareStatement(sql);
            prepareStatement.setString(1, nom);
            prepareStatement.setString(2, prenom);
            prepareStatement.setString(3, adresse);
            prepareStatement.setString(4, numero);
            prepareStatement.setString(5, mail);
            prepareStatement.setString(6, password);

            int addedRows = prepareStatement.executeUpdate();
            if (addedRows > 0){
                user = new User();
                user.nom = nom;
                user.prenom = prenom;
                user.adresse = adresse;
                user.numero = numero;
                user.Mail = mail;
                user.motDePasse = password;
            }

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }




//    public static void main(String[] args) {
//        RegisterForm myForm = new RegisterForm(null);
//        User user = myForm.user;
//        myForm.setLocationRelativeTo(null);
//
//        if (user != null){
//            JOptionPane.showMessageDialog(myForm,
//                    "Enregistrement réussi ✅ \n" + user.nom + " " + user.prenom,
//                    "Succès", JOptionPane.ERROR_MESSAGE);
//        }
//    }
}
