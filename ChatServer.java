import java.io.*;
import java.util.*;
import java.net.*;
import java.math.*;
import static java.lang.System.out;

public class ChatServer {
    public static void main(String ... args) throws Exception
    {
        new ChatServer().createserver();
    }
    
    Vector<String> users = new Vector<String>();
    Vector<Manageuser> clients = new Vector<Manageuser>();
    public void createserver() throws Exception
    {
        ServerSocket server = new ServerSocket(80,10);
        out.println("Serwer włączony");
        while(true)
        {
            Socket client = server.accept();
            Manageuser c = new Manageuser(client);
            clients.add(c);
        }
    }
    public void sendtoall(String user, String message)
    {
        for(Manageuser c : clients)
        {
            if(!c.getchatusers().equals(user))
            {
                c.sendMessage(user,message);
            }
        }
    }
    
    class Manageuser extends Thread
    {
        String gotuser = "";
        BufferedReader input;
        PrintWriter output;
        public Manageuser(Socket client) throws Exception
        {
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(),true);
            gotuser = input.readLine();
            users.add(gotuser);
            start();
        }
        public void sendMessage(String chatuser, String chatmsg)
        {
            output.println(chatuser + ": " + chatmsg);
        }
        public String getchatusers(){return gotuser;}
        public void run()
        {
            String line;
            try
            {
                while(true)
                {
                    line = input.readLine();
                    if(line.equals("end"))
                    {
                        clients.remove(this);
                        users.remove(gotuser);
                        break;
                    }
                    kluczRSA();
                    sendtoall(gotuser,line);
                }
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }        
        }
    }
    
    public void kluczRSA() 
    {
        int p, q, phi, n, e, d;
        p = RandPrime();
        q = RandPrime();
        System.out.println("p = " + p);
        System.out.println("q = " + q);
        
        phi = (p - 1) * (q - 1);
        n = p * q;
        
        for(e = 3; NWD(e,phi) != 1; e += 2);
        d = Euklides(e,phi);
        
        System.out.println("e = " + e);
        System.out.println("n = " + n);
        System.out.println("d = " + d);
    }

    int RandPrime() 
    {
        Random generator = new Random();
        int x=0;
        boolean prime;
        
        x = generator.nextInt(20)+1;
        do 
        {
            prime = true;
            for (int i = 2; i*i <= x; i++)
            {
                if(x%i == 0)
                {
                    prime = false;
                }
            }
            if (prime == false)
            {
                x=x+1;
            }
        }while(!prime);  
        return x;
    }
    
    int NWD(int a, int b) 
    {
        int t;
        while (b != 0) 
        {
            t = b;
            b = a % b;
            a = t;
        };
        return a;
    }
    
    int Euklides(int a, int n) 
    {
        int p0, p1, n0, a0, q, r, t;
        p0 = 0;
        p1 = 1;
        n0 = n;
        a0 = a;
        q = n0 / a0;
        r = n0 % a0;
        while (r > 0) 
        {
            t = p0 - q * p1;
            if (t >= 0) 
            {
                t = t % n;
            } 
            else 
            {
                t = n - ((-t) % n);
            }
            p0 = p1;
            p1 = t;
            n0 = a0;
            a0 = r;
            q = n0 / a0;
            r = n0 % a0;
        };
        return p1;
    }
}
