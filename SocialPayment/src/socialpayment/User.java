
package socialpayment;

import java.util.ArrayList;

/**
 *
 * @author Devesh
 */
public class User {
    String name;  //name of the user
    double balance; // stores the balance of the user
    CreditCard card; // stores the card assigned to the user 
    int noofcard; // stores the number of cards of the user
    int nooftrans; // stores the number of transactions of the user
  
    ArrayList<String> tran=new ArrayList();  // stores the target or the actor of a specific transaction in which the user is involved
    ArrayList<Double> pays=new ArrayList();  // stores the values of the transactions of the user
    ArrayList<String> trans=new ArrayList();  // stores the method of the transaction i.e credit or debit
    ArrayList<String> notess= new ArrayList();  // store the notes of the transactions
   
    User()
    {
        
    }
    public User (String name)
    {
        if(name.length()<4||name.length()>15)
        {
            System.out.println("ERROR: Invalid Name");
            return;
        }
        this.name=name;
        this.balance=0;
        this.noofcard=0;
        this.nooftrans=0;
    }
   
    public void transhist(String paidto, double amount, String note,boolean mode)
    {
        // checks the mode 
        // if it is false : debit happened
        // if it true: credit happened
        
        if(mode==true)    
        {
            this.tran.add(paidto);
            this.trans.add("paid you");
            this.pays.add(amount);
            this.notess.add(note);
          
        }
        else
        {
            this.tran.add(paidto);
            this.trans.add("paid to");
            this.pays.add(amount);
            this.notess.add(note);
           
        }
        this.nooftrans++; // number of transactions of a user increased
    }
    
    public void addCard(CreditCard ccard)
    {
        this.card=ccard;  // card assigned
    }
}
