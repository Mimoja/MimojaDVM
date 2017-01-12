package mimojadvm.dexFile;

public class AcessFlag {

    public static final int FLAG_PUBLIC = 0x1;
    public static final int FLAG_PRIVATE = 0x2;
    public static final int FLAG_PROTECTED = 0x4;
    public static final int FLAG_STATIC = 0x8;
    public static final int FLAG_FINAL = 0x10;
    public static final int FLAG_SYNCHRONIZED = 0x20;
    public static final int FLAG_VOLATILE = 0x40;
    public static final int FLAG_BRIDGE = 0x40;
    public static final int FLAG_TRANSIENT = 0x80;
    public static final int FLAG_VARARGS = 0x80;
    public static final int FLAG_NATIVE = 0x100;
    public static final int FLAG_INTERFACE = 0x200;
    public static final int FLAG_ABSTRACT = 0x400;
    public static final int FLAG_STRICT = 0x800;
    public static final int FLAG_SYNTHETIC = 0x1000;
    public static final int FLAG_ANNOTATION = 0x2000;
    public static final int FLAG_DECLARED_SYNCHRONIZED = 0x2000;
    public static final int FLAG_ENUM = 0x4000;
    public static final int FLAG_UNUSED = 0x8000;
    public static final int FLAG_CONSTRUCTOR = 0x10000;

}
