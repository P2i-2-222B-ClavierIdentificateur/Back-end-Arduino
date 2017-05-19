package fr.insalyon.p2i2.javaarduino;

import fr.insalyon.p2i2.javaarduino.util.Console;
import fr.insalyon.p2i2.javaarduino.usb.ArduinoUsbChannel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import jssc.SerialPortException;

public class TestArduino 
{
    
    public static void main( String[] args )
    {   
        final Console console = new Console();
        
        
        console.log( "DEBUT du programme TestArduino !.." );
        
        String port = null;
        
        /*do {
        
            console.log( "RECHERCHE d'un port disponible..." );
            port = ArduinoUsbChannel.getOneComPort();
            
            if (port == null) {
                console.log( "Aucun port disponible!" );
                console.log( "Nouvel essai dans 5s" );
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    // Ignorer l'Exception
                }
            }

        } while (port == null);*/
        
        port = "COM3";
        
        console.println("Connection au Port " + port);
        try {

            final ArduinoUsbChannel vcpChannel = new ArduinoUsbChannel(port);

            Thread readingThread = new Thread(new Runnable() {

                public void run() {
                    Mesure [] tabMesures=new Mesure [100];
                    int rang=0;
                    BufferedReader vcpInput = new BufferedReader(new InputStreamReader(vcpChannel.getReader()));
                    String line;
                    try {

                        while ((line = vcpInput.readLine()) != null && rang <100) {
                           insertionTab (line, tabMesures , rang);
                           rang++ ; 
                           console.println("Data from Arduino: " + line);  
                        }
                        Mesure[] tabTrie = triTab (tabMesures);
                        afficherTabTriee (tabTrie); 
                        

                    } catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    }
                    
                }
            });
            
            readingThread.start();
            
            vcpChannel.open();
            
            boolean exit = false;
            
            while (!exit) {
            
                String line = console.readLine("Envoyer une ligne (ou 'fin') > ");
            
                if (line.length() == 0) {
                    continue;
                }
                
                if ("fin".equals(line)) {
                    exit = true;
                    continue;
                }
                
                vcpChannel.getWriter().write(line.getBytes("UTF-8"));
                vcpChannel.getWriter().write('\n');
            
            }
            
            vcpChannel.close();
            
            readingThread.interrupt();
            try {
                readingThread.join(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
            }

        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        } catch (SerialPortException ex) {
            ex.printStackTrace(System.err);
        }
        
        
        
    }


    public static void insertionTab(String s, Mesure [] tab,int rang){
        int nbretiret=0;
        char ident='t';
        String pression="0";
        String compteur="0";
        boolean idtrouve=false;
        for (int i = 0; i < s.length(); i++) {
            if(s.charAt(i)=='_'){nbretiret++;}
            else {if(nbretiret==0){ident=s.charAt(i);}
            if(nbretiret==1){pression=pression+s.charAt(i);}
            if(nbretiret==2){compteur=compteur+s.charAt(i);}
            }
       }
        
      int cmpt=Integer.parseInt(compteur);
      double p=Double.parseDouble(pression);
      
      Mesure m=new Mesure(cmpt,p,ident);
      tab[rang]=m;
        
        
        
    }
    
    public static Mesure[] triTab(Mesure[] t){
        Mesure[] m=new Mesure [30];
        int j=0;
        int cmpt=t[0].compt;
        double pres=t[0].pression;
        char ident=t[0].id;
        
        for(int i=0;i<t.length;i++){
            if(t[i].compt==cmpt && t[i].id==ident){
                if(t[i].pression>=pres){
                    pres=t[i].pression;
                }
                
            }
            else{
                m[j]=new Mesure(cmpt,pres,ident);
                j++;
                ident=t[i].id;
                pres=t[i].pression;
                cmpt=t[i].compt;
            }
        }
        m[j]=new Mesure(cmpt,pres,ident);
        return m;
    }
    
    public static void afficherTabTriee (Mesure [] t){
        for (int i = 0 ; i< t.length ; i++){
            System.out.println(t[i]);
        }
    }
}


