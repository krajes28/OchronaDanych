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
    int n, e, d;
    int[] t;
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
    public void sendtoall(String user, String message, int[] key) throws IOException
    {
        for(Manageuser c : clients)
        {
            if(!c.getchatusers().equals(user))
            {
                c.sendMessage(user,message, key);
            }
        }
    }
    
    class Manageuser extends Thread
    {
        String gotuser = "";
        BufferedReader input;
        ObjectOutputStream oos;
        public Manageuser(Socket client) throws Exception
        {
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            oos = new ObjectOutputStream(client.getOutputStream());
            gotuser = input.readLine();
            users.add(gotuser);
            start();
        }
        public void sendMessage(String chatuser, String chatmsg, int[] chatkey) throws IOException
        {
            Message mes = new Message();
            mes.user = chatuser;
            mes.message = chatmsg;
            mes.d = d;
            mes.n = n;
            mes.key = chatkey;
            oos.writeObject(mes);
            oos.flush();
            //oos.close();
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
                    t = new int[line.length()];
                    if(line.equals("end"))
                    {
                        clients.remove(this);
                        users.remove(gotuser);
                        break;
                    }
                    kluczRSA();
                    System.out.println("e = " + e);
                    System.out.println("n = " + n);
                    System.out.println("d = " + d);
                    t = kodowanieRSA(line,e,n);
                    for(int i=0; i<line.length(); i++)
                    {
                        System.out.println(" " + t[i]);
                    }
                    sendtoall(gotuser,line,t);
                }
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }        
        }
    }
    public int[] kodowanieRSA(String message, int e, int n)
    {
        int pot,wyn,q;
        int[] key = new int[message.length()];
        for(int i=0; i<message.length(); i++)
        {
            char c = message.charAt(i);
            pot = (byte) c; wyn = 1;
            for(q = e; q > 0; q /= 2)
            {
              if((q % 2) == 1)
                  wyn = (wyn * pot) % n;
              pot = (pot * pot) % n; // kolejna potęga
            };
            key[i] = wyn;
        }
        
        
        
        return key;
    }
    public void kluczRSA() 
    {
        int p, q, phi;
        p = RandPrime();
        do{
            q = RandPrime();
        }while (p==q);
        
        System.out.println("p = " + p);
        System.out.println("q = " + q);
        
        phi = (p - 1) * (q - 1);
        n = p * q;
        for(e = 3; NWD(e,phi) != 1; e += 2);
        d = Euklides(e,phi);
    }

    int RandPrime() 
    {
        Random generator = new Random();
        int x=0;
        boolean prime;
        
        x = generator.nextInt(13)+1;
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
            if (x==1)
            {
                prime = false;
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