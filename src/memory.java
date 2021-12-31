import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class memory {
    public static String MEMORY_FILE_PATH = "C:\\Users\\saryn\\Desktop\\GIU2\\Third\\Operating systems\\milestone3\\memory\\src\\MEMORYFILE.txt";
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

        Scanner MemoryFileScanner = new Scanner(new File(MEMORY_FILE_PATH));
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


//    public static Integer VariablesVirtualToPhysical(HashMap<Integer,Integer> segmentToBase,String Virtual) {
//            int segment = (Virtual.charAt(1) - '0');
//            int offset = Integer.parseInt(Virtual.substring(2));
//            System.out.println("virt "+Virtual);
//            System.out.println("phys "+ (segmentToBase.get(segment)+offset));
//            return segmentToBase.get(segment) + offset;
//    }

    // pass the physical address of the variable to be changed and the value you want to put.
    // just add the lines to a list, change in the list directly by accessing the line number,
    // basically the line number is the variable physical address so we access this line directly and re write data to the file.
    public static void modifyMemory(Integer VariablePhysical, Integer value) throws IOException {
        List<String> fileContent = new ArrayList<>(Files.readAllLines(Path.of(MEMORY_FILE_PATH), StandardCharsets.UTF_8));
        String newString = VariablePhysical + " " + value;
        fileContent.set(VariablePhysical,newString);
        Files.write(Path.of(MEMORY_FILE_PATH),fileContent,StandardCharsets.UTF_8);
    }

    public static String movDesVal(Integer destinationPhysical,
                                   Integer value,
                                    HashMap<Integer,String> addressToData) throws IOException {

        addressToData.put(destinationPhysical,value.toString());
        modifyMemory(destinationPhysical, value);
        return "RESULT " + value + " STORED AT LOCATION " + destinationPhysical;
    }


    public static String movDesSrc(Integer destinationPhysical,
                                   Integer srcPhysical,
                                   HashMap<Integer,String> addressToData) throws IOException {

        String ValueInsideSrc = addressToData.get(srcPhysical);
        addressToData.put(destinationPhysical,ValueInsideSrc);
        modifyMemory(destinationPhysical,Integer.parseInt(ValueInsideSrc));

        return "RESULT " + ValueInsideSrc + " STORED AT LOCATION " + destinationPhysical;
    }

    public static String Execute() throws IOException {
        HashMap<Integer,String> addressToData = GetMemory();
        HashMap<Integer,Integer> segmentToBase = GetSegmentToBase();
        ArrayList<Integer> InstructionsPhysicalAddress =  VirtualMemoToPhysical(segmentToBase);

        for (int i=0;i < InstructionsPhysicalAddress.size();i ++){
            int InsPhysAdd = InstructionsPhysicalAddress.get(i);
            String ans;
            String[] instructionAndAddress = addressToData.get(InsPhysAdd).replaceAll(",","").split(" ");
            //System.out.println(Arrays.toString(addressToData.get(InsPhysAdd).replaceAll(",", "").split(" ")));
            String Instruction = instructionAndAddress[0];
            // check the instruction
            if(Instruction.equals("mov")){
                Integer destinationPhysical =Integer.parseInt(instructionAndAddress[1].substring(1) );

                // move Value to specific Place in the memory (mov %des val)
                if(!instructionAndAddress[2].contains("%")){
                    Integer value = Integer.parseInt(instructionAndAddress[2]);
                    //System.out.println( "phys add , old data, new val"+ destinationPhysical + " " + addressToData.get(destinationPhysical)+  " " + instructionAndAddress[2]  );
                    ans = movDesVal(destinationPhysical,value,addressToData);
                }
                // move value in variable to another variable (mov %dest %source)
                else{
                    Integer srcPhysical = Integer.parseInt(instructionAndAddress[2].substring(1) );
                    //System.out.println( "phys add , old data, new data"+ destinationPhysical + " " + addressToData.get(destinationPhysical)+  " " + addressToData.get(srcPhysical));
                    ans = movDesSrc(destinationPhysical,srcPhysical,addressToData);
                }
            }

        }
        return "";
    }

    public static void main(String[] args) throws IOException {
        Execute();
    }
}
