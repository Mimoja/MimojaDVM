package mimojadvm.dexFile;

public class Header {

    private class expected {

        public static final String magicNumber = "dex\n035\0";
        public static final String version = "035";
        public static final long header_size = 0x70;
        public static final long endian = 0x12345678;
        public static final long link_size = 0x00;
        public static final long link_off = 0x00;

    }

    public char[] magic;
    public long checksum;
    public char[] signature;
    public long file_size;
    public long header_size;
    public long endian_tag;
    public long link_size;
    public long link_off;
    public long map_off;
    public long string_ids_size;
    public long string_ids_off;
    public long type_ids_size;
    public long type_ids_off;
    public long proto_ids_size;
    public long proto_ids_off;
    public long field_ids_size;
    public long field_ids_off;
    public long method_ids_size;
    public long method_ids_off;
    public long class_defs_size;
    public long class_defs_off;
    public long data_size;
    public long data_off;

    public Header() {
        magic = new char[expected.magicNumber.length()];
        signature = new char[20];

    }

    public boolean isValid(long fileSize) {
        if (!new String(magic).equals(expected.magicNumber)) {
            System.out.format("Invalid header magic: %s! Aborting", new String(magic));
            return false;
        }
        if (file_size != fileSize) {
            System.out.format("File corrupted! Size: %d expected %d Aborting", file_size, fileSize);
            return false;
        }
        if (header_size != expected.header_size) {
            System.out.println("header corrupted! Aborting");
            return false;
        }
        if (endian_tag != expected.endian) {
            System.out.println("endianness wrong! Aborting");
            return false;
        }
        if (link_size != expected.link_size) {
            System.out.format("Warning: link offset wrong 0x%02x! Ignoring", link_size);
            return false;
        }
        if (link_off != expected.link_off) {
            System.out.format("Warning: link offset wrong 0x%02x ! Ignoring", link_off);
        }
        return true;
    }
}
