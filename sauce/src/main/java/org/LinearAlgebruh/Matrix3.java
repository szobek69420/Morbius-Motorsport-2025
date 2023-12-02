package main.java.org.LinearAlgebruh;

/**
 * Klasse für 3x3 Matrizen
 */
public class Matrix3 implements Cloneable{
    /**
     * Die Werte der Matrix.
     * Erster Index ist die Zeilennummer und der zweite ist die Spaltennummer.
     */
    private float[][] value;

    /**
     * Erzeugte eine neue Matrixinstanz. Es wird eine Einheitsmatrix.
     */
    public Matrix3(){
        value=new float[3][3];
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(i==j)
                    continue;
                value[i][j]=0;
            }
            value[i][i]=1;
        }
    }

    /**
     * Erzeugt eine Matrixinstanz mit den gegebenen Werten. Die Werte werden nicht kopiert.
     * @param value die Werte der neuen Matrix
     */
    public Matrix3(float[][] value){
        this.value=value;
    }

    /**
     * Erzeugt eine Matrixinstanz mit der gegebenen Spaltenvektoren. Die Werte der Vektoren werden kopiert.
     * @param vec0 erster Spaltenvektor
     * @param vec1 zweiter Spaltenvektor
     * @param vec2 dritter Spaltenvektor
     */
    public Matrix3(Vector3 vec0, Vector3 vec1, Vector3 vec2){
        value=new float[3][3];
        for (int i=0;i<3;i++){
            value[i][0]=vec0.get(i);
            value[i][1]=vec1.get(i);
            value[i][2]=vec2.get(i);
        }
    }

    /**
     * Überschreibt die clone-Funktion des Cloneable-Interfaces.
     * @return Die kopierte Matrix.
     */
    @Override
    public Object clone(){
        return new Matrix3(this.getValues());
    }

    /**
     * Kopiert die Werte der Matrix in einen neuen zweidimensionalen Array
     * @return das neu erzeugte Array
     */
    public float[][] getValues(){
        float[][] copy=new float[3][3];
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                copy[i][j]=value[i][j];
            }
        }

        return copy;
    }

    /**
     * Zurückgibt die unkopierte Werte der Matrix
     * @return die unkopierte Werte der Matrix
     */
    public float[][] getValuesByReference(){
        return value;
    }

    /**
     * Konvertiert den Inhalt der Matrix zum Text
     * @return Textform der Matrix
     */
    @Override
    public String toString(){
        return value[0][0]+" "+value[0][1]+" "+value[0][2]+"\n"+value[1][0]+" "+value[1][1]+" "+value[1][2]+"\n"+value[2][0]+" "+value[2][1]+" "+value[2][2];
    }

    //static

    /**
     * Eine statische Funktion, die den Determinant einer Matrix berechnet
     * @param neo die untersuchte Matrix
     * @return die Determinant der Matrix
     */
    public static float determinant(Matrix3 neo){
        float[][] values=neo.getValuesByReference();

        return (values[0][0]*(values[1][1]*values[2][2]-values[1][2]*values[2][1])
                - values[0][1]*(values[1][0]*values[2][2]-values[1][2]*values[2][0])
                + values[0][2]*(values[1][0]*values[2][1]-values[1][1]*values[2][0]));
    }

    /**
     * Eine statische Funktion, die das Transponierte einer Matrix berechnet
     * @param neo die untersuchte Matrix
     */
    public static void transpose(Matrix3 neo){
        float[][] values=neo.getValuesByReference();
        float temp;
        for(int i=0;i<3;i++){
            for(int j=0;j<i;j++) {
                temp=values[i][j];
                values[i][j]=values[j][i];
                values[j][i]=temp;
            }
        }
    }

}
