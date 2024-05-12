import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UciteljiForm {

    private JFrame window;
    private Container container;
    private JLabel mainTitle;
    private JLabel imeLabel;
    private JTextField imeField;
    private JLabel priimekLabel;
    private JTextField priimekField;
    private JLabel emailLabel;
    private JTextField emailField;
    private JLabel gesloLabel;
    private JTextField gesloField;
    private JButton shraniButton;
    private int uciteljId;

    public UciteljiForm(int uciteljId) {
        this.uciteljId = uciteljId;

        window = new JFrame("Učitelji Obrazec"); // Ustvarimo novo okno
        window.setPreferredSize(new Dimension(1024, 768)); // Nastavimo velikost okna
        window.setBounds(10, 10, 1024, 768); // Nastavimo pozicijo in velikost okna
        window.setLayout(null); // Nastavimo postavitev okna
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Nastavimo akcijo ob zaprtju okna
        window.setLocationRelativeTo(null); // Nastavimo pozicijo okna na sredino
        window.setResizable(false); // Omogočimo spreminjanje velikosti okna

        container = window.getContentPane(); // Ustvarimo nov container

        mainTitle = new JLabel("Učitelji Obrazec"); // Ustvarimo nov label
        mainTitle.setFont(new Font("Arial", Font.BOLD, 48)); // Nastavimo velikost in obliko pisave
        mainTitle.setBounds(10, 50, 1004, 50); // Nastavimo pozicijo in velikost
        container.add(mainTitle); // Dodamo label v container

        imeLabel = new JLabel("Ime učitelja:"); // Ustvarimo nov label
        imeLabel.setBounds(10, 150, 200, 40); // Nastavimo pozicijo in velikost
        container.add(imeLabel); // Dodamo label v container

        imeField = new JTextField(); // Ustvarimo nov textfield
        imeField.setBounds(220, 150, 200, 40); // Nastavimo pozicijo in velikost
        container.add(imeField); // Dodamo textfield v container

        priimekLabel = new JLabel("Priimek učitelja:"); // Ustvarimo nov label
        priimekLabel.setBounds(10, 200, 200, 40); // Nastavimo pozicijo in velikost
        container.add(priimekLabel); // Dodamo label v container

        priimekField = new JTextField(); // Ustvarimo nov textfield
        priimekField.setBounds(220, 200, 200, 40); // Nastavimo pozicijo in velikost
        container.add(priimekField); // Dodamo textfield v container

        emailLabel = new JLabel("Email učitelja:"); // Ustvarimo nov label
        emailLabel.setBounds(10, 250, 200, 40); // Nastavimo pozicijo in velikost
        container.add(emailLabel); // Dodamo label v container

        emailField = new JTextField(); // Ustvarimo nov textfield
        emailField.setBounds(220, 250, 200, 40); // Nastavimo pozicijo in velikost
        container.add(emailField); // Dodamo textfield v container

        gesloLabel = new JLabel("Geslo učitelja:"); // Ustvarimo nov label
        gesloLabel.setBounds(10, 300, 200, 40); // Nastavimo pozicijo in velikost
        container.add(gesloLabel); // Dodamo label v container

        gesloField = new JTextField(); // Ustvarimo nov textfield
        gesloField.setBounds(220, 300, 200, 40); // Nastavimo pozicijo in velikost
        container.add(gesloField); // Dodamo textfield v container

        shraniButton = new JButton("Shrani"); // Ustvarimo nov gumb
        shraniButton.setBounds(10, 450, 100, 40); // Nastavimo pozicijo in velikost
        shraniButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shraniUcitelja();
            }
        });
        container.add(shraniButton); // Dodamo gumb v container

        window.setVisible(true); // Naredimo okno vidno

        if (uciteljId != 0) {
            try {
                Baza db = Baza.getInstance();
                String query = "SELECT * FROM \"Ucitelji\" WHERE \"id\" = " + uciteljId + ";";
                ResultSet resultSet = db.executeQuery(query);
                if (resultSet.next()) {
                    imeField.setText(resultSet.getString("ime"));
                    priimekField.setText(resultSet.getString("priimek"));
                    emailField.setText(resultSet.getString("email"));
                    gesloField.setText(resultSet.getString("geslo"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void shraniUcitelja() {
        String ime = imeField.getText();
        String priimek = priimekField.getText();
        String email = emailField.getText();
        String geslo = gesloField.getText();

        if (ime.isEmpty() || priimek.isEmpty() || email.isEmpty() || geslo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vsa polja morajo biti izpolnjena.", "Napaka", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Baza db = Baza.getInstance();

            if (uciteljId <= 0) {
//               CREATE OR REPLACE FUNCTION dodaj_ucitelja(p_ime character varying, p_priimek character varying, p_email character varying, p_geslo character varying)
                String query = "SELECT dodaj_ucitelja('" + ime + "', '" + priimek + "', '" + email + "', '" + geslo + "');";
                db.executeQuery(query);
            } else {
//                CREATE OR REPLACE FUNCTION posodobi_ucitelja(p_id integer, p_ime character varying, p_priimek character varying, p_email character varying, p_geslo character varying)
                String query = "SELECT posodobi_ucitelja(" + uciteljId + ", '" + ime + "', '" + priimek + "', '" + email + "', '" + geslo + "');";
                db.executeQuery(query);
            }
            window.dispose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void show() {
        window.setVisible(true); // Naredimo okno vidno
    }
}
