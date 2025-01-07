package com.example.Original;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;


public class bank {

    static Scanner sc = new Scanner(System.in);

    static ArrayList<Double> amountHistory = new ArrayList<>();
    static ArrayList<String> transHistory = new ArrayList<>();
    static ArrayList<String> dateHistory = new ArrayList<>();
    static ArrayList<Double> balanceHistory = new ArrayList<>();
    static ArrayList<String> loanRepaymentHistory = new ArrayList<>();
    static ArrayList<Double> loanAmountHistory = new ArrayList<>();
    static ArrayList<Double> loanHistory = new ArrayList<>();

    static double balance = 0;
    static double savings = 0;
    static double loan = 0;
    static double percentage;

    static boolean savingstatus = false;

    public static void main(String[] args) {

        String input;
        int choice = -1;
        double amount;

        while (choice != 7) {

            System.out.println("\n== Welcome, " + data.name + " ==");

            System.out.printf("%10s %15.2f\n", "Balance : ", balance);
            System.out.printf("%10s %15.2f\n", "Saving  : ", savings);
            System.out.printf("%10s %15.2f\n  ", "Loan    : ", loan);

            System.out.println("\n== Transactions ==");
            System.out.println("1. Debit");
            System.out.println("2. Credit");
            System.out.println("3. History");
            System.out.println("4. Savings");
            System.out.println("5. Credit Loan");
            System.out.println("6. Deposit Interest Predictor");
            System.out.println("7. Logout");
            System.out.println("8. Get Report");

            System.out.print("Choose an option : ");

            input = sc.next();

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                choice = 100;
            }

            sc.nextLine(); // dunno why

            switch (choice) {
                case 1:
                    deposit();
                    break;

                case 2:
                    withdraw();
                    break;

                case 3:
                    printTransactions();
                    break;

                case 4:
                    savings();
                    break;

                case 5:
                    creditLoan();
                    break;

                case 6:
                    depositInterestPredictor();
                    break;

                case 7:
                    System.out.println("Logging Out");
                    break;
                    
                default:
                    System.out.println("Please enter a valid number");
            }
        }
    }

    static void checkBalance() {
        System.out.printf("\nCurrent Balance : RM " + "%.2f", balance);
        System.out.println();
    }

    static void deposit() {

        double amount;

        do {
            System.out.println("\n== DEBIT ==");
            System.out.print("Enter amount : ");
            amount = sc.nextDouble();
            sc.nextLine();

            if (amount < 0) {
                System.out.println("Please Enter a valid Amount!!");
            }

        } while (amount < 0);

        System.out.print("Enter description : ");
        String description = sc.nextLine();

        if (savingstatus == true) {
            savings += amount * (percentage / 100);
            balance += amount * (1 - (percentage / 100));
        } else
            balance += amount;

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

        amountHistory.add(amount);
        transHistory.add(description);
        dateHistory.add(timeStamp);
        balanceHistory.add(balance);

        System.out.printf("Your balance is now : RM " + "%.2f", balance);
        System.out.println();

    }

    static void withdraw() {

        double amount;

        do {
            System.out.println("\n== CREDIT ==");
            System.out.print("Enter amount : ");
            amount = sc.nextDouble();
            sc.nextLine();

            if (amount < 0) {
                System.out.println("Please Enter a valid amount!");
            } else if (amount > balance + loan) {
                System.out.println("Insuficient Balance");
            }

        } while (amount < 0 || amount > balance + loan);

        System.out.print("Enter description : ");
        String description = sc.nextLine();

        if (amount > loan) {
            balance += loan;
            balance -= amount;
            loan = 0;
        } else
            loan -= amount;

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

        amountHistory.add(amount);
        transHistory.add(description);
        dateHistory.add(timeStamp);
        balanceHistory.add(balance);

        System.out.printf("Your balance is now : RM " + "%.2f", balance);
        System.out.println();

    }

    static void printTransactions() {


        System.out.println("\n== History ==");
        System.out.printf("%-15s %-15s %10s %10s\n",
                   "Date","Description","Amount","Balance");

        for (int i = 0; i < transHistory.size(); i++) {
            System.out.printf("%-15s %-15s %10.2f %10.2f\n",
                            dateHistory.get(i), transHistory.get(i), amountHistory.get(i),balanceHistory.get(i));
        }

        System.out.println("\nGet Report");
        System.out.println("1. Yes");
        System.out.println("2. No");
        System.out.print("Choice : ");
        int input = sc.nextInt();
        sc.nextLine();

        switch(input){
            case 1: getReport();
                    System.out.println("Report Generated Successfully");
                    break;
            default: break;
        }
    }

    static void savings() {

        System.out.println("\n== Savings ==");

        if (savingstatus == false) {
            System.out.print("do you want to activate it (Y/N) : ");
            String input = sc.next().toUpperCase();
            sc.nextLine();

            switch (input) {
                case "Y":
                    savingstatus = true;

                    do {
                        if (percentage > 100 || percentage < 0) {
                            System.out.println("Invalid amount");
                        }
                        System.out.print("Please enter the percentage you wish to deduct from your next debit : ");
                        percentage = sc.nextDouble();
                    } while (percentage > 100 || percentage < 0);

                    System.out.println("\nSavings Activated");

                    break;

                case "N":
                    System.out.println("OK");
                    break;
                default:
                    System.out.println("Enter a Valid Input");
            }
        } else {
            System.out.println("1. Change saving percentage");
            System.out.println("2. Deacticate Savings");
            System.out.println("3. Back");
            System.out.print("Choice : ");
            int input = sc.nextInt();
            sc.nextLine();

            switch (input) {
                case 1:
                    System.out.print("Please enter the percentage you wish to deduct from your next debit : ");
                    percentage = sc.nextDouble();
                    System.out.println("\nSavings Changed");

                    break;
                case 2:
                    savingstatus = false;
                    System.out.println("Savings account deacticated");
                    break;

                case 3:
                    break;
            }
        }

    }

    static void creditLoan() {

        System.out.println("\n==Credit Loan== ");
        System.out.println("1. Apply Credit Loan ");
        System.out.println("2. Repay Credit Loan ");
        System.out.println("3. Loan History ");
        System.out.println("4. Back ");
        System.out.print("Choice : ");

        int input = sc.nextInt();
        sc.nextLine();

        switch (input) {

            case 1:
                double loan_amount;
                do {
                    System.out.println("\nLoan Apply");
                    System.out.print("How much : ");
                    loan_amount = sc.nextDouble();
                } while (loan_amount < 0);

                loan += loan_amount;
                System.out.println("Loan has been added");

                String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                dateHistory.add(timeStamp);

                loanRepaymentHistory.add("apply");
                loanAmountHistory.add(loan_amount);
                loanHistory.add(loan);
                break;

            case 2:
                double repay_amount;
                do {
                    System.out.println("\nLoan Repayment");
                    System.out.print("How much : ");
                    repay_amount = sc.nextDouble();

                    System.out.println("Deduct from :");
                    System.out.println("1. Balance");
                    System.out.println("2. Debit ");
                    System.out.print("Choice : ");

                    int repay_input = sc.nextInt();

                    switch (repay_input) {
                        case 1:
                            if (repay_amount > balance) {
                                System.out.println("Insuficient Balance!!");
                            } else {
                                balance -= repay_amount;
                                loan -= repay_amount;

                                timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

                                amountHistory.add(repay_amount);
                                transHistory.add("Loan repayment");
                                dateHistory.add(timeStamp);
                                balanceHistory.add(balance);

                                loanRepaymentHistory.add("repay");
                                loanAmountHistory.add(repay_amount);
                                loanHistory.add(loan);

                            }
                            break;

                        case 2:
                            loan -= repay_amount;

                            timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                            dateHistory.add(timeStamp);

                            loanRepaymentHistory.add("repay");
                            loanAmountHistory.add(repay_amount);
                            loanHistory.add(loan);
                            break;

                        default:
                            System.out.println("Invalid input");
                            break;

                    }

                } while (repay_amount < 0 || repay_amount > loan);

                break;

            case 3:
                System.out.println("\n== History ==");
                System.out.printf("%-15s %-15s %10s %10s\n",
                            "Date","Description","Amount","Loan Balance");

                for (int i = 0; i < loanHistory.size(); i++) {
                    System.out.printf("%-15s %-15s %10.2f %10.2f\n",
                            dateHistory.get(i), loanRepaymentHistory.get(i), loanAmountHistory.get(i),
                            loanHistory.get(i));
                }
                break;

            case 4:
                break;

            default:
                System.out.println("Invalid input!");
        }
    }

    static void depositInterestPredictor() {

        double miRHB = (balance * 2.6) / 12;
        double miMB = (balance * 2.5) / 12;
        double miHL = (balance * 2.3) / 12;
        double miAll = (balance * 2.85) / 12;
        double miAB = (balance * 2.55) / 12;
        double miSC = (balance * 2.65) / 12;

        System.out.println("\n==Monthly Interest==");
        System.out.printf("%-20s %15.2f\n", "RHB", miRHB);
        System.out.printf("%-20s %15.2f\n", "Maybank", miMB);
        System.out.printf("%-20s %15.2f\n", "Hong Leong", miHL);
        System.out.printf("%-20s %15.2f\n", "Alliance", miAll);
        System.out.printf("%-20s %15.2f\n", "Ambank", miAB);
        System.out.printf("%-20s %15.2f\n", "Standard Chartered", miSC);
    }

    static void getReport(){
        String timeStamp = new SimpleDateFormat("_ddMMyyyy_HHmm").format(Calendar.getInstance().getTime());

        try {
            String filename ="UserTransactionReport/" + data.name + timeStamp + ".csv";
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

            bw.write(data.name + "'s Transaction Report");
            bw.write("\nDate,Description,Amount,Balance");
            for (int i = 0; i < transHistory.size(); i++) {
                String date = dateHistory.get(i);
                String description = transHistory.get(i);
                String amount = String.valueOf(amountHistory.get(i));
                String balance = String.valueOf(balanceHistory.get(i));
                bw.write("\n" + date + "," + description + "," + amount + "," + balance);
            }

            bw.close();
        } catch (Exception e) {
            System.out.println("Failed Generating a report");
            
        }
    }

}