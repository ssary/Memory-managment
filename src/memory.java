import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class memory {
    public static String filePath = new File("").getAbsolutePath();
    public static String MEMORY_FILE_PATH = filePath.concat("\\src\\MEMORYFILE.txt");


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
        Scanner sc = new Scanner(new File(filePath.concat("\\src\\INFOFILE.txt")));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] segmentToBaseSplit = line.split(" ");
            if(segmentToBaseSplit.length != 2){
                int segmentNumber = Integer.parseInt(segmentToBaseSplit[1]);
                int baseOfSegment = Integer.parseInt(segmentToBaseSplit[3]);
                SegmentToBase.put(segmentNumber,baseOfSegment);
            }
        }
        return SegmentToBase;
    }

    public static ArrayList<Integer> VirtualMemoToPhysical(HashMap<Integer,Integer> segmentToBase) throws FileNotFoundException {
        ArrayList<Integer> VirtToPhys = new ArrayList<>();
        Scanner sc = new Scanner(new File(filePath.concat("\\src\\VIRTUALMEMORY.txt")));
        while(sc.hasNextLine()){
            String Virtual = sc.nextLine();
            int segment = (Virtual.charAt(2) - '0');
            int offset = Integer.parseInt(Virtual.substring(3,Virtual.length()));
            int PhysicalAddress = segmentToBase.get(segment) + offset;
            VirtToPhys.add(PhysicalAddress);
        }
        return VirtToPhys;
    }

    public static ArrayList<String> VirtualAddresses() throws FileNotFoundException {
        ArrayList<String> VirtualAddress = new ArrayList<>();
        Scanner sc = new Scanner(new File(filePath.concat("\\src\\VIRTUALMEMORY.txt")));
        while(sc.hasNextLine()){
            String Virtual = sc.nextLine();
            Virtual = Virtual.substring(2);
            VirtualAddress.add(Virtual);
        }
        return VirtualAddress;
    }

    // pass the physical address of the variable to be changed and the value you want to put.
    // just add the lines to a list, change in the list directly by accessing the line number,
    // basically the line number is the variable physical address so we access this line directly and re write data to the file.
    public static void modifyMemory(Integer VariablePhysical, Integer value) throws IOException {
        List<String> fileContent = new ArrayList<>(Files.readAllLines(Path.of(MEMORY_FILE_PATH), StandardCharsets.UTF_8));
        String newString = VariablePhysical + " " + value;
        fileContent.set(VariablePhysical,newString);
        Files.write(Path.of(MEMORY_FILE_PATH),fileContent,StandardCharsets.UTF_8);
    }

    public static String ChangeValueInMemory(Integer destinationPhysical,
                                   Integer value,
                                    HashMap<Integer,String> addressToData) throws IOException {

        addressToData.put(destinationPhysical,value.toString());
        modifyMemory(destinationPhysical, value);
        return "RESULT " + value + " STORED AT LOCATION " + destinationPhysical;
    }

    public static void Execute() throws IOException {
        HashMap<Integer,String> addressToData = GetMemory();
        HashMap<Integer,Integer> segmentToBase = GetSegmentToBase();
        ArrayList<Integer> InstructionsPhysicalAddress =  VirtualMemoToPhysical(segmentToBase);
        List<String> outputList = new ArrayList<>();
        ArrayList<String> VirtualAdresses = VirtualAddresses();

        for (int i=0;i < InstructionsPhysicalAddress.size();i ++) {
            int InsPhysAdd = InstructionsPhysicalAddress.get(i);
            String ans = "FETCH VIRTUAL MEMORY " + VirtualAdresses.get(i) + " -> PHYSICAL MEMORY " + InsPhysAdd + '\n';
            ans += "EXECUTE " + addressToData.get(InsPhysAdd) + '\n';

            String[] instructionAndAddress = addressToData.get(InsPhysAdd).replaceAll(",", "").split(" ");
            System.out.println(Arrays.toString(addressToData.get(InsPhysAdd).replaceAll(",", "").split(" ")));
            String Instruction = instructionAndAddress[0];
            Integer destinationPhysical = Integer.parseInt(instructionAndAddress[1].substring(1));
            // check the instruction

            switch (Instruction) {
                case "mov":

                    // move Value to specific Place in the memory (mov %des val)
                    if (!instructionAndAddress[2].contains("%")) {
                        Integer value = Integer.parseInt(instructionAndAddress[2]);
                        ans += ChangeValueInMemory(destinationPhysical, value, addressToData);
                    }
                    // move value in variable to another variable (mov %dest %source)
                    else {
                        Integer srcPhysical = Integer.parseInt(instructionAndAddress[2].substring(1));
                        Integer value = Integer.parseInt(addressToData.get(srcPhysical));
                        ans += ChangeValueInMemory(destinationPhysical, value, addressToData);
                    }
                    break;
                case "add": {
                    Integer initValue = Integer.parseInt(addressToData.get(destinationPhysical));
                    Integer value = Integer.parseInt(instructionAndAddress[2]);
                    System.out.println(initValue + " " + value);
                    ans += ChangeValueInMemory(destinationPhysical, initValue + value, addressToData);
                    break;
                }
                case "sub": {
                    Integer initValue = Integer.parseInt(addressToData.get(destinationPhysical));
                    Integer value = Integer.parseInt(instructionAndAddress[2]);
                    ans += ChangeValueInMemory(destinationPhysical, initValue - value, addressToData);
                    break;
                }
                case "inc": {
                    int initValue = Integer.parseInt(addressToData.get(destinationPhysical));
                    ans += ChangeValueInMemory(destinationPhysical, initValue + 1, addressToData);
                    break;
                }
                case "dec": {
                    int initValue = Integer.parseInt(addressToData.get(destinationPhysical));
                    ans += ChangeValueInMemory(destinationPhysical, initValue - 1, addressToData);
                    break;
                }
            }
            outputList.add(ans);
            Files.write(Path.of(filePath.concat("\\src\\OUTPUT.txt")),
                    outputList,StandardCharsets.UTF_8);
        }
    }

    public static void main(String[] args) throws IOException {
        Execute();
    }
}
