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

public class Predmeti {
    private JFrame window; // Okno aplikacije
    private Container container; // Glavni vsebnik za elemente uporabniškega vmesnika
    private JLabel mainTitle; // Glavni naslov obrazca
    private JTable table; // Tabela za prikaz predmetov
    private DefaultTableModel model; // Model tabele za shranjevanje podatkov
    private Baza db;

    public Predmeti() {
        Skladisce skladisce = Skladisce.getInstance();

        try {
            db = Baza.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri povezavi s podatkovno bazo.", "Napaka", JOptionPane.ERROR_MESSAGE);
        }

        // Inicializacija okna
        window = new JFrame("Predmeti");
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
        mainTitle = new JLabel("Predmeti");
        mainTitle.setFont(new Font("Arial", Font.BOLD, 48));
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(mainTitle);

        // Ustvarjanje modela tabele
        model = new DefaultTableModel();
        model.addColumn("ID"); // Dodajanje stolpca "ID"
        model.addColumn("Ime"); // Dodajanje stolpca "Ime"
        model.addColumn("Opis"); // Dodajanje stolpca "Opis"
        model.addColumn("Učitelj"); // Dodajanje stolpca "Učitelj"

        // Dodajanje vrstic v tabelo
        try {
            String query = "";
            if (skladisce.getTipUporabnika() == TipUporabnika.ADMINISTRATOR) {
                query = "SELECT p.*, (u.ime || ' ' || u.priimek) AS ucitelj FROM \"Predmeti\" p, \"Ucitelji\" u WHERE p.ucitelj_id = u.id;";
            } else if (skladisce.getTipUporabnika() == TipUporabnika.UCITELJ) {
                query = "SELECT p.*, (u.ime || ' ' || u.priimek) AS ucitelj FROM \"Predmeti\" p, \"Ucitelji\" u WHERE p.ucitelj_id = u.id AND u.id = " + skladisce.getUporabnikId() + ";";
            }

            ResultSet resultSet = db.executeQuery(query);
            while (resultSet.next()) {
                model.addRow(new Object[]{resultSet.getInt("id"), resultSet.getString("ime_predmeta"), resultSet.getString("opis"), resultSet.getString("ucitelj")});
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
        columnModel.getColumn(2).setPreferredWidth(600);
        columnModel.getColumn(3).setPreferredWidth(200);

        // Ustvarjanje drsnega okna za tabelo
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonsPanel = new JPanel();

        if (skladisce.getTipUporabnika() == TipUporabnika.ADMINISTRATOR) {
            // Ustvarjanje panela z gumbi
            JButton addButton = new JButton("Dodaj nov predmet");
            JButton editButton = new JButton("Uredi predmet");
            JButton deleteButton = new JButton("Izbriši predmet");
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
                        String query = "";
                        if (skladisce.getTipUporabnika() == TipUporabnika.ADMINISTRATOR) {
                            query = "SELECT p.*, (u.ime || ' ' || u.priimek) AS ucitelj FROM \"Predmeti\" p, \"Ucitelji\" u WHERE p.ucitelj_id = u.id;";
                        } else if (skladisce.getTipUporabnika() == TipUporabnika.UCITELJ) {
                            query = "SELECT p.*, (u.ime || ' ' || u.priimek) AS ucitelj FROM \"Predmeti\" p, \"Ucitelji\" u WHERE p.ucitelj_id = u.id AND u.id = " + skladisce.getUporabnikId() + ";";
                        }
                        ResultSet resultSet = db.executeQuery(query);
                        while (resultSet.next()) {
                            model.addRow(new Object[]{resultSet.getInt("id"), resultSet.getString("ime_predmeta"), resultSet.getString("opis"), resultSet.getString("ucitelj")});
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Napaka pri pridobivanju podatkov iz baze.", "Napaka", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Poslušalci dogodkov za gumb "Dodaj nov predmet"
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Dodajanje novega predmeta
                    // Tukaj bi morali odpreti novo okno za dodajanje predmeta
                    PredmetiForm obrazec = new PredmetiForm(0);
                    obrazec.show();
                }
            });

            // Poslušalci dogodkov za gumb "Uredi predmet"
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Urejanje predmeta
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        // Pridobitev ID-ja predmeta iz izbrane vrstice
                        String predmetID = model.getValueAt(selectedRow, 0).toString();
                        PredmetiForm obrazec = new PredmetiForm(Integer.parseInt(predmetID));
                        obrazec.show();
                    } else {
                        JOptionPane.showMessageDialog(container, "Prosimo, izberite predmet za urejanje.");
                    }
                }
            });

            // Poslušalci dogodkov za gumb "Izbriši predmet"
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Brisanje predmeta
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        // Odstranitev izbrane vrstice iz tabele
                        try {
                            String predmetID = model.getValueAt(selectedRow, 0).toString();
                            String query = "SELECT izbrisi_predmet(" + predmetID + ");";
                            db.executeQuery(query);
                            model.removeRow(selectedRow);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Napaka pri brisanju predmeta.", "Napaka", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(container, "Prosimo, izberite predmet za brisanje.");
                    }
                }
            });
        } else if (skladisce.getTipUporabnika() == TipUporabnika.UCITELJ) {
            JButton refreshButton = new JButton("Osveži");
            buttonsPanel.add(refreshButton);
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
