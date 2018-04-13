import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) {
        try{
            File file = new File("F:\\output.txt");
            PrintWriter pw = new PrintWriter(file);
            pw.println("~~~~~~~~~~~~~~~~~~~");
            pw.flush();
            pw.close();
        }catch (IOException e){
            e.printStackTrace();
        }


    }
}
