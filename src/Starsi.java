import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Starsi {
    private JFrame window; // Okno aplikacije
    private Container container; // Glavni vsebnik za elemente uporabniškega vmesnika
    private JLabel mainTitle; // Glavni naslov obrazca
    private JTable table; // Tabela za prikaz starsev
    private DefaultTableModel model; // Model tabele za shranjevanje podatkov
    private Baza db;

    public Starsi() {
        try {
            db = Baza.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri povezavi s podatkovno bazo.", "Napaka", JOptionPane.ERROR_MESSAGE);
        }

        // Inicializacija okna
        window = new JFrame("Starši");
        window.setPreferredSize(new Dimension(1024, 768)); // Nastavitev velikosti okna
        window.setBounds(10, 10, 1024, 768); // Nastavitev položaja in velikosti okna
        window.setLayout(new BorderLayout()); // Uporaba BorderLayout za razporeditev komponent
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Zapiranje okna ob zaprtju
        window.setLocationRelativeTo(null); // Središčenje okna glede na zaslon
        window.setResizable(false); // Onemogočanje spreminjanja velikosti okna

        // Inicializacija glavnega vsebnika
        container = window.getContentPane();
        container.setLayout(new BorderLayout());

        // Dodajanje glavnega naslova obrazca
        mainTitle = new JLabel("Starši");
        mainTitle.setFont(new Font("Arial", Font.BOLD, 48));
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(mainTitle);

        // Ustvarjanje modela tabele
        model = new DefaultTableModel();
        model.addColumn("ID"); // Dodajanje stolpca "ID"
        model.addColumn("Ime"); // Dodajanje stolpca "Ime"
        model.addColumn("Priimek"); // Dodajanje stolpca "Priimek"
        model.addColumn("Email"); // Dodajanje stolpca "Email"
        model.addColumn("Geslo"); // Dodajanje stolpca "Geslo"

        // Dodajanje vrstic v tabelo
        try {
            String query = "SELECT * FROM \"Starsi\";";
            ResultSet resultSet = db.executeQuery(query);
            while (resultSet.next()) {
                model.addRow(new Object[]{resultSet.getInt("id"), resultSet.getString("ime"), resultSet.getString("priimek"), resultSet.getString("email"), resultSet.getString("geslo")});
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri pridobivanju podatkov iz baze.", "Napaka", JOptionPane.ERROR_MESSAGE);
        }

        // Ustvarjanje tabele s podanim modelom
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 24));
        table.setRowHeight(30);
        table.setDefaultEditor(Object.class, null);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Onemogočanje samodejnega prilagajanja velikosti stolpcev

        // Nastavitev preferirane širine stolpcev
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(200);
        columnModel.getColumn(3).setPreferredWidth(200);
        columnModel.getColumn(4).setPreferredWidth(200);

        // Ustvarjanje drsnega okna za tabelo
        JScrollPane scrollPane = new JScrollPane(table);

        // Ustvarjanje panela z gumbi
        JPanel buttonsPanel = new JPanel();
        JButton addButton = new JButton("Dodaj novega starša");
        JButton editButton = new JButton("Uredi starša");
        JButton deleteButton = new JButton("Izbriši starša");
        JButton refreshButton = new JButton("Osveži");
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setRowCount(0);
                try {
                    String query = "SELECT * FROM \"Starsi\";";
                    ResultSet resultSet = db.executeQuery(query);
                    while (resultSet.next()) {
                        model.addRow(new Object[]{resultSet.getInt("id"), resultSet.getString("ime"), resultSet.getString("priimek"), resultSet.getString("email"), resultSet.getString("geslo")});
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Napaka pri pridobivanju podatkov iz baze.", "Napaka", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Poslušalci dogodkov za gumb "Dodaj novega starsa"
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Dodajanje novega starsa
                // Tukaj bi morali odpreti novo okno za dodajanje starsa
                StarsiForm obrazec = new StarsiForm(0);
                obrazec.show();
            }
        });

        // Poslušalci dogodkov za gumb "Uredi starsa"
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Urejanje starsa
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Pridobitev ID-ja starsa iz izbrane vrstice
                    String starsID = model.getValueAt(selectedRow, 0).toString();
                    StarsiForm obrazec = new StarsiForm(Integer.parseInt(starsID));
                    obrazec.show();
                } else {
                    JOptionPane.showMessageDialog(container, "Prosimo, izberite starša za urejanje.");
                }
            }
        });

        // Poslušalci dogodkov za gumb "Izbriši starsa"
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Brisanje starsa
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Odstranitev izbrane vrstice iz tabele
                    try {
                        String starsId = model.getValueAt(selectedRow, 0).toString();
                        String query = "SELECT izbrisi_starsa(" + starsId + ");";
                        db.executeQuery(query);
                        model.removeRow(selectedRow);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Napaka pri brisanju starša.", "Napaka", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(container, "Prosimo, izberite starša za brisanje.");
                }
            }
        });

        // Dodajanje tabele v glavni vsebnik
        container.add(scrollPane, BorderLayout.CENTER);
        // Dodajanje panela z gumbi v glavni vsebnik
        container.add(buttonsPanel, BorderLayout.SOUTH);
    }

    // Metoda za prikaz okna
    public void show() {
        window.pack(); // Samodejno prilagodi velikost okna glede na vsebino
        window.setVisible(true); // Naredi okno vidno
    }
}
