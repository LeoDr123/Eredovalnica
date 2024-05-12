import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PredmetiForm {

    private JFrame window;
    private Container container;
    private JLabel mainTitle;
    private JLabel imeLabel;
    private JTextField imeField;
    private JLabel opisLabel;
    private JTextArea opisArea;

    private JLabel uciteljIdLabel;
    private JComboBox<UciteljItem> uciteljComboBox;
    private JButton shraniButton;
    private int predmetId;

    public PredmetiForm(int predmetId) {
        Skladisce skladisce = Skladisce.getInstance();

        this.predmetId = predmetId;

        window = new JFrame("Predmeti Obrazec"); // Ustvarimo novo okno
        window.setPreferredSize(new Dimension(1024, 768)); // Nastavimo velikost okna
        window.setBounds(10, 10, 1024, 768); // Nastavimo pozicijo in velikost okna
        window.setLayout(null); // Nastavimo postavitev okna
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Nastavimo akcijo ob zaprtju okna
        window.setLocationRelativeTo(null); // Nastavimo pozicijo okna na sredino
        window.setResizable(false); // Omogočimo spreminjanje velikosti okna

        container = window.getContentPane(); // Ustvarimo nov container

        mainTitle = new JLabel("Predmeti Obrazec"); // Ustvarimo nov label
        mainTitle.setFont(new Font("Arial", Font.BOLD, 48)); // Nastavimo velikost in obliko pisave
        mainTitle.setBounds(10, 50, 1004, 50); // Nastavimo pozicijo in velikost
        container.add(mainTitle); // Dodamo label v container

        imeLabel = new JLabel("Ime predmeta:"); // Ustvarimo nov label
        imeLabel.setBounds(10, 150, 200, 40); // Nastavimo pozicijo in velikost
        container.add(imeLabel); // Dodamo label v container

        imeField = new JTextField(); // Ustvarimo nov textfield
        imeField.setBounds(220, 150, 200, 40); // Nastavimo pozicijo in velikost
        container.add(imeField); // Dodamo textfield v container

        opisLabel = new JLabel("Opis predmeta:"); // Ustvarimo nov label
        opisLabel.setBounds(10, 200, 200, 40); // Nastavimo pozicijo in velikost
        container.add(opisLabel); // Dodamo label v container

        opisArea = new JTextArea(); // Ustvarimo novo text area
        opisArea.setBounds(220, 200, 600, 200); // Nastavimo pozicijo in velikost
        container.add(opisArea); // Dodamo text area v container

        uciteljIdLabel = new JLabel("Učitelj:"); // Ustvarimo nov label
        uciteljIdLabel.setBounds(10, 450, 200, 40); // Nastavimo pozicijo in velikost
        container.add(uciteljIdLabel); // Dodamo label v container

        uciteljComboBox = new JComboBox<>(); // Ustvarimo nov combobox
        uciteljComboBox.setBounds(220, 450, 200, 40); // Nastavimo pozicijo in velikost
        container.add(uciteljComboBox); // Dodamo combobox v container

        try {
            Baza db = Baza.getInstance();
            String query = "";
            if (skladisce.getTipUporabnika() == TipUporabnika.ADMINISTRATOR) {
                query = "SELECT * FROM \"Ucitelji\";";
            } else if (skladisce.getTipUporabnika() == TipUporabnika.UCITELJ) {
                query = "SELECT * FROM \"Ucitelji\" WHERE \"id\" = " + skladisce.getUporabnikId() + ";";
            }
            ResultSet resultSet = db.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ime = resultSet.getString("ime");
                String priimek = resultSet.getString("priimek");
                String email = resultSet.getString("email");
                uciteljComboBox.addItem(new UciteljItem(id, ime, priimek, email));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        shraniButton = new JButton("Shrani"); // Ustvarimo nov gumb
        shraniButton.setBounds(10, 550, 100, 40); // Nastavimo pozicijo in velikost
        shraniButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shraniPredmet();
            }
        });
        container.add(shraniButton); // Dodamo gumb v container

        window.setVisible(true); // Naredimo okno vidno

        if (predmetId != 0) {
            try {
                Baza db = Baza.getInstance();
                String query = "SELECT * FROM \"Predmeti\" WHERE \"id\" = " + predmetId + ";";
                ResultSet resultSet = db.executeQuery(query);
                if (resultSet.next()) {
                    imeField.setText(resultSet.getString("ime_predmeta"));
                    opisArea.setText(resultSet.getString("opis"));
                    int uciteljId = resultSet.getInt("ucitelj_id");

                    for (int i = 0; i < uciteljComboBox.getItemCount(); i++) {
                        if (uciteljComboBox.getItemAt(i).id == uciteljId) {
                            uciteljComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void shraniPredmet() {
        String ime = imeField.getText();
        String opis = opisArea.getText();

        if (uciteljComboBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Izberite učitelja.", "Napaka", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int uciteljId = uciteljComboBox.getItemAt(uciteljComboBox.getSelectedIndex()).id;

        if (ime.isEmpty() || opis.isEmpty() || uciteljId <= 0) {
            JOptionPane.showMessageDialog(null, "Ime, opis in učitelj ne smejo biti prazni.", "Napaka", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Baza db = Baza.getInstance();

            if (predmetId <= 0) {
//                CREATE OR REPLACE FUNCTION dodaj_predmet(p_ime_predmeta character varying, p_opis text, p_ucitelj_id integer)
                String query = "SELECT dodaj_predmet('" + ime + "', '" + opis + "', " + uciteljId + ");";
                db.executeQuery(query);
            } else {
//                CREATE OR REPLACE FUNCTION posodobi_predmet(p_id integer, p_ime_predmeta character varying, p_opis text, p_ucitelj_id integer)
                String query = "SELECT posodobi_predmet(" + predmetId + ", '" + ime + "', '" + opis + "', " + uciteljId + ");";
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

    private class UciteljItem {
        public int id;
        public String ime;
        public String priimek;
        public String email;

        public UciteljItem(int id, String ime, String priimek, String email) {
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
