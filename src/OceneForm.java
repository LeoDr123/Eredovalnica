import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OceneForm {

    private JFrame window;
    private Container container;
    private JLabel mainTitle;
    private JLabel ocenaLabel;
    private JTextField ocenaField;
    private JLabel predmetIdLabel;
    private JComboBox<PredmetItem> predmetComboBox;
    private JLabel ucenecIdLabel;
    private JComboBox<UcenecItem> ucenecComboBox;
    private JButton shraniButton;
    private int ocenaId;

    public OceneForm(int ocenaId) {
        this.ocenaId = ocenaId;

        window = new JFrame("Ocene Obrazec"); // Ustvarimo novo okno
        window.setPreferredSize(new Dimension(1024, 768)); // Nastavimo velikost okna
        window.setBounds(10, 10, 1024, 768); // Nastavimo pozicijo in velikost okna
        window.setLayout(null); // Nastavimo postavitev okna
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Nastavimo akcijo ob zaprtju okna
        window.setLocationRelativeTo(null); // Nastavimo pozicijo okna na sredino
        window.setResizable(false); // Omogočimo spreminjanje velikosti okna

        container = window.getContentPane(); // Ustvarimo nov container

        mainTitle = new JLabel("Ocene Obrazec"); // Ustvarimo nov label
        mainTitle.setFont(new Font("Arial", Font.BOLD, 48)); // Nastavimo velikost in obliko pisave
        mainTitle.setBounds(10, 50, 1004, 50); // Nastavimo pozicijo in velikost
        container.add(mainTitle); // Dodamo label v container

        ocenaLabel = new JLabel("Ocena:"); // Ustvarimo nov label
        ocenaLabel.setBounds(10, 150, 200, 40); // Nastavimo pozicijo in velikost
        container.add(ocenaLabel); // Dodamo label v container

        ocenaField = new JTextField(); // Ustvarimo nov textfield
        ocenaField.setBounds(220, 150, 200, 40); // Nastavimo pozicijo in velikost
        container.add(ocenaField); // Dodamo textfield v container

        predmetIdLabel = new JLabel("Predmet:"); // Ustvarimo nov label
        predmetIdLabel.setBounds(10, 200, 200, 40); // Nastavimo pozicijo in velikost
        container.add(predmetIdLabel); // Dodamo label v container

        predmetComboBox = new JComboBox<>(); // Ustvarimo nov combobox
        predmetComboBox.setBounds(220, 200, 200, 40); // Nastavimo pozicijo in velikost
        container.add(predmetComboBox); // Dodamo combobox v container

        ucenecIdLabel = new JLabel("Učenec:"); // Ustvarimo nov label
        ucenecIdLabel.setBounds(10, 250, 200, 40); // Nastavimo pozicijo in velikost
        container.add(ucenecIdLabel); // Dodamo label v container

        ucenecComboBox = new JComboBox<>(); // Ustvarimo nov combobox
        ucenecComboBox.setBounds(220, 250, 200, 40); // Nastavimo pozicijo in velikost
        container.add(ucenecComboBox); // Dodamo combobox v container

        try {
            Baza db = Baza.getInstance();
            String query = "SELECT * FROM \"Predmeti\";";
            ResultSet resultSet = db.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ime = resultSet.getString("ime_predmeta");
                String opis = resultSet.getString("opis");
                predmetComboBox.addItem(new PredmetItem(id, ime, opis));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Baza db = Baza.getInstance();
            String query = "SELECT * FROM \"Uceneci\";";
            ResultSet resultSet = db.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ime = resultSet.getString("ime");
                String priimek = resultSet.getString("priimek");
                ucenecComboBox.addItem(new UcenecItem(id, ime, priimek));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        shraniButton = new JButton("Shrani"); // Ustvarimo nov gumb
        shraniButton.setBounds(10, 550, 100, 40); // Nastavimo pozicijo in velikost
        shraniButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shraniOceno();
            }
        });
        container.add(shraniButton); // Dodamo gumb v container

        window.setVisible(true); // Naredimo okno vidno

        if (ocenaId != 0) {
            try {
                Baza db = Baza.getInstance();
                String query = "SELECT * FROM \"Ocene\" WHERE \"id\" = " + ocenaId + ";";
                ResultSet resultSet = db.executeQuery(query);
                if (resultSet.next()) {
                    ocenaField.setText(resultSet.getString("ocena"));
                    int predmetId = resultSet.getInt("predmet_id");
                    int ucenecId = resultSet.getInt("ucenec_id");

                    for (int i = 0; i < predmetComboBox.getItemCount(); i++) {
                        if (predmetComboBox.getItemAt(i).id == predmetId) {
                            predmetComboBox.setSelectedIndex(i);
                            break;
                        }
                    }

                    for (int i = 0; i < ucenecComboBox.getItemCount(); i++) {
                        if (ucenecComboBox.getItemAt(i).id == ucenecId) {
                            ucenecComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void shraniOceno() {
        String ocena = ocenaField.getText();

        if (predmetComboBox.getSelectedIndex() == -1 || ucenecComboBox.getSelectedIndex() == -1|| ocena.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Izpolnite vsa polja.", "Napaka", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int predmetId = predmetComboBox.getItemAt(predmetComboBox.getSelectedIndex()).id;
        int ucenecId = ucenecComboBox.getItemAt(ucenecComboBox.getSelectedIndex()).id;

        try {
            Baza db = Baza.getInstance();

            if (ocenaId <= 0) {
//                CREATE OR REPLACE FUNCTION dodaj_oceno(p_ocena integer, p_predmet_id integer, p_ucenec_id integer)
                String query = "SELECT dodaj_oceno(" + ocena + ", " + predmetId + ", " + ucenecId + ");";
                db.executeQuery(query);
            } else {
//                CREATE OR REPLACE FUNCTION posodobi_oceno(p_id integer, p_ocena integer, p_predmet_id integer, p_ucenec_id integer)
                String query = "SELECT posodobi_oceno(" + ocenaId + ", " + ocena + ", " + predmetId + ", " + ucenecId + ");";
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

    private class PredmetItem {
        public int id;
        public String ime;
        public String opis;

        public PredmetItem(int id, String ime, String opis) {
            this.id = id;
            this.ime = ime;
            this.opis = opis;
        }

        public String toString() {
            return this.ime;
        }
    }

    private class UcenecItem {
        public int id;
        public String ime;
        public String priimek;

        public UcenecItem(int id, String ime, String priimek) {
            this.id = id;
            this.ime = ime;
            this.priimek = priimek;
        }

        public String toString() {
            return this.ime + " " + this.priimek;
        }
    }
}
