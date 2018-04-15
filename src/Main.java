import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String str = "I1:IDENTIFIER";
        String str2 = "I1:IDENTIFIER";
        System.out.println(str.hashCode());
        System.out.println(str2.hashCode());
        System.out.println("I1:IDENTIFIER".hashCode());

    }
}
