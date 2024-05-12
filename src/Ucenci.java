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

public class Ucenci {
    private JFrame window; // Okno aplikacije
    private Container container; // Glavni vsebnik za elemente uporabniškega vmesnika
    private JLabel mainTitle; // Glavni naslov obrazca
    private JTable table; // Tabela za prikaz ucencev
    private DefaultTableModel model; // Model tabele za shranjevanje podatkov
    private Baza db;

    public Ucenci() {
        Skladisce skladisce = Skladisce.getInstance();

        try {
            db = Baza.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri povezavi s podatkovno bazo.", "Napaka", JOptionPane.ERROR_MESSAGE);
        }

        // Inicializacija okna
        window = new JFrame("Učenci");
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
        mainTitle = new JLabel("Učenci");
        mainTitle.setFont(new Font("Arial", Font.BOLD, 48));
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(mainTitle);

        // Ustvarjanje modela tabele
        model = new DefaultTableModel();
        model.addColumn("ID"); // Dodajanje stolpca "ID"
        model.addColumn("Ime"); // Dodajanje stolpca "Ime"
        model.addColumn("Priimek"); // Dodajanje stolpca "Priimek"
        model.addColumn("Razred"); // Dodajanje stolpca "Razred"
        model.addColumn("Skrbnik 1"); // Dodajanje stolpca "Skrbnik 1"
        model.addColumn("Skrbnik 2"); // Dodajanje stolpca "Skrbnik 2"

        // Dodajanje vrstic v tabelo
        try {
            String query = "";
            if (skladisce.getTipUporabnika() == TipUporabnika.ADMINISTRATOR) {
                query = "SELECT u.*, (s1.ime || ' ' || s1.priimek) AS skrbnik1, (s2.ime || ' ' || s2.priimek) AS skrbnik2 FROM \"Uceneci\" u LEFT JOIN \"Starsi\" s1 ON u.stars1_id = s1.id LEFT JOIN \"Starsi\" s2 ON u.stars2_id = s2.id;";
            } else if (skladisce.getTipUporabnika() == TipUporabnika.STARS) {
                query = "SELECT u.*, (s1.ime || ' ' || s1.priimek) AS skrbnik1, (s2.ime || ' ' || s2.priimek) AS skrbnik2 FROM \"Uceneci\" u LEFT JOIN \"Starsi\" s1 ON u.stars1_id = s1.id LEFT JOIN \"Starsi\" s2 ON u.stars2_id = s2.id WHERE u.stars1_id = " + skladisce.getUporabnikId() + " OR u.stars2_id = " + skladisce.getUporabnikId() + ";";
            }
            ResultSet resultSet = db.executeQuery(query);
            while (resultSet.next()) {
                model.addRow(new Object[]{resultSet.getInt("id"), resultSet.getString("ime"), resultSet.getString("priimek"), resultSet.getString("razred"), resultSet.getString("skrbnik1"), resultSet.getString("skrbnik2")});
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
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(200);
        columnModel.getColumn(5).setPreferredWidth(200);

        // Ustvarjanje drsnega okna za tabelo
        JScrollPane scrollPane = new JScrollPane(table);

        // Ustvarjanje panela z gumbi
        JPanel buttonsPanel = new JPanel();

        if (skladisce.getTipUporabnika() == TipUporabnika.ADMINISTRATOR) {
            JButton addButton = new JButton("Dodaj novega učenca");
            JButton editButton = new JButton("Uredi učenca");
            JButton deleteButton = new JButton("Izbriši učenca");
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
                        String query = "SELECT u.*, (s1.ime || ' ' || s1.priimek) AS skrbnik1, (s2.ime || ' ' || s2.priimek) AS skrbnik2 FROM \"Uceneci\" u LEFT JOIN \"Starsi\" s1 ON u.stars1_id = s1.id LEFT JOIN \"Starsi\" s2 ON u.stars2_id = s2.id;";
                        ResultSet resultSet = db.executeQuery(query);
                        while (resultSet.next()) {
                            model.addRow(new Object[]{resultSet.getInt("id"), resultSet.getString("ime"), resultSet.getString("priimek"), resultSet.getString("razred"), resultSet.getString("skrbnik1"), resultSet.getString("skrbnik2")});
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Napaka pri pridobivanju podatkov iz baze.", "Napaka", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Poslušalci dogodkov za gumb "Dodaj novega ucenca"
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Dodajanje novega ucenca
                    // Tukaj bi morali odpreti novo okno za dodajanje ucenca
                    UcenciForm obrazec = new UcenciForm(0);
                    obrazec.show();
                }
            });

            // Poslušalci dogodkov za gumb "Uredi ucenca"
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Urejanje ucenca
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        // Pridobitev ID-ja ucenca iz izbrane vrstice
                        String ucenecID = model.getValueAt(selectedRow, 0).toString();
                        UcenciForm obrazec = new UcenciForm(Integer.parseInt(ucenecID));
                        obrazec.show();
                    } else {
                        JOptionPane.showMessageDialog(container, "Prosimo, izberite učenca za urejanje.");
                    }
                }
            });

            // Poslušalci dogodkov za gumb "Izbriši ucenca"
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Brisanje ucenca
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        // Odstranitev izbrane vrstice iz tabele
                        try {
                            String ucenecID = model.getValueAt(selectedRow, 0).toString();
                            String query = "SELECT izbrisi_ucenca(" + ucenecID + ");";
                            db.executeQuery(query);
                            model.removeRow(selectedRow);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Napaka pri brisanju učenca.", "Napaka", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(container, "Prosimo, izberite učenca za brisanje.");
                    }
                }
            });
        } else if (skladisce.getTipUporabnika() == TipUporabnika.STARS) {
            JButton refreshButton = new JButton("Osveži");
            buttonsPanel.add(refreshButton);

            refreshButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    model.setRowCount(0);
                    try {
                        String query = "SELECT u.*, (s1.ime || ' ' || s1.priimek) AS skrbnik1, (s2.ime || ' ' || s2.priimek) AS skrbnik2 FROM \"Uceneci\" u LEFT JOIN \"Starsi\" s1 ON u.stars1_id = s1.id LEFT JOIN \"Starsi\" s2 ON u.stars2_id = s2.id;";
                        ResultSet resultSet = db.executeQuery(query);
                        while (resultSet.next()) {
                            model.addRow(new Object[]{resultSet.getInt("id"), resultSet.getString("ime"), resultSet.getString("priimek"), resultSet.getString("razred"), resultSet.getString("skrbnik1"), resultSet.getString("skrbnik2")});
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Napaka pri pridobivanju podatkov iz baze.", "Napaka", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

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
