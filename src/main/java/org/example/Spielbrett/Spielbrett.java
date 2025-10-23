package org.example.Spielbrett;
import javax.smartcardio.Card;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Spielbrett {

    private int breite;
    private int hoehe;
    private Spielkarte [][] karten;
    private int Eckkarten;
    private int Kreuzungskarten;
    private int Geradekarten;
    private Spielkarte spareCard;
    private enum direction {
        LEFT, RIGHT, UP, DOWN
    };


    public Spielbrett(int breite, int hoehe, int Eckkarten, int Kreuzungskarten, int Geradekarten) {
        if(Eckkarten + Kreuzungskarten + Geradekarten != (breite*hoehe +1))
         {
            System.out.println("Fehler: Kartenanzahl stimmt nicht mit Spielfeldgröße überein");
            return;
         };

        this.breite = breite;
        this.hoehe = hoehe;
        this.Eckkarten = Eckkarten;
        this.Kreuzungskarten = Kreuzungskarten;
        this.Geradekarten = Geradekarten;
        karten = new Spielkarte[breite][hoehe];


        karten[0][0] = new Spielkarte(Spielkarte.Wegtyp.ECKE, 90 );
        karten[0][2] = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, 90);
        karten[0][4] = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, 90);
        karten[0][6] = new Spielkarte(Spielkarte.Wegtyp.ECKE, 180);

        karten[2][0] = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, 0);
        karten[2][2] = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, 0);
        karten[2][4] = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, 90);
        karten[2][6] = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, 180);

        karten[4][0] = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, 0);
        karten[4][2] = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, 270);
        karten[4][4] = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, 180);
        karten[4][6] = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, 180);

        karten[6][0] = new Spielkarte(Spielkarte.Wegtyp.ECKE, 0);
        karten[6][2] = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, 270);
        karten[6][4] = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, 270);
        karten[6][6] = new Spielkarte(Spielkarte.Wegtyp.ECKE, 270);

        // Erstellen eines Arrays mit den restlichen Karten
        int Kartenspeicher= Eckkarten-4 + Kreuzungskarten-12 + Geradekarten;
        Integer[] kartenArray = new Integer[Kartenspeicher];

        for (int i = 0; i < Eckkarten-4; i++)
            kartenArray[i] = 0;
         for (int i = 0; i < Kreuzungskarten-12; i++)
             kartenArray[i+ Eckkarten-4] = 1;
         for (int i = 0; i < Geradekarten; i++)
             kartenArray[i + Eckkarten-4+ Kreuzungskarten-12] = 2;

         List<Integer> list = Arrays.asList(kartenArray);
         // Liste mischen
         Collections.shuffle(list);
         // Zurück in Array (optional)
         kartenArray = list.toArray(new Integer[0]);

        // Füllen des Spielfelds mit den restlichen Karten
        int kartenTypIndex = 0;
        for (int i=0; i<hoehe; i++) {
            for (int j = 0; j < breite; j++) {
                if (karten[i][j] == null)
                    karten[i][j]=setSpielkarteRandomized(kartenArray, kartenTypIndex++ );
            }
        }
        spareCard = setSpielkarteRandomized(kartenArray, kartenTypIndex );

        //Schätze über das Spielfeld verstreuen
         //24 Schatzkarten in Array
        Integer[] Schatznummer= new Integer[24];
        //49 Plätze am Spielfeld in Array
        Integer[] Platznummer= new Integer[49];
         //Schatznummern initialisieren
         for (int i=0; i<24; i++)
             Schatznummer[i]=i;
         //Platznummern initialisieren
         for (int i=0; i<49; i++)
             Platznummer[i]=i;

         // Schatznummern in eine Liste umwandeln
         List<Integer> list2 = Arrays.asList(Schatznummer);
         // Schatznummern Liste mischen
         Collections.shuffle(list2);
         Schatznummer = list2.toArray(new Integer[0]);

         // PlatznummernIn eine Liste umwandeln
         list2 = Arrays.asList(Platznummer);
         // Platznummern Liste mischen
         Collections.shuffle(list2);
         Platznummer = list2.toArray(new Integer[0]);

         //Schatzkarten durchgehen und auf Platzkarten mappen
         int reihen=0, spalten=0, intKarteTyp;
         for (int i=0; i<24; i++) {
             reihen = Platznummer[i] / 7;
             spalten = Platznummer[i] % 7;
             karten[reihen][spalten].setSchatzTyp( Schatznummer[i]);
         }
    };


    public void setSparecard(Spielkarte newSpareCard){
        this.spareCard = newSpareCard;
    }


    private Spielkarte setSpielkarteRandomized(Integer[] kartenArray, int kartenTypIndex )
    {
        Spielkarte card = new Spielkarte();
        Random random = new Random();
        switch (kartenArray[kartenTypIndex]) {
            case 0:
                int randEcke = random.nextInt(4);
                card= new Spielkarte(Spielkarte.Wegtyp.ECKE, randEcke*90);
                break;
            case 1:
                int randKreuz = random.nextInt(4);
                card = new Spielkarte(Spielkarte.Wegtyp.KREUZUNG, randKreuz*90);
                break;
            case 2:
                int randgerade = random.nextInt(2);
                card = new Spielkarte(Spielkarte.Wegtyp.GERADE, randgerade*90);
                break;
        }
        return card;
    }


    public Object[][] getKarten() {

        return karten;
    }
    public Spielkarte getSpareCard() {
        return spareCard;
    }
    public void printBrett() {
        int reihen = 0, spalten = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(karten[i][j].getWegSymbol() + " ");
            }
            System.out.println();
        }
        System.out.print("Spare");
        System.out.println(spareCard);
    }
    public String printLine(int line){
        int reihen=0, spalten=0;
        String ZeilenString="";
        if(line>=0 || line<=6) {
            for (int j = 0; j < 7; j++) {
                ZeilenString = ZeilenString+karten[line][j].getWegSymbol() ;
            }
            return ZeilenString;
        };
        return " ";
    };
    public String imageNamePlate(char symbol) {
        switch (symbol) {
            case '╔':
                return "ecke_BC";
            case '╗':
                return "ecke_CD";
            case '╝':
                return "ecke_AD";
            case '╚':
                return "ecke_AB";
            case '═':
                return "gerade_BD";
            case '║':
                return "gerade_AC";
            case '╠':
                return "kreuzung_ABC";
            case '╦':
                return "kreuzung_BCD";
            case '╣':
                return "kreuzung_ACD";
            case '╩':
                return "kreuzung_ABD";
            default:
                return null;
        }
    }
    public Spielkarte pushPlate(int row, int col, Spielkarte newPlate, int direction){
        Spielkarte temp;
        switch(direction) {
            case 1: //left
                System.out.println("Push left");
                // Logik zum Schieben der Platte nach links implementieren
                 temp = karten[row][0];
                for(int j = 0; j < breite - 1; j++) {
                    karten[row][j] = karten[row][j + 1];
                }
                karten[row][breite-1] = newPlate;
                return temp;
                //break;
            case 2: //right
                System.out.println("Push right");
                // Logik zum Schieben der Platte nach rechts implementiere
                temp = karten[row][breite-1];
                for(int k = breite - 1; k > 0; k--){
                    karten[row][k] = karten[row][k - 1];
                }
                karten[row][0] = newPlate;
                return temp;
                //break;
            case 3: //up
                System.out.println("Push up");
                // Logik zum Schieben der Platte nach oben implementieren
                temp = karten[0][col];
                for(int l = 0 ; l <hoehe - 1; l++){
                    karten[l][col] = karten[l+1][col];
                }
                karten[hoehe-1][col] = newPlate;
                return temp;
                //break;


            case 4: //down
                System.out.println("Push down");
                // Logik zum Schieben der Platte nach unten implementieren
                temp = karten[hoehe-1][col];
                for(int l = hoehe-1 ; l>0 ; l--){
                    karten[l][col] = karten[l-1][col];
                }
                karten[0][col] = newPlate;
                return temp;
                //break;



            default:
                throw new IllegalArgumentException("Ungültige Richtung zum Schieben der Platte.");

        }

        //return temp;

        };
    public void rotatePlate (){
       int rotation  = spareCard.getOrientierung();
       rotation = (rotation + 90) % 360;
       spareCard.setOrientierung(rotation);
        System.out.println("Spare Card rotated to " + rotation);
    }



}
