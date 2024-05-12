import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ZacetnaStran {

    private JFrame window;
    private Container container;

    private JLabel mainTitle;

    public ZacetnaStran() {
        Skladisce skladisce = Skladisce.getInstance();

        window = new JFrame("Eredovalnica"); // Ustvarimo novo okno
        window.setPreferredSize(new Dimension(1024, 768)); // Nastavimo velikost okna
        window.setBounds(10, 10, 1024, 768); // Nastavimo pozicijo in velikost okna
        window.setLayout(null); // Nastavimo postavitev okna
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Nastavimo akcijo ob zaprtju okna
        window.setLocationRelativeTo(null); // Nastavimo pozicijo okna na sredino
        window.setResizable(false); // Omogočimo spreminjanje velikosti okna

        container = window.getContentPane(); // Ustvarimo nov container

        mainTitle = new JLabel("Eredovalnica"); // Ustvarimo nov label
        mainTitle.setFont(new Font("Arial", Font.BOLD, 48)); // Nastavimo velikost in obliko pisave
        mainTitle.setBounds(10, 50, 1004, 50); // Nastavimo pozicijo in velikost
        container.add(mainTitle); // Dodamo label v container

        if (skladisce.getTipUporabnika() == TipUporabnika.ADMINISTRATOR) {
            JButton ucitelji = new JButton("Učitelji");
            ucitelji.setBounds(10, 150, 200, 40);
            ucitelji.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Ucitelji ucitelji = new Ucitelji();
                    ucitelji.show();
                }
            });
            container.add(ucitelji);

            JButton predmeti = new JButton("Predmeti");
            predmeti.setBounds(10, 200, 200, 40);
            predmeti.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Predmeti predmeti = new Predmeti();
                    predmeti.show();
                }
            });
            container.add(predmeti);

            JButton starsi = new JButton("Starši");
            starsi.setBounds(10, 250, 200, 40);
            starsi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Starsi starsi = new Starsi();
                    starsi.show();
                }
            });
            container.add(starsi);

            JButton ucenci = new JButton("Učenci");
            ucenci.setBounds(10, 300, 200, 40);
            ucenci.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Ucenci ucenci = new Ucenci();
                    ucenci.show();
                }
            });
            container.add(ucenci);

            JButton ocene = new JButton("Ocene");
            ocene.setBounds(10, 350, 200, 40);
            ocene.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
            container.add(ocene);
        }
    }

    public void show() {
        window.setVisible(true);
    }
}
