package mimojadvm;

import java.io.IOException;
import mimojadvm.dexFile.ClassDef;
import mimojadvm.dexFile.DexFile;

public class VirtualMachine {

    private DexFile mDexFile;

    public VirtualMachine(String path) throws IOException {
        mDexFile = new DexFile(path);
        System.out.println(mDexFile.toString());
    }
    
    public void run(){
        //ClassDef mainClazz = mDexLoader.getMainClass();
    }
}
