package ledger_system;
public class Data{

    static String name;
    static String email;
    static String pass;
    double money, debt;

    Data(String name, String pass) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        money = 0;
        debt = 0;
    }
}
