package main.java.org.LinearAlgebruh;

/**
 * Eine Klasse für dreidimensionalen Vektoren
 */
public class Vector3{

    /**
     * Die Werte des Vektores
     */
    private float[] value;

    /**
     * Erzeugt einen neuen Nullvektor
     */
    public Vector3(){
        value=new float[3];
        value[0]=0;
        value[1]=0;
        value[2]=0;
    }

    /**
     * Erzeugt einen neuen Vektor
     * @param x erster Wert des Vektores
     * @param y zweiter Wert des Vektores
     * @param z dritter Wert des Vektores
     */
    public Vector3(float x, float y, float z){
        value=new float[3];
        value[0]=x;
        value[1]=y;
        value[2]=z;
    }

    /**
     * Zurückgibt einen Wert des Vektores
     * @param index der Index des gewünschten Wertes
     * @return der gewünschte Wert
     */
    public float get(int index){
        assert index<3&&index>=0;
        return value[index];
    }

    /**
     * Stellt der Wert mit dem gewünschten Index ein
     * @param index der Index des einzustellenen Wertes
     * @param value der neue Wert
     */
    public void set(int index, float value){
        assert index<3&&index>=0;
        this.value[index]=value;
    }

    /**
     * Kopiert die Werte des Vektores
     * @return eine identische Kopie des Vektores
     */
    public Vector3 copy(){
        return new Vector3(value[0], value[1], value[2]);
    }

    /**
     * Konvertiert die Werte des Vektores zum Text
     * @return Textform des Vektores
     */
    @Override
    public String toString(){
        return "("+value[0]+"; "+value[1]+"; "+value[2]+")";
    }

    //static
    /**
     * Nullvektor
     */
    public static final Vector3 zero=new Vector3(0,0,0);
    /**
     * (-1;0;0)
     */
    public static final Vector3 right =new Vector3(-1, 0,0);
    /**
     * (0;1;0)
     */
    public static final Vector3 up=new Vector3(0,1,0);
    /**
     * (0;0;1)
     */
    public static final Vector3 forward=new Vector3(0,0,1);

    /**
     * Gibt das Quadrat der Länge des Vektores zurück
     * @param vec Der untersuchte Vektor
     * @return das Quadrat der Länge des Vektores
     */
    public static float sqrMagnitude(Vector3 vec){
        return (vec.value[0]*vec.value[0]+vec.value[1]*vec.value[1]+vec.value[2]*vec.value[2]);
    }
    /**
     * Gibt die Länge des Vektores zurück
     * @param vec Der untersuchte Vektor
     * @return die Länge des Vektores
     */
    public static float magnitude(Vector3 vec){
        return (float)Math.sqrt(vec.value[0]*vec.value[0]+vec.value[1]*vec.value[1]+vec.value[2]*vec.value[2]);
    }

    /**
     * Normalisiert den Vektor
     * @param vec der Vektor, der normalisiert werden soll
     */
    public static void normalize(Vector3 vec){
        float temp=magnitude(vec);
        vec.value[0]/=temp;
        vec.value[1]/=temp;
        vec.value[2]/=temp;
    }

    /**
     * Gibt das Skalarprodukt zweier Vektoren zurück
     * @param a erster Vektor
     * @param b zweiter Vektor
     * @return das Skalarprodukt von a und b
     */
    public static float dotProduct(Vector3 a, Vector3 b){
        return (a.value[0]*b.value[0]+a.value[1]*b.value[1]+a.value[2]*b.value[2]);
    }

    /**
     * Gibt das Kreuzprodukt zweier Vektoren zurück
     * @param a erster Vektor
     * @param b zweiter Vektor
     * @return das Kreuzprodukt von a und b
     */
    public static Vector3 crossProduct(Vector3 a, Vector3 b){
        return new Vector3(
                a.value[1]*b.value[2]-a.value[2]*b.value[1],
                a.value[2]*b.value[0]-a.value[0]*b.value[2],
                a.value[0]*b.value[1]-a.value[1]*b.value[0]);
    }

    /**
     * Multipliziert ein Vektor mit einem Skalar und gibt eine Kopie zurück
     * @param num das Skalar
     * @param vec der Vektor
     * @return der multiplizierte Vektor
     */
    public static Vector3 multiplyWithScalar(float num, Vector3 vec){
        return new Vector3(num*vec.value[0],num*vec.value[1],num*vec.value[2]);
    }

    /**
     * Multipliziert ein Vektor mit einer Matrix und gibt eine Kopie zurück
     * @param neo die Matrix
     * @param vec der Vektor
     * @return der multiplizierte Vektor
     */
    public static Vector3 multiplyWithMatrix(Matrix3 neo, Vector3 vec){
        float[][] neoValues=neo.getValuesByReference();
        return new Vector3(
                neoValues[0][0]*vec.value[0]+neoValues[0][1]*vec.value[1]+neoValues[0][2]*vec.value[2],
                neoValues[1][0]*vec.value[0]+neoValues[1][1]*vec.value[1]+neoValues[1][2]*vec.value[2],
                neoValues[2][0]*vec.value[0]+neoValues[2][1]*vec.value[1]+neoValues[2][2]*vec.value[2]
        );
    }

    /**
     * Summiert zwei Vektoren und gibt eine Kopie zurück
     * @param a erster Vektor
     * @param b zweiter Vektor
     * @return die Summe von a und b
     */
    public static Vector3 sum(Vector3 a, Vector3 b){
        return new Vector3(
                a.value[0]+b.value[0],
                a.value[1]+b.value[1],
                a.value[2]+b.value[2]
        );
    }

    /**
     * Subtrahiert einen Vektor von einem anderen
     * @param a der Opfer des Subtrahierens
     * @param b der subtrahierte Vektor
     * @return a-b
     */
    public static Vector3 difference(Vector3 a, Vector3 b){
        return new Vector3(
                a.value[0]-b.value[0],
                a.value[1]-b.value[1],
                a.value[2]-b.value[2]
        );
    }

    /**
     * Untersucht, ob alle Werte von einem Vektor größer sind als den entsprechenden Werten eines anderen Vektores
     * @param a soll größer sein
     * @param b soll kleiner sein
     * @return TRUE, fallse a ist größer als b
     */
    public static boolean isAGreaterThanB(Vector3 a, Vector3 b){
        if(a.value[0]>b.value[0]&&a.value[1]>b.value[1]&&a.value[2]>b.value[2])
            return true;
        return false;
    }

    /**
     * Berechnet den Durchschnitt mehrer Vektoren
     * @param vex ein Array von Vektoren
     * @param count die Anzahl der Vektoren im Array, total überflüßig
     * @return der Durchschnitt den Vektoren in vex. Nullvektor, falls count==0
     */
    public static Vector3 avg(Vector3[] vex, int count){
        float x=0,y=0,z=0;

        for(int i=0;i<count;i++){
            x+=vex[i].value[0];
            y+=vex[i].value[1];
            z+=vex[i].value[2];
        }

        if(count!=0){
            x/=count;
            y/=count;
            z/=count;
        }

        return new Vector3(x,y,z);
    }

    /**
     * Lineare Interpolation zwischen zwei Vektoren
     * @param a erster Vektor
     * @param b zweiter Vektor
     * @param i Stärke der Interpolation
     * @return a+(b-a)*i
     */
    public static Vector3 lerp(Vector3 a, Vector3 b, float i){
        return new Vector3(
                a.value[0]+(b.value[0]-a.value[0])*i,
                a.value[1]+(b.value[1]-a.value[1])*i,
                a.value[2]+(b.value[2]-a.value[2])*i
        );
    }
}
