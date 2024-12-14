

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class bank{

    static Scanner sc = new Scanner(System.in);

    static ArrayList<Double> amountHistory = new ArrayList<>();
    static ArrayList<String> transHistory = new ArrayList<>();
    static ArrayList<String> dateHistory = new ArrayList<>();
    static ArrayList<Double> balanceHistory = new ArrayList<>();

    static double balance = 0;
    static double savings = 0;
    static double loan = 0;


    public static void main(String[] args) {
        
        String input;
        int choice = -1;
        double amount;
        
        while(choice != 5){
          
            System.out.println("\n== Welcome, " + data.name + " ==");

            System.out.println( "Balance : " + balance);
            System.out.println( "Saving  : " + savings);
            System.out.println( "Loan    : " + loan);

            System.out.println("\n== Transactions ==");
            System.out.println("1. Debit");
            System.out.println("2. Credit"); 
            System.out.println("3. History");
            System.out.println("4. Savings");
            System.out.println("5. Credit Loan");
            System.out.println("6. Deposit Interest Predictor");
            System.out.println("7. Logout");


            System.out.print("Choose an option : ");

            input = sc.next();

            try {
                choice = Integer.parseInt(input);
            }
            catch (NumberFormatException e){
                choice = 100;
            }

            sc.nextLine();               //dunno why

            switch(choice){
                case 1: System.out.print("\n== Debit ==");
                        System.out.print("\nEnter amount : ");
                        amount = sc.nextDouble();

                        sc.nextLine();

                        balance = deposit(amount);
                        System.out.printf("Your balance is now : RM " + "%.2f",balance); 
                        System.out.println();
                        break;

                case 2: System.out.print("\n== Credit ==");
                        System.out.print("\nEnter amount : ");

                        amount = sc.nextDouble();

                        sc.nextLine();

                        balance = withdraw(amount);  
                        System.out.printf("Your balance is now : RM " + "%.2f",balance); 
                        System.out.println();
                        break;
 
                case 3: printTransactions();
                        break;

                case 4: //Savings
                        break;
                
                case 5: //Credti Loan
                        break;

                case 6: //Deposit Interest Predictor
                        break;
                
                case 7: System.out.println("Logging Out");
                        break;

                default: System.out.println("Please enter a valid number");
            }
        }
    }

    static void checkBalance(){
        System.out.printf( "\nCurrent Balance : RM " + "%.2f",balance);
        System.out.println();
    }

    static double deposit(double amount) {

        while(amount < 0){

            System.out.println("Please Enter a valid amount!");
            System.out.print("\nEnter amount : ");
            amount = sc.nextDouble();

        }
        System.out.print("Enter description : ");
        String description = sc.nextLine();
        
        
        balance += amount;

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

        amountHistory.add(amount);
        transHistory.add(description);
        dateHistory.add(timeStamp);
        balanceHistory.add(balance);


        return balance;
    }

    static double withdraw(double amount){

        while(amount < 0 || amount > balance){

            if(amount < 0){
                System.out.println("Please Enter a valid amount!");
            }
            else if(amount > balance){
                System.out.println("Insuficient Balance");
                return balance;
            }
            System.out.print("\nEnter amount : ");
            amount = sc.nextDouble();
        }
        System.out.print("Enter description : ");
        String description = sc.nextLine();
        

        balance -= amount;

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

        amountHistory.add(amount);
        transHistory.add(description);
        dateHistory.add(timeStamp);
        balanceHistory.add(balance);


        return balance;
    }

    static void printTransactions(){
        
        System.out.println("\n== History ==");
        System.out.println("Date\t\tDescription\t\tAmount\t\tBalance");

        for(int i = 0 ; i < amountHistory.size() ; i++){
            System.out.printf(dateHistory.get(i) + "\t" + transHistory.get(i) + "\t\t" + amountHistory.get(i) + "\t\t" + balanceHistory.get(i));
            System.out.println();
        }
    }
    
}