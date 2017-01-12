package mimojadvm.dexFile;

import java.util.Hashtable;

public class ClassDef {

    public long class_idx;
    public long access_flags;
    public long superclass_idx;
    public long interfaces_off;
    public long source_file_idx;
    public long annotations_off;
    public long class_data_off;
    
    
    public long static_values_off;
    public long static_fields_size;
    public long instance_fields_size;
    public long direct_methods_size;
    public long virtual_methods_size;

}
