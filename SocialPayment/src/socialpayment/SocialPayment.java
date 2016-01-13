package socialpayment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Devesh
 */
public class SocialPayment {

    private static final String[] commands = {"user", "balance", "pay", "feed", "add"}; //conatins the commands which can be performed
    private static int flag = 0; //used to check if command entered is valid
    private static BufferedReader br = null; //used to read the file
    private static final ArrayList<User> users = new ArrayList(); //used to store the users created
    private static final ArrayList<CreditCard> cards = new ArrayList<>(); // used to store the cards created

    public static void main(String[] args) {

        boolean takech, takefile;
        /*takech = to check if stdin is used
        takefile = to check if file has to be used*/
        if (args.length < 1)// if no argument is given
        {
            takech = true;
            takefile = false;
        } else {
            takech = false;

            try {
                br = new BufferedReader(new FileReader(args[0])); // creates a reader to read the file given as argument
                takefile = true;
            } catch (FileNotFoundException ex) {
                System.out.println("ERROR: error opening the file");// displays error if file is not found
                takefile = false;
            }
        }

        if (takech) {
            Scanner scc = new Scanner(System.in);
            System.out.println("Enter the choice: ");
            char ch = scc.next().charAt(0); //takes input of choice
            while (ch != 'q') {
                flag = 0;
                int chosen = -1; //stores the command entered
                User user;

                System.out.println("Enter the command"); //takes input the command
                Scanner sc = new Scanner(System.in);
                String cmd = sc.nextLine();
                String[] parts = cmd.split("\\s"); // creates different parts of the command given according to the space
                try {
                    int count = 0;
                    for (int i = 0; i < 5; i++) {

                        if (!parts[0].equals(commands[i])) //used to check if command entered is present in the predefined commands
                        {
                            count++;
                        } else if (parts[0].equals(commands[i])) {
                            chosen = i;
                        }

                    }
                    if (count > 4) ///no valid command entered, so break to exception
                    {
                        flag = 1;
                        throw new Exception();
                    }
                } catch (Exception e) {
                    System.out.println("ERROR: command not recognized");
                }
                if (flag == 0) // valid command entered
                {
                    if (chosen == 0) // "user" command entered
                    {
                        try {
                            boolean itisuser;
                            itisuser = parts[0].equals("user");

                            if (!parts[1].isEmpty() && parts[1] != null && itisuser) //checks validity of the "user" command and creates the user
                            {
                                user = new User(parts[1]);  // creates a new user
                                users.add(user);  //push the user to the users list

                            } else {
                                throw new Exception(); // catches exception when just user is entered and not user <name>
                            }

                        } catch (Exception e) {
                            System.out.println("ERROR: invalid arguments");
                        }
                    }

                    if (chosen == 1) // "balance" is entered
                    {
                        int count = 0; //keeps count of which exception is caught
                        try {

                            if (parts[1] == null || parts[1].isEmpty()) //if no valid arguments given
                            {
                                count = -1;
                                throw new Exception();
                            }
                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).name.equals(parts[1])) {
                                    System.out.println("$ " + users.get(i).balance);

                                }

                                if (!users.get(i).name.equals(parts[1])) {
                                    count++;
                                }
                            }
                            if (count == users.size()) // gives error that user is not present
                            {
                                throw new Exception();
                            }

                        } catch (Exception e) {
                            if (count > 0) {
                                System.out.println("ERROR: invalid user name");
                            } else if (count == -1) {
                                System.out.println("ERROR: invalid arguments");
                            }

                        }

                    }
                    if (chosen == 4) // "add" is entered
                    {
                        int count = 0; //check to see ehich type of error is to be displayed
                        try {
                            boolean iscardvalid = true; //checks if card entered is valid
                            String ccc = parts[2];
                            iscardvalid = luhnTest(ccc);  // true if card passes luhn test
                            if (!iscardvalid) {
                                count = 1;   //luhn test failed
                                throw new Exception();
                            }
                            if (cards.size() > 0) {
                                for (int i = 0; i < cards.size(); i++) {
                                    if (cards.get(i).credit.equals(ccc)) //check to see if card is already added to another user
                                    {
                                        iscardvalid = false;
                                    }
                                }
                                if (!iscardvalid) {
                                    count = 2;  //card already assigned
                                    throw new Exception();
                                }
                            }
                            if (parts[1] == null || parts[2] == null) {
                                count = 4;  //invalid arguments passed
                                throw new Exception();
                            }

                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).name.equals(parts[1]) && users.get(i).noofcard > 0) //checks if user already has a card
                                {
                                    count = 3;  //user already has a card
                                    throw new Exception();
                                }
                                if (users.get(i).name.equals(parts[1]) && iscardvalid && users.get(i).noofcard == 0) {
                                    CreditCard c = new CreditCard(ccc);  //creates a card
                                    users.get(i).card = c;  // assigns user the card
                                    cards.add(c);  //pushes the card to card list
                                    users.get(i).noofcard++;  //increment the user's number of cards

                                }
                            }
                        } catch (Exception e) {
                            if (count == 1) {
                                System.out.println("ERROR: this card is invalid");
                            }
                            if (count == 2) {
                                System.out.println("ERROR: that card has already been added by another user, reported for fraud!");
                            }
                            if (count == 3) {
                                System.out.println("ERROR: this user already has a valid credit card");
                            }
                            if (count == 4) {
                                System.out.println("ERROR: invalid arguments");
                            }

                        }
                    }

                    if (chosen == 2) // "pay" is entered
                    {
                        int count = 0; //keeps check on which error is present

                        String note = ""; // stores the note associated with the transaction
                        String payment = parts[3].substring(1, parts[3].length());  // creates the string out of the money separating the "$"

                        double payd = Double.parseDouble(payment);  // parses the string to double

                        for (int i = 4; i < parts.length; i++) {
                            note += parts[i] + " ";  // adds all the parts of the note entered
                        }

                        try {
                            if (parts[1].equals(parts[2])) {
                                count = 1;  // user tries to pay themselves
                                throw new Exception();
                            }

                            int p1 = 0; // increments till actor is not found
                            int p2 = 0; // increments till target is not found
                            int o1 = 0; // stores the position of the actor in users' list
                            int o2 = 0; // stores the position of the target in users' list
                            for (int i = 0; i < users.size(); i++) {
                                if (!users.get(i).name.equals(parts[1])) {
                                    p1++;
                                }

                                if (!users.get(i).name.equals(parts[2])) {
                                    p2++;
                                }

                                if (users.get(i).name.equals(parts[1])) {
                                    o1 = i;
                                }
                                if (users.get(i).name.equals(parts[2])) {
                                    o2 = i;
                                }
                            }

                            if (p1 == users.size() || p2 == users.size()) {
                                count = 2; // actor or target not found
                                throw new Exception();
                            }

                            if (users.get(o1).noofcard == 0) {
                                count = 3; // if actor does not have a card
                                throw new Exception();
                            }
                            if (parts[1] == null || parts[2] == null || parts[3] == null || parts[4] == null) {
                                count = 4; // if invalid arguments passed
                                throw new Exception();
                            }

                            users.get(o2).balance += payd; // increment the balance of the target

                            users.get(o2).transhist(users.get(o1).name, payd, note, true); // add the transaction to the target's history 
                            users.get(o1).transhist(users.get(o2).name, -payd, note, false); // add the transaction to the actor's history 

                        } catch (Exception e) {
                            if (count == 1) {
                                System.out.println("ERROR: users cannot pay themselves");
                            }
                            if (count == 2) {
                                System.out.println("ERROR: invalid user name");
                            }
                            if (count == 3) {
                                System.out.println("ERROR: this user does not have a credit card");
                            }
                            if (count == 4) {
                                System.out.println("ERROR: invalid arguments");
                            }
                        }
                    }

                    if (chosen == 3) // "feed" is entered 
                    {
                        int thisone = 0; // stores the position of user in users' list
                        int n = 0;
                        int count = 0; // keeps check on which error to display
                        try {
                            String findperson = parts[1]; // the user whose name is given
                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).name.equals(findperson)) {
                                    thisone = i;
                                }
                                if (users.get(i).name.equals(findperson)) {
                                    n++;
                                }
                            }
                            if (n == users.size()) {
                                count = 1;  // user is not found
                                throw new Exception();
                            }
                            if (users.get(thisone).nooftrans == 0) {
                                count = 2;  // user has no transaction
                                throw new Exception();
                            }
                            if (parts[1] == null) {
                                count = 3; // invalid argument passed
                                throw new Exception();
                            }
                            for (int i = 0; i < users.get(thisone).nooftrans; i++) {

                                if (users.get(thisone).trans.get(i).equals("paid to")) // shows the transaction in which account is debited
                                {
                                    System.out.println("You paid " + users.get(thisone).tran.get(i) + " $" + users.get(thisone).pays.get(i) + " for " + users.get(thisone).notess.get(i));
                                } else if (users.get(thisone).trans.get(i).equals("paid you")) // shows the transaction in which account is credited
                                {
                                    System.out.println(users.get(thisone).tran.get(i) + " paid you $" + users.get(thisone).pays.get(i) + " for " + users.get(thisone).notess.get(i));
                                }

                            }

                        } catch (Exception e) {

                            if (count == 1) {
                                System.out.println("ERROR: invalid user name");
                            }
                            if (count == 2) {
                                System.out.println("ERROR: user has done no transaction");
                            }
                            if (count == 3) {
                                System.out.println("ERROR: invalid arguments");
                            }
                        }
                    }

                }

                System.out.println("Enter the choice");
                ch = scc.next().charAt(0);
            }
        }
        if (takefile) {
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    
                    flag = 0;
                    int chosen = -1; //stores the command entered
                    User user;

                    String cmd = line; //stores the line extracted from file as command
                    String[] parts = cmd.split("\\s"); // creates different parts of the command given according to the space
                    try {
                        int count = 0;
                        for (int i = 0; i < 5; i++) {

                            if (!parts[0].equals(commands[i])) //used to check if command entered is present in the predefined commands
                            {
                                count++;
                            } else if (parts[0].equals(commands[i])) {
                                chosen = i;
                            }

                        }
                        if (count > 4) ///no valid command entered, so break to exception
                        {
                            flag = 1;
                            throw new Exception();
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR: command not recognized");
                    }
                    if (flag == 0) // valid command entered
                    {
                        if (chosen == 0) // "user" command entered
                        {
                            try {
                                boolean itisuser;
                                itisuser = parts[0].equals("user");

                                if (!parts[1].isEmpty() && parts[1] != null && itisuser) //checks validity of the "user" command and creates the user
                                {
                                    user = new User(parts[1]);  // creates a new user
                                    users.add(user);  //push the user to the users list

                                } else {
                                    throw new Exception(); // catches exception when just user is entered and not user <name>
                                }

                            } catch (Exception e) {
                                System.out.println("ERROR: invalid arguments");
                            }
                        }

                        if (chosen == 1) // "balance" is entered
                        {
                            int count = 0; //keeps count of which exception is caught
                            try {

                                if (parts[1] == null || parts[1].isEmpty()) //if no valid arguments given
                                {
                                    count = -1;
                                    throw new Exception();
                                }
                                for (int i = 0; i < users.size(); i++) {
                                    if (users.get(i).name.equals(parts[1])) {
                                        System.out.println("$ " + users.get(i).balance);

                                    }

                                    if (!users.get(i).name.equals(parts[1])) {
                                        count++;
                                    }
                                }
                                if (count == users.size()) // gives error that user is not present
                                {
                                    throw new Exception();
                                }

                            } catch (Exception e) {
                                if (count > 0) {
                                    System.out.println("ERROR: invalid user name");
                                } else if (count == -1) {
                                    System.out.println("ERROR: invalid arguments");
                                }

                            }

                        }
                        if (chosen == 4) // "add" is entered
                        {
                            int count = 0; //check to see ehich type of error is to be displayed
                            try {
                                boolean iscardvalid = true; //checks if card entered is valid
                                String ccc = parts[2];
                                iscardvalid = luhnTest(ccc);  // true if card passes luhn test
                                if (!iscardvalid) {
                                    count = 1;   //luhn test failed
                                    throw new Exception();
                                }
                                if (cards.size() > 0) {
                                    for (int i = 0; i < cards.size(); i++) {
                                        if (cards.get(i).credit.equals(ccc)) //check to see if card is already added to another user
                                        {
                                            iscardvalid = false;
                                        }
                                    }
                                    if (!iscardvalid) {
                                        count = 2;  //card already assigned
                                        throw new Exception();
                                    }
                                }
                                if (parts[1] == null || parts[2] == null) {
                                    count = 4;  //invalid arguments passed
                                    throw new Exception();
                                }

                                for (int i = 0; i < users.size(); i++) {
                                    if (users.get(i).name.equals(parts[1]) && users.get(i).noofcard > 0) //checks if user already has a card
                                    {
                                        count = 3;  //user already has a card
                                        throw new Exception();
                                    }
                                    if (users.get(i).name.equals(parts[1]) && iscardvalid && users.get(i).noofcard == 0) {
                                        CreditCard c = new CreditCard(ccc);  //creates a card
                                        users.get(i).card = c;  // assigns user the card
                                        cards.add(c);  //pushes the card to card list
                                        users.get(i).noofcard++;  //increment the user's number of cards

                                    }
                                }
                            } catch (Exception e) {
                                if (count == 1) {
                                    System.out.println("ERROR: this card is invalid");
                                }
                                if (count == 2) {
                                    System.out.println("ERROR: that card has already been added by another user, reported for fraud!");
                                }
                                if (count == 3) {
                                    System.out.println("ERROR: this user already has a valid credit card");
                                }
                                if (count == 4) {
                                    System.out.println("ERROR: invalid arguments");
                                }

                            }
                        }

                        if (chosen == 2) // "pay" is entered
                        {
                            int count = 0; //keeps check on which error is present

                            String note = ""; // stores the note associated with the transaction
                            String payment = parts[3].substring(1, parts[3].length());  // creates the string out of the money separating the "$"

                            double payd = Double.parseDouble(payment);  // parses the string to double

                            for (int i = 4; i < parts.length; i++) {
                                note += parts[i] + " ";  // adds all the parts of the note entered
                            }

                            try {
                                if (parts[1].equals(parts[2])) {
                                    count = 1;  // user tries to pay themselves
                                    throw new Exception();
                                }

                                int p1 = 0; // increments till actor is not found
                                int p2 = 0; // increments till target is not found
                                int o1 = 0; // stores the position of the actor in users' list
                                int o2 = 0; // stores the position of the target in users' list
                                for (int i = 0; i < users.size(); i++) {
                                    if (!users.get(i).name.equals(parts[1])) {
                                        p1++;
                                    }

                                    if (!users.get(i).name.equals(parts[2])) {
                                        p2++;
                                    }

                                    if (users.get(i).name.equals(parts[1])) {
                                        o1 = i;
                                    }
                                    if (users.get(i).name.equals(parts[2])) {
                                        o2 = i;
                                    }
                                }

                                if (p1 == users.size() || p2 == users.size()) {
                                    count = 2; // actor or target not found
                                    throw new Exception();
                                }

                                if (users.get(o1).noofcard == 0) {
                                    count = 3; // if actor does not have a card
                                    throw new Exception();
                                }
                                if (parts[1] == null || parts[2] == null || parts[3] == null || parts[4] == null) {
                                    count = 4; // if invalid arguments passed
                                    throw new Exception();
                                }

                                users.get(o2).balance += payd; // increment the balance of the target

                                users.get(o2).transhist(users.get(o1).name, payd, note, true); // add the transaction to the target's history 
                                users.get(o1).transhist(users.get(o2).name, payd, note, false); // add the transaction to the actor's history 

                            } catch (Exception e) {
                                if (count == 1) {
                                    System.out.println("ERROR: users cannot pay themselves");
                                }
                                if (count == 2) {
                                    System.out.println("ERROR: invalid user name");
                                }
                                if (count == 3) {
                                    System.out.println("ERROR: this user does not have a credit card");
                                }
                                if (count == 4) {
                                    System.out.println("ERROR: invalid arguments");
                                }
                            }
                        }

                        if (chosen == 3) // "feed" is entered 
                        {
                            int thisone = 0; // stores the position of user in users' list
                            int n = 0;
                            int count = 0; // keeps check on which error to display
                            try {
                                String findperson = parts[1]; // the user whose name is given
                                for (int i = 0; i < users.size(); i++) {
                                    if (users.get(i).name.equals(findperson)) {
                                        thisone = i;
                                    }
                                    if (users.get(i).name.equals(findperson)) {
                                        n++;
                                    }
                                }
                                if (n == users.size()) {
                                    count = 1;  // user is not found
                                    throw new Exception();
                                }
                                if (users.get(thisone).nooftrans == 0) {
                                    count = 2;  // user has no transaction
                                    throw new Exception();
                                }
                                if (parts[1] == null) {
                                    count = 3; // invalid argument passed
                                    throw new Exception();
                                }
                                for (int i = 0; i < users.get(thisone).nooftrans; i++) {

                                    if (users.get(thisone).trans.get(i).equals("paid to")) // shows the transaction in which account is debited
                                    {
                                        System.out.println("You paid " + users.get(thisone).tran.get(i) + " $" + users.get(thisone).pays.get(i) + " for " + users.get(thisone).notess.get(i));
                                    } else if (users.get(thisone).trans.get(i).equals("paid you")) // shows the transaction in which account is credited
                                    {
                                        System.out.println(users.get(thisone).tran.get(i) + " paid you $" + users.get(thisone).pays.get(i) + " for " + users.get(thisone).notess.get(i));
                                    }

                                }

                            } catch (Exception e) {

                                if (count == 1) {
                                    System.out.println("ERROR: invalid user name");
                                }
                                if (count == 2) {
                                    System.out.println("ERROR: user has done no transaction");
                                }
                                if (count == 3) {
                                    System.out.println("ERROR: invalid arguments");
                                }
                            }
                        }

                    }
                }
            } catch (IOException ex) {
                System.out.println("ERROR: file can not be read");
            }
        }
    }

    public void process() {

    }

    public static boolean luhnTest(String number) {
        int s1 = 0, s2 = 0;
        String reverse = new StringBuffer(number).reverse().toString();
        for (int i = 0; i < reverse.length(); i++) {
            int digit = Character.digit(reverse.charAt(i), 10);
            if (i % 2 == 0) {
                s1 += digit;
            } else {
                s2 += 2 * digit;
                if (digit >= 5) {
                    s2 -= 9;
                }
            }
        }
        return (s1 + s2) % 10 == 0;
    }

}
