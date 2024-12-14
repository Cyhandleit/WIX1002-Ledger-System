import java.util.Scanner;

public class main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        String input;
        int choicSe;
        while (true) {
            System.out.println("\nLogin or Register :");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println();

            input = sc.next();

            try {
               choice = Integer.parseInt(input);
           }
           catch (NumberFormatException e){
               choice = 6;
           }

            sc.nextLine();

            switch (choice) {
                case 1:
                    if(loginPage()){
                       bank.main(args);
                    }

                    break;

                case 2:
                    registerPage();
                    break;

                default:
                    System.out.println("Enter a valid number !");
                    break;
            }
        }
    }

    // login page (boolean to see if login successful or not)
    public static boolean loginPage() {

        String input_email;
        String input_pass;

        System.out.println("\n== Please enter your email and password == ");
        System.out.print("email : ");
        input_email = sc.next();
        
        System.out.print("password : ");
        input_pass = sc.nextLine();

        if (!data.email.equals(input_email) || !data.pass.equals(input_pass)){
            System.out.println("Username or password is incorrect!");
            return false;
        } else {
            System.out.println("\nLogin Successful!");
            System.out.println("== Welcome, " + data.name + " ==");
            return true;
        }

    }

    // register page
    public static void registerPage() {

        System.out.println("\n== Please fill in the form ==");
        System.out.print("username : ");
        data.name = sc.next();

        System.out.print("email : ");
        data.email = sc.nextLine();

        System.out.print("password : ");
        data.pass = sc.nextLine();


        System.out.println("\nRegister Successful!");

    }
}

