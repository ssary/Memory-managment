import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class memory {
    public static String readFile(String Path) {
        StringBuilder fileContent = new StringBuilder();
        try {

            File file = new File(Path);
            Scanner filereader = new Scanner(file);
            while (filereader.hasNextLine()) {
                fileContent.append(filereader.nextLine()).append('\n');
            }
            filereader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Not found");
            e.printStackTrace();
        }
        return fileContent.toString();
    }



}
