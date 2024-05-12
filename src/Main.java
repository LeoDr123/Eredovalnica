import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ZacetnaStran zacetnaStran = new ZacetnaStran();
                zacetnaStran.show();
            }
        });
    }
}
