package org.example.Spielbrett;

import java.util.HashMap;
import java.util.Map;

public class Spielkarte {

    public enum Wegtyp {
        GERADE, ECKE, KREUZUNG
    };

    private int Schatz=0;
    private Wegtyp kartenType;
    private int Lage=0;
    public Spielkarte() {
    };
    public Spielkarte(Wegtyp type, int Lage) {
        this.kartenType = type;
        this.Lage = Lage;
    };
    public int setOrientierung( int Lage) {


            if(Lage == 0||
                    Lage == 90||
                    Lage==180||
                    Lage==270){

                this.Lage = Lage;
                return Lage;
            }

        return 1;
    }
    public int getOrientierung() {
        return Lage;
    }
    public void setSchatzTyp(int Schatz) {
        this.Schatz = Schatz;
    }
    public char getWegSymbol() {
        switch(this.kartenType    ) {
            case Wegtyp.GERADE:
                switch(this.Lage) {
                    case 0 :
                        return '║';
                    case 180:
                        return '║';
                    case 90:
                        return '═';
                    case 270:
                        return '═';
                    default:
                        return '☻';
                }

            case Wegtyp.ECKE:
                switch(this.Lage) {
                    case 0:
                        return '╚';
                    case 90:
                        return '╔';
                    case 180:
                        return '╗';
                    case 270:
                        return '╝';
                    default:
                        return '☻';
                }
            case Wegtyp.KREUZUNG:
                switch (this.Lage)
                {
                    case 0:
                        return '╠';
                    case 90:
                        return '╦';
                    case 180:
                        return '╣';
                    case 270:
                        return '╩';
                    default:
                        return '☻';
                }
                default:
                    return '☻';
        }
    }


    // ✅ Override toString()
    @Override
    public String toString() {
        return String.format(
                "Spielkarte [Typ=%s, Ausrichtung=%s, Schatz=%d]",
                kartenType, Lage, Schatz
        );
    }

}
