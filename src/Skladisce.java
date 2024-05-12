

public class Skladisce {
    // Singleton vzorec
    private static Skladisce instance;
    private int uporabnikId;
    private TipUporabnika tipUporabnika;

    private Skladisce() {

    }

    // Singleton metoda za dostop do instance
    public static synchronized Skladisce getInstance() {
        if (instance == null) {
            instance = new Skladisce();
        }
        return instance;
    }

    public void setTipUporabnika(TipUporabnika tipUporabnika) {
        this.tipUporabnika = tipUporabnika;
    }

    public TipUporabnika getTipUporabnika() {
        return tipUporabnika;
    }

    public void setUporabnikId(int uporabnikId) {
        this.uporabnikId = uporabnikId;
    }

    public int getUporabnikId() {
        return uporabnikId;
    }
}
