package fr.insalyon.p2i2.javaarduino;

/**
 *
 * @author imoudden
 */
public class Mesure {
    
    public int compt;
    public double pression;
    public char id;
    
    
    public Mesure(int co, double p, char c){
        this.compt=co;
        this.pression=p;
        this.id=c;
    }
    
    public String toString(){
        return "Caractere : "+id+" Pression :"+pression+" Compteur :"+compt;
    }
    
}

