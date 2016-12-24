import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public final class ChatUsers extends JFrame implements ActionListener
{
    String username;
    PrintWriter pw;
    ObjectInputStream ois;
    JTextArea chatmsg;
    JTextField chatip;
    JButton send, exit;
    Socket chatusers;
    int e, n;
    public ChatUsers(String uname, String servername) throws Exception
    {
        super(uname);
        this.username = uname;
        chatusers = new Socket(servername,80);
        ois = new ObjectInputStream(chatusers.getInputStream());
        pw = new PrintWriter(chatusers.getOutputStream(), true);
        pw.println(uname);
        buildInterface();
        new MessageThread().start();
    }
    public void buildInterface()
    {
        send = new JButton("Wyślij");
        exit = new JButton("Wyjdź");
        chatmsg = new JTextArea();
        chatmsg.setRows(30);
        chatmsg.setColumns(50);
        chatip = new JTextField(50);
        JScrollPane sp = new JScrollPane(chatmsg,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(sp,"Center");
        JPanel bp = new JPanel(new FlowLayout());
        bp.add(chatip);
        bp.add(send);
        bp.add(exit);
        bp.setBackground(Color.LIGHT_GRAY);
        bp.setName("Chat RSA");
        add(bp, "North");
        send.addActionListener(this);
        exit.addActionListener(this);
        setSize(500,300);
        setVisible(true);
        pack();
    }
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        if(evt.getSource()==exit)
        {
            pw.println("end");
            System.exit(0);
        }
        else
        {
            pw.println(chatip.getText());
            chatmsg.append(username + ": " + chatip.getText() + "\n");
            chatip.setText(null);
        }
    }
    public static void main(String ... args)
    {
        String SetUserName = JOptionPane.showInputDialog(null, "Podaj swoje imię: ", "Chat RSA", JOptionPane.PLAIN_MESSAGE);
        String servername = "localhost";
        try
        {
            new ChatUsers(SetUserName, servername);
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    
    class MessageThread extends Thread
    {
        @Override
        public void run()
        {
            String message, user;
            try
            {
                while(true)
                {
                    Message mess = (Message) ois.readObject();
                    message = mess.message;
                    user = mess.user;
                    e = mess.e;
                    n = mess.n;
                    chatmsg.append(user + ": " + message + "e= " + e + "n= " + n +"\n");
                }
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }
}