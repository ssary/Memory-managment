import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
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

    public static HashMap<Long,String> GetMemory() throws FileNotFoundException {
        HashMap<Long, String> addressToData = new HashMap<>();

        Scanner MemoryFileScanner = new Scanner(new File("MEMORYFILE.txt"));
        while (MemoryFileScanner.hasNextLine()) {
            String PhysicalAddressToData = MemoryFileScanner.nextLine();
            String[] AddressToDataSplitted = PhysicalAddressToData.split(" ", 2);
            addressToData.put(Long.parseLong(AddressToDataSplitted[0]), AddressToDataSplitted[1]);
        }
        return addressToData;
    }


    public static String Execute() throws FileNotFoundException {
        HashMap<Long,String> addressToData = GetMemory();

    }

    public static void main(String[] args) {

    }
}
