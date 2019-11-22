package eps.scp;
import com.google.common.collect.HashMultimap;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.List;

public class CocurrentIndexer implements Runnable{
    private final int DKeySize = 10;            // Tamaño de la clave/ k-word por defecto.
    private final int DIndexMaxNumberOfFiles = 1000;   // Número máximio de ficheros para salvar el índice invertido.
    private final String DIndexFilePrefix = "/IndexFile";   // Prefijo de los ficheros de Índice Invertido.
    private final float DMinimunMatchingPercentage = 0.80f;  // Porcentaje mínimo de matching entre el texto original y la consulta (80%)
    private final int DPaddingMatchText = 20;   // Al mostrar el texto original que se corresponde con la consulta se incrementa en 20 carácteres


    // Members
    private String InputFilePath;       // Contiene la ruta del fichero a Indexar.
    private RandomAccessFile randomInputFile;  // Fichero random para acceder al texto original con mayor porcentaje de matching.
    private int KeySize;            // Número de carácteres de la clave (k-word)
    private HashMultimap<String, Long> Hash = HashMultimap.create();    // Hash Map con el Índice Invertido.

    //Myfields
    private int FileSize;
    private int start;
    private int end;

    public CocurrentIndexer(int start, int end, int KeySize, int FileSize,String inputFile) {
        this.InputFilePath = inputFile;
        this.KeySize = KeySize;
        this.FileSize = FileSize;
        this.start = start;
        this.end =  end;
    }

    @Override
    public void run() {
        FileInputStream is;
        long offset = -1;
        int car;
        String key="";
        System.out.println("\nIN BUILD INDEX FUNCTION\nKEY SIZE = "+ KeySize);

        try {
            File file = new File(InputFilePath);
            is = new FileInputStream(file);
            // Leer fichero  a indexar carácter a carácter-
            for (int i = start; i < end  && (car = is.read())!=-1; i++) {
                System.out.println("Car Read = "+(char)car);
                offset++;
                if (car=='\n' || car=='\r' || car=='\t') {
                    // Sustituimos los carácteres de \n,\r,\t en la clave por un espacio en blanco.



                    if (key.length()==KeySize && key.charAt(KeySize-1)!=' ')
                        key = key.substring(1, KeySize) + ' ';
                    System.out.println("key sin  \\n,\\r,\\t  ===> " + key);
                    continue;
                }


                if (key.length()<KeySize){
                    // Si la clave es menor de K, entonces le concatenamos el nuevo carácter leído.
                    key = key + (char) car;}
                else{
                    // Si la clave es igua a K, entonces eliminaos su primier carácter y le concatenamos el nuevo carácter leído (implementamos una slidding window sobre el fichero a indexar).
                    String subs =key.substring(1, KeySize);
                    System.out.println("temp = " + subs + " + "+(char) car);
                    key = subs + (char) car;
                    System.out.println("key sliding windows = " + key);
                }


                if (key.length()==KeySize){
                    // Si tenemos una clave completa, la añadimos al Hash, junto a su desplazamiento dentro del fichero.
                    long value =offset-KeySize+1;
                    String s = "\n-----ADD HASHTABLE----------\n"+"Key="+key+"\n value="+value
                            +"\n-----------------------------\n";
                    System.out.println(s);
                    AddKey(key, value);
                }
            }

            /*while((car = is.read())!=-1)
            {
                System.out.println("Car Read = "+(char)car);
                offset++;
                if (car=='\n' || car=='\r' || car=='\t') {
                    // Sustituimos los carácteres de \n,\r,\t en la clave por un espacio en blanco.



                    if (key.length()==KeySize && key.charAt(KeySize-1)!=' ')
                        key = key.substring(1, KeySize) + ' ';
                    System.out.println("key sin  \\n,\\r,\\t  ===> " + key);
                    continue;
                }


                if (key.length()<KeySize){
                    // Si la clave es menor de K, entonces le concatenamos el nuevo carácter leído.
                    key = key + (char) car;}
                else{
                    // Si la clave es igua a K, entonces eliminaos su primier carácter y le concatenamos el nuevo carácter leído (implementamos una slidding window sobre el fichero a indexar).
                    String subs =key.substring(1, KeySize);
                    System.out.println("temp = " + subs + " + "+(char) car);
                    key = subs + (char) car;
                    System.out.println("key sliding windows = " + key);
                }


                if (key.length()==KeySize){
                    // Si tenemos una clave completa, la añadimos al Hash, junto a su desplazamiento dentro del fichero.
                    long value =offset-KeySize+1;
                    String s = "\n-----ADD HASHTABLE----------\n"+"Key="+key+"\n value="+value
                            +"\n-----------------------------\n";
                    System.out.println(s);
                    AddKey(key, value);
                }
            }*/
            is.close();

        } catch (FileNotFoundException fnfE) {
            System.err.println("Error opening Input file.");
        }  catch (IOException ioE) {
            System.err.println("Error read Input file.");
        }







    }






    public HashMultimap<String, Long> getValue(){
        PrintIndex();
        return Hash;
    }
    // Método que añade una k-word y su desplazamiento en el HashMap.
    private void AddKey(String key, long offset){
        Hash.put(key, offset);
        System.out.print(offset+"\t-> "+key+"\r");
    }
    public void PrintIndex() {
        Set<String> keySet = Hash.keySet();
        Iterator keyIterator = keySet.iterator();
        while (keyIterator.hasNext() ) {
            String key = (String) keyIterator.next();
            System.out.print(key + "\t");
            Collection<Long> values = Hash.get(key);
            for(Long value : values){
                System.out.print(value+",");
            }
            System.out.println();
        }
    }
}
