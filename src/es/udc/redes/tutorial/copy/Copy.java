package es.udc.redes.tutorial.copy;
import java.io.*;

public class Copy {
    public static void main(String[] args) throws IOException {
        FileInputStream inputFile = null;
        FileOutputStream outputFile = null;

        try {
            inputFile = new FileInputStream(args[0]);
            outputFile = new FileOutputStream(args[1]);
            int aux;

            aux = inputFile.read();

            while(aux != -1){
                outputFile.write(aux);
            }
        }
        finally {
            if(inputFile != null){
                inputFile.close();
            }
            if(outputFile != null){
                outputFile.close();
            }
        }

    }
}
