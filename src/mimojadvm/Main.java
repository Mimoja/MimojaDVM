package mimojadvm;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        String dexFile = "Foo1.dex";
        VirtualMachine vm = new VirtualMachine(dexFile);
        vm.run();
    }
    
}
