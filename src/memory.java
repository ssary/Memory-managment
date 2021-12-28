import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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

    public static HashMap<Integer,String> GetMemory() throws FileNotFoundException {
        HashMap<Integer, String> addressToData = new HashMap<>();

        Scanner MemoryFileScanner = new Scanner(new File("C:\\Users\\saryn\\Desktop\\GIU2\\Third\\Operating systems\\milestone3\\memory\\src\\MEMORYFILE.txt"));
        while (MemoryFileScanner.hasNextLine()) {
            String PhysicalAddressToData = MemoryFileScanner.nextLine();
            String[] AddressToDataSplitted = PhysicalAddressToData.split(" ", 2);
            addressToData.put(Integer.parseInt(AddressToDataSplitted[0]), AddressToDataSplitted[1]);
        }
        return addressToData;
    }


    public static HashMap<Integer,Integer> GetSegmentToBase() throws FileNotFoundException {
        HashMap<Integer,Integer> SegmentToBase = new HashMap<>();
        Scanner sc = new Scanner(new File("C:\\Users\\saryn\\Desktop\\GIU2\\Third\\Operating systems\\milestone3\\memory\\src\\INFOFILE.txt"));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] segmentToBaseSplit = line.split(" ");
            if(segmentToBaseSplit.length != 2){
                int segmentNumber = Integer.parseInt(segmentToBaseSplit[1]);
                int baseOfSegment = Integer.parseInt(segmentToBaseSplit[3]);
                System.out.println(segmentNumber+ " " + baseOfSegment);
                SegmentToBase.put(segmentNumber,baseOfSegment);
            }
        }
        return SegmentToBase;
    }

    public static ArrayList<Integer> VirtualMemoToPhysical(HashMap<Integer,Integer> segmentToBase) throws FileNotFoundException {
        ArrayList<Integer> VirtToPhys = new ArrayList<>();
        Scanner sc = new Scanner(new File("C:\\Users\\saryn\\Desktop\\GIU2\\Third\\Operating systems\\milestone3\\memory\\src\\VIRTUALMEMORY.txt"));
        while(sc.hasNextLine()){
            String Virtual = sc.nextLine();
            int segment = (Virtual.charAt(2) - '0');
            int offset = Integer.parseInt(Virtual.substring(3,Virtual.length()));
            int PhysicalAddress = segmentToBase.get(segment) + offset;
            VirtToPhys.add(PhysicalAddress);
        }
        return VirtToPhys;
    }

    public static String Execute() throws FileNotFoundException {
        HashMap<Integer,String> addressToData = GetMemory();
        HashMap<Integer,Integer> segmentToBase = GetSegmentToBase();
        ArrayList<Integer> Instructions =  VirtualMemoToPhysical(segmentToBase);

        return "";
    }

    public static void main(String[] args) throws FileNotFoundException {
        Execute();
    }
}
