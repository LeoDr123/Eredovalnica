import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UcenciForm {

    private JFrame window;
    private Container container;
    private JLabel mainTitle;
    private JLabel imeLabel;
    private JTextField imeField;
    private JLabel priimekLabel;
    private JTextField priimekField;
    private JLabel razredLabel;
    private JTextField razredField;
    private JLabel stars1_idLabel;
    private JComboBox<StarsItem> stars1_idComboBox;
    private JLabel stars2_idLabel;
    private JComboBox<StarsItem> stars2_idComboBox;
    private JButton shraniButton;
    private int ucenecId;

    public UcenciForm(int ucenecId) {
        this.ucenecId = ucenecId;

        window = new JFrame("Učenci Obrazec"); // Ustvarimo novo okno
        window.setPreferredSize(new Dimension(1024, 768)); // Nastavimo velikost okna
        window.setBounds(10, 10, 1024, 768); // Nastavimo pozicijo in velikost okna
        window.setLayout(null); // Nastavimo postavitev okna
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Nastavimo akcijo ob zaprtju okna
        window.setLocationRelativeTo(null); // Nastavimo pozicijo okna na sredino
        window.setResizable(false); // Omogočimo spreminjanje velikosti okna

        container = window.getContentPane(); // Ustvarimo nov container

        mainTitle = new JLabel("Učenci Obrazec"); // Ustvarimo nov label
        mainTitle.setFont(new Font("Arial", Font.BOLD, 48)); // Nastavimo velikost in obliko pisave
        mainTitle.setBounds(10, 50, 1004, 50); // Nastavimo pozicijo in velikost
        container.add(mainTitle); // Dodamo label v container

        imeLabel = new JLabel("Ime učenca:"); // Ustvarimo nov label
        imeLabel.setBounds(10, 150, 200, 40); // Nastavimo pozicijo in velikost
        container.add(imeLabel); // Dodamo label v container

        imeField = new JTextField(); // Ustvarimo nov textfield
        imeField.setBounds(220, 150, 200, 40); // Nastavimo pozicijo in velikost
        container.add(imeField); // Dodamo textfield v container

        priimekLabel = new JLabel("Priimek učenca:"); // Ustvarimo nov label
        priimekLabel.setBounds(10, 200, 200, 40); // Nastavimo pozicijo in velikost
        container.add(priimekLabel); // Dodamo label v container

        priimekField = new JTextField(); // Ustvarimo nov textfield
        priimekField.setBounds(220, 200, 200, 40); // Nastavimo pozicijo in velikost
        container.add(priimekField); // Dodamo textfield v container

        razredLabel = new JLabel("Razred:"); // Ustvarimo nov label
        razredLabel.setBounds(10, 250, 200, 40); // Nastavimo pozicijo in velikost
        container.add(razredLabel); // Dodamo label v container

        razredField = new JTextField(); // Ustvarimo nov textfield
        razredField.setBounds(220, 250, 200, 40); // Nastavimo pozicijo in velikost
        container.add(razredField); // Dodamo textfield v container

        stars1_idLabel = new JLabel("Skrbnik 1:"); // Ustvarimo nov label
        stars1_idLabel.setBounds(10, 300, 200, 40); // Nastavimo pozicijo in velikost
        container.add(stars1_idLabel); // Dodamo label v container

        stars1_idComboBox = new JComboBox<>(); // Ustvarimo nov combobox
        stars1_idComboBox.setBounds(220, 300, 200, 40); // Nastavimo pozicijo in velikost
        container.add(stars1_idComboBox); // Dodamo combobox v container

        stars2_idLabel = new JLabel("Skrbnik 2:"); // Ustvarimo nov label
        stars2_idLabel.setBounds(10, 350, 200, 40); // Nastavimo pozicijo in velikost
        container.add(stars2_idLabel); // Dodamo label v container

        stars2_idComboBox = new JComboBox<>(); // Ustvarimo nov combobox
        stars2_idComboBox.setBounds(220, 350, 200, 40); // Nastavimo pozicijo in velikost
        container.add(stars2_idComboBox); // Dodamo combobox v container

        try {
            stars2_idComboBox.addItem(new StarsItem(0, "Izberi", "", ""));
            stars1_idComboBox.addItem(new StarsItem(0, "Izberi", "", ""));
            Baza db = Baza.getInstance();
            String query = "SELECT * FROM \"Starsi\";";
            ResultSet resultSet = db.executeQuery(query);
            while (resultSet.next()) {
                StarsItem item = new StarsItem(resultSet.getInt("id"), resultSet.getString("ime"), resultSet.getString("priimek"), resultSet.getString("email"));
                stars1_idComboBox.addItem(item);
                stars2_idComboBox.addItem(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        shraniButton = new JButton("Shrani"); // Ustvarimo nov gumb
        shraniButton.setBounds(10, 550, 100, 40); // Nastavimo pozicijo in velikost
        shraniButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shraniUcenca();
            }
        });
        container.add(shraniButton); // Dodamo gumb v container

        window.setVisible(true); // Naredimo okno vidno

        if (ucenecId != 0) {
            try {
                Baza db = Baza.getInstance();
                String query = "SELECT * FROM \"Uceneci\" WHERE \"id\" = " + ucenecId + ";";
                ResultSet resultSet = db.executeQuery(query);
                if (resultSet.next()) {
                    imeField.setText(resultSet.getString("ime"));
                    priimekField.setText(resultSet.getString("priimek"));
                    razredField.setText(resultSet.getString("razred"));

                    int stars1_id = resultSet.getInt("stars1_id");
                    int stars2_id = resultSet.getInt("stars2_id");

                    for (int i = 0; i < stars1_idComboBox.getItemCount(); i++) {
                        if (stars1_idComboBox.getItemAt(i).id == stars1_id) {
                            stars1_idComboBox.setSelectedIndex(i);
                            break;
                        }
                    }

                    for (int i = 0; i < stars2_idComboBox.getItemCount(); i++) {
                        if (stars2_idComboBox.getItemAt(i).id == stars2_id) {
                            stars2_idComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void shraniUcenca() {
        String ime = imeField.getText();
        String priimek = priimekField.getText();
        String razred = razredField.getText();
        int stars1_id = stars1_idComboBox.getSelectedIndex() == -1 ? 0 : stars1_idComboBox.getItemAt(stars1_idComboBox.getSelectedIndex()).id;
        int stars2_id = stars2_idComboBox.getSelectedIndex() == -1 ? 0 : stars2_idComboBox.getItemAt(stars2_idComboBox.getSelectedIndex()).id;

        if (ime.isEmpty() || priimek.isEmpty() || razred.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Izpolnite vsa polja.", "Napaka", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Baza db = Baza.getInstance();

            if (ucenecId <= 0) {
//                CREATE OR REPLACE FUNCTION dodaj_ucenca(p_ime character varying, p_priimek character varying, p_razred character varying, p_stars1_id integer DEFAULT NULL::integer, p_stars2_id integer DEFAULT NULL::integer)
                String query = "SELECT dodaj_ucenca('" + ime + "', '" + priimek + "', '" + razred + "', " + (stars1_id == 0 ? "NULL" : stars1_id) + ", " + (stars2_id == 0 ? "NULL" : stars2_id) + ");";
                db.executeQuery(query);
            } else {
//                CREATE OR REPLACE FUNCTION posodobi_ucenca(p_id integer, p_ime character varying, p_priimek character varying, p_razred character varying, p_stars1_id integer DEFAULT NULL::integer, p_stars2_id integer DEFAULT NULL::integer)
                String query = "SELECT posodobi_ucenca(" + ucenecId + ", '" + ime + "', '" + priimek + "', '" + razred + "', " + (stars1_id == 0 ? "NULL" : stars1_id) + ", " + (stars2_id == 0 ? "NULL" : stars2_id) + ");";
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

    private class StarsItem {
        public int id;
        public String ime;
        public String priimek;
        public String email;

        public StarsItem(int id, String ime, String priimek, String email) {
            this.id = id;
            this.ime = ime;
            this.priimek = priimek;
            this.email = email;
        }

        @Override
        public String toString() {
            return ime + " " + priimek + " (" + email + ")";
        }
    }
}
