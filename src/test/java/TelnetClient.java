package test.java;

import java.net.*;
import java.io.*;

class TelnetClient
{
    public static void main(String args[]) throws Exception
    {
        Socket soc=new Socket("127.0.0.1",1234);
        String LoginName;
        String Password;
        String Command;
        
        
        DataInputStream din=new DataInputStream(soc.getInputStream());        
        DataOutputStream dout=new DataOutputStream(soc.getOutputStream());
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Welcome to Telnet Client");
        System.out.println("Your Credential Please...");
        System.out.print("Login Name :");

        LoginName=br.readLine();
        
        System.out.print("Password :");
        Password=br.readLine();
        
        dout.writeUTF(LoginName);
        dout.writeUTF(Password);
        dout.flush();
        if (din.readUTF().equals("ALLOWED"))
        {
            do
            {
            System.out.print("< Telnet Prompt >");
            Command=br.readLine();            
            dout.writeUTF(Command);
            dout.flush();
            if(!Command.equals("quit"))
            {
                System.out.println(din.readUTF());        
            }                
            }while(!Command.equals("quit"));
        }
        soc.close();        
    }
}