package eps.scp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class Indexing {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        InvertedIndex hash;

        //if (args.length <2 || args.length>4)
           // System.err.println("Erro in Parameters. Usage: Indexing <TextFile> [<Key_Size>] [<Index_Directory>]");
        //if (args.length < 2)
          //  hash = new InvertedIndex(args[0]);
        //else
          //  hash = new InvertedIndex(args[0], Integer.parseInt(args[1]));

        //hash.BuildIndex();
        //args[0]  <TextFile>
        //args[1] <Threads_Number>"
        //args[2] [<Key_Size>]
        //args[3] [<Index_Directory>]"
        File f = new File("/home/pere/Indexing/test/example.txt");///home/pere/Indexing/test/example.txt

        int start = 0;
        int FileSize=(int)f.length();
        int NumByThreads = FileSize/Integer.parseInt(args[1]);
        int resto = FileSize-(NumByThreads * Integer.parseInt(args[1]));
        int end =0;
        for (int i = 0; i < Integer.parseInt(args[1]); i++) {
            if (i == Integer.parseInt(args[1])-1 && resto !=0 ){
                end = FileSize;
            }else{
                end += NumByThreads;
            }

            CocurrentIndexer ci = new CocurrentIndexer(start,end,Integer.parseInt(args[2]),FileSize,args[0] );
            Thread thread = new Thread(ci);
            thread.start();
            thread.join();

            preparar(start,end,Integer.parseInt(args[2]),FileSize );
            System.out.println("Return "+ci.getValue());


            start =end;
        }

        System.out.println("--------------MAIN--print index");

        //if (args.length > 2)
           // hash.SaveIndex(args[2]);
       /// else
           // hash.PrintIndex();
    }

    private static void preparar(int start, int end, int key,int FileSize) {
        //System.out.println(start+" "+end);
        int r = end-start;
        int m = r/2;
        int meitat_pos = start + m;
       // System.out.println("-->"+meitat_pos);
        int RealStart = meitat_pos-key+1;
        if (RealStart < 0){
            RealStart = 0;
        }
        int RealEnd = meitat_pos+key;
        if (RealEnd>FileSize){
            RealEnd = FileSize;
        }
        System.out.println(">"+RealStart+"-"+RealEnd);

    }

}
