import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Prijava {

    private JFrame window;
    private Container container;
    private JLabel mainTitle;
    private JLabel emailLabel;
    private JTextField emailField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public Prijava() {
        window = new JFrame("Prijava"); // Ustvarimo novo okno
        window.setPreferredSize(new Dimension(1024, 768)); // Nastavimo velikost okna
        window.setBounds(10, 10, 1024, 768); // Nastavimo pozicijo in velikost okna
        window.setLayout(new BorderLayout()); // Nastavimo postavitev okna
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Nastavimo akcijo ob zaprtju okna
        window.setLocationRelativeTo(null); // Nastavimo pozicijo okna na sredino
        window.setResizable(false); // Omogočimo spreminjanje velikosti okna

        container = window.getContentPane(); // Ustvarimo nov container
        container.setLayout(null); // Nastavimo postavitev panela

        mainTitle = new JLabel("Prijava"); // Ustvarimo nov label
        mainTitle.setFont(new Font("Arial", Font.BOLD, 48)); // Nastavimo velikost in obliko pisave
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT); // Nastavimo poravnavo
        mainTitle.setBounds(10, 50, 1004, 50); // Nastavimo pozicijo in velikost
        container.add(mainTitle); // Dodamo label v panel

        emailLabel = new JLabel("Elektronski naslov:"); // Ustvarimo nov label
        emailLabel.setBounds(10, 150, 1004, 40); // Nastavimo pozicijo in velikost
        container.add(emailLabel); // Dodamo label v panel

        emailField = new JTextField(); // Ustvarimo nov textfield
        emailField.setBounds(10, 190, 1004, 40); // Nastavimo pozicijo in velikost
        container.add(emailField); // Dodamo textfield v panel

        passwordLabel = new JLabel("Geslo:"); // Ustvarimo nov label
        passwordLabel.setBounds(10, 240, 1004, 40); // Nastavimo pozicijo in velikost
        container.add(passwordLabel); // Dodamo label v panel

        passwordField = new JPasswordField(); // Ustvarimo nov textfield
        passwordField.setBounds(10, 280, 1004, 40); // Nastavimo pozicijo in velikost
        container.add(passwordField); // Dodamo textfield v panel

        loginButton = new JButton("Prijavi se"); // Ustvarimo nov gumb
        loginButton.setBounds(10, 330, 1004, 40); // Nastavimo pozicijo in velikost
        loginButton.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed();
            }
        }); // Dodamo listener na gumb
        container.add(loginButton); // Dodamo gumb v panel
    }

    private void loginButtonActionPerformed() {
        String email = emailField.getText(); // Preberemo vrednost iz textfielda
        String password = new String(passwordField.getPassword()); // Preberemo vrednost iz textfielda
        try {
            Baza database = Baza.getInstance(); // Ustvarimo povezavo na bazo
            ResultSet result1 = database.executeQuery("SELECT * FROM \"Admin\" WHERE email = '" + email + "';"); // Izvedemo poizvedbo
            ResultSet result2 = database.executeQuery("SELECT * FROM \"Starsi\" WHERE email = '" + email + "';"); // Izvedemo poizvedbo
            ResultSet result3 = database.executeQuery("SELECT * FROM \"Ucitelji\" WHERE email = '" + email + "';"); // Izvedemo poizvedbo
            if (result1.next() && PasswordUtil.checkPassword(password, result1.getString("geslo"))) { // Če je uporabnik najden
                JOptionPane.showMessageDialog(window, "Uspešno ste se prijavili!"); // Izpišemo sporočilo
                Skladisce.getInstance().setUporabnikId(result1.getInt("id"));
                Skladisce.getInstance().setTipUporabnika(TipUporabnika.ADMINISTRATOR);
                ZacetnaStran zacetnaStran = new ZacetnaStran(); // Ustvarimo novo okno
                zacetnaStran.show(); // Pokažemo novo okno
                window.dispose(); // Zapremo trenutno okno
            } else if (result2.next() && PasswordUtil.checkPassword(password, result2.getString("geslo"))) { // Če je uporabnik najden
                JOptionPane.showMessageDialog(window, "Uspešno ste se prijavili!"); // Izpišemo sporočilo
                Skladisce.getInstance().setUporabnikId(result2.getInt("id"));
                Skladisce.getInstance().setTipUporabnika(TipUporabnika.STARS);
                ZacetnaStran zacetnaStran = new ZacetnaStran(); // Ustvarimo novo okno
                zacetnaStran.show(); // Pokažemo novo okno
                window.dispose(); // Zapremo trenutno okno
            } else if (result3.next() && PasswordUtil.checkPassword(password, result3.getString("geslo"))) { // Če je uporabnik najden
                JOptionPane.showMessageDialog(window, "Uspešno ste se prijavili!"); // Izpišemo sporočilo
                Skladisce.getInstance().setUporabnikId(result3.getInt("id"));
                Skladisce.getInstance().setTipUporabnika(TipUporabnika.UCITELJ);
                ZacetnaStran zacetnaStran = new ZacetnaStran(); // Ustvarimo novo okno
                zacetnaStran.show(); // Pokažemo novo okno
                window.dispose(); // Zapremo trenutno okno
            } else { // Če uporabnik ni najden
                JOptionPane.showMessageDialog(window, "Napačen email ali geslo!"); // Izpišemo sporočilo
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(window, "Napaka pri prijavi!"); // Izpišemo sporočilo
        }
    }

    public void show() {
        window.setVisible(true); // Naredimo okno vidno
    }
}
