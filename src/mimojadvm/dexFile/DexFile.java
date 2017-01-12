package mimojadvm.dexFile;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DexFile {

    public static final int NO_INDEX = 0xffffffff;

    public Header mHeader;
    public ClassDef[] mClasses;
    public MethodID[] mMethods;
    public ProtoID[] mProtos;
    public TypeID[] mTypes;
    public StringDef[] mStrings;

    private String filePath;
    private byte[] fileContent;

    public DexFile(String path) throws IOException {
        filePath = path;
        File dexFile = new File(path);
        DataInputStream din = new DataInputStream(new BufferedInputStream(new FileInputStream(dexFile)));

        long length = dexFile.length();
        fileContent = new byte[(int) length];
        din.readFully(fileContent);

        readHeader(length);
        readMethods();
        readTypes();
        readStrings();
        readProtos();
        readClasses();

    }

    public ClassDef getMainClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getStringData(long id) {
        try {
            return mStrings[(int) id].string_content;
        } catch (ArrayIndexOutOfBoundsException e) {
            return "ERROR";
        }
    }

    private void readHeader(long length) throws IOException {

        mHeader = new Header();

        int offset[] = {0};

        // read Magic
        for (int i = 0; i < 8; i++) {
            mHeader.magic[i] = (char) fileContent[offset[0]];
            offset[0]++;
        }

        System.out.println();

        // read Checksum
        mHeader.checksum = readLong(offset) ^ 0xffffffff00000000L;

        // read Signature
        for (int i = 0; i < 20; i++) {
            mHeader.signature[i] = (char) fileContent[offset[0]++];
        }

        mHeader.file_size = readLong(offset);
        mHeader.header_size = readLong(offset);
        mHeader.endian_tag = readLong(offset);
        mHeader.link_size = readLong(offset);
        mHeader.link_off = readLong(offset);
        mHeader.map_off = readLong(offset);
        mHeader.string_ids_size = readLong(offset);
        mHeader.string_ids_off = readLong(offset);
        mHeader.type_ids_size = readLong(offset);
        mHeader.type_ids_off = readLong(offset);
        mHeader.proto_ids_size = readLong(offset);
        mHeader.proto_ids_off = readLong(offset);
        mHeader.field_ids_size = readLong(offset);
        mHeader.field_ids_off = readLong(offset);
        mHeader.method_ids_size = readLong(offset);
        mHeader.method_ids_off = readLong(offset);
        mHeader.class_defs_size = readLong(offset);
        mHeader.class_defs_off = readLong(offset);
        mHeader.data_size = readLong(offset);
        mHeader.data_off = readLong(offset);

        if (!mHeader.isValid(length)) {
            System.exit(-1);
        }
    }

    private void readClasses() throws IOException {
        int classNum = (int) mHeader.class_defs_size;
        mClasses = new ClassDef[classNum];

        int offset[] = {(int) mHeader.class_defs_off};
        int dataOffset[] = new int[1];

        for (int i = 0; i < classNum; i++) {
            mClasses[i] = new ClassDef();
            mClasses[i].class_idx = readLong(offset);
            mClasses[i].access_flags = readLong(offset);
            mClasses[i].superclass_idx = readLong(offset);
            mClasses[i].interfaces_off = readLong(offset);
            mClasses[i].source_file_idx = readLong(offset);
            mClasses[i].annotations_off = readLong(offset);
            mClasses[i].class_data_off = readLong(offset);
            mClasses[i].static_values_off = readLong(offset);

            dataOffset[0] = (int) mClasses[i].class_data_off;
            mClasses[i].static_fields_size = readUnsignedLeb128(dataOffset);

        }
    }

    private void readMethods() throws IOException {
        int metNum = (int) mHeader.method_ids_size;
        mMethods = new MethodID[metNum];

        int offset[] = {(int) mHeader.method_ids_off};

        for (int i = 0; i < metNum; i++) {
            mMethods[i] = new MethodID();
            mMethods[i].class_idx = readShort(offset);
            mMethods[i].proto_idx = readShort(offset);
            mMethods[i].name_idx = readLong(offset);
        }

    }

    private void readTypes() throws IOException {
        int typeNum = (int) mHeader.type_ids_size;
        mTypes = new TypeID[typeNum];

        int offset[] = {(int) mHeader.type_ids_off};

        for (int i = 0; i < typeNum; i++) {
            mTypes[i] = new TypeID();
            mTypes[i].descriptor_idx = readLong(offset);
        }
    }

    private void readStrings() throws IOException {
        int typeNum = (int) mHeader.string_ids_size;
        mStrings = new StringDef[typeNum];
        System.out.println("Expected " + typeNum + " Strings");
        int offset[] = {(int) mHeader.string_ids_off};

        for (int i = 0; i < typeNum; i++) {
            mStrings[i] = new StringDef();
            mStrings[i].string_data_off = readLong(offset);

            int data_offset[] = {(int) mStrings[i].string_data_off};

            int len = readUnsignedLeb128(data_offset);

            char data[] = new char[len];
            for (int y = 0; y < len; y++) {
                data[y] = (char) fileContent[data_offset[0]];
                data_offset[0]++;
            }

            mStrings[i].string_content = new String(data);
        }
    }

    private void readProtos() throws IOException {
        int typeNum = (int) mHeader.proto_ids_size;
        mProtos = new ProtoID[typeNum];

        int offset[] = {(int) mHeader.proto_ids_off};

        for (int i = 0; i < typeNum; i++) {
            mProtos[i] = new ProtoID();
            mProtos[i].descriptor_idx = readLong(offset);
        }
    }

    private long readLong(int[] offset) throws IOException {
        int off = offset[0];
        int byte1 = fileContent[off + 3] & 0xff;
        int byte2 = fileContent[off + 2] & 0xff;
        int byte3 = fileContent[off + 1] & 0xff;
        int byte4 = fileContent[off] & 0xff;
        long res = byte1 << 24 | byte2 << 16 | byte3 << 8 | byte4;
        offset[0] += 4;
        return res;
    }

    private int readShort(int[] offset) throws IOException {
        int off = offset[0];
        int byte1 = fileContent[off] & 0xff;
        int byte2 = fileContent[off + 1] & 0xff;
        int res = byte2 << 8 | byte1;
        offset[0] += 2;
        return res;
    }

    private int readSignedLeb128(int[] offset) {
        int result = 0;
        int cur;
        int count = 0;
        int signBits = -1;
        do {
            cur = fileContent[offset[0]] & 0xff;
            offset[0]++;
            result |= (cur & 0x7f) << (count * 7);
            signBits <<= 7;
            count++;
        } while (((cur & 0x80) == 0x80) && count < 5);
        if ((cur & 0x80) == 0x80) {
            return -1;
        }
        // Sign extend if appropriate
        if (((signBits >> 1) & result) != 0) {
            result |= signBits;
        }
        return result;
    }

    private int readUnsignedLeb128(int[] offset) {
        int result = 0;
        int cur;
        int count = 0;
        do {
            cur = fileContent[offset[0]] & 0xff;
            offset[0]++;
            result |= (cur & 0x7f) << (count * 7);
            count++;
        } while (((cur & 0x80) == 0x80) && count < 5);
        if ((cur & 0x80) == 0x80) {
            return -1;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("|| Dex file: %s\n", filePath));
        builder.append(String.format(("|| DEX magic: ")));
        for (int i = 0; i < 8; i++) {
            builder.append(String.format("0x%X ", (byte) mHeader.magic[i]));
        }

        builder.append("\n");

        builder.append(String.format("|| Adler32 checksum: 0x%x\n", mHeader.checksum));

        builder.append("|| SHA1 signature: ");
        for (int i = 0; i < 20; i++) {
            builder.append(String.format("%02x", (byte) mHeader.signature[i]));
        }
        builder.append("\n");

        builder.append(String.format("|| File size: %d bytes\n", mHeader.file_size));
        builder.append(String.format("|| DEX Header size: %d bytes (0x%x)\n", mHeader.header_size, mHeader.header_size));
        builder.append(String.format("|| Endian Tag: 0x%x\n", mHeader.endian_tag));
        builder.append(String.format("|| Link size: %d\n", mHeader.link_size));
        builder.append(String.format("|| Link offset: 0x%x\n", mHeader.link_off));
        builder.append(String.format("|| Map list offset: 0x%x\n", mHeader.map_off));
        builder.append(String.format("|| Number of strings in string ID list: %d\n", mHeader.string_ids_size));
        builder.append(String.format("|| String ID list offset: 0x%x\n", mHeader.string_ids_off));
        for (int i = 0; i < mStrings.length; i++) {
            builder.append(String.format("|| String %d \n", i));
            builder.append(String.format("\tstring_data_off='0x%x'\n", mStrings[i].string_data_off));
            builder.append(String.format("\tstring_content='%s'\n", mStrings[i].string_content));
        }
        builder.append(String.format("|| Number of types in the type ID list: %d\n", mHeader.type_ids_size));
        builder.append(String.format("|| Type ID list offset: 0x%x\n", mHeader.type_ids_off));
        for (int i = 0; i < mTypes.length; i++) {
            builder.append(String.format("|| Type %d \n", i));
            builder.append(String.format("\tdescriptor_idx='0x%x'\n", mTypes[i].descriptor_idx));
            builder.append(String.format("\t\tstring_content='%s'\n", getStringData(mTypes[i].descriptor_idx)));
        }
        builder.append(String.format("|| Number of items in the method prototype ID list: %d\n", mHeader.proto_ids_size));
        builder.append(String.format("|| Method prototype ID list offset: 0x%x\n", mHeader.proto_ids_off));
        for (int i = 0; i < mProtos.length; i++) {
            builder.append(String.format("|| Method Prototype %d \n", i));
            builder.append(String.format("\tdescriptor_idx='0x%x'\n", mProtos[i].descriptor_idx));
            builder.append(String.format("\t\tstring_content='%s'\n", getStringData(mProtos[i].descriptor_idx)));
        }
        builder.append(String.format("|| Number of item in the field ID list: %d\n", mHeader.field_ids_size));
        builder.append(String.format("|| Field ID list offset: 0x%x\n", mHeader.field_ids_off));
        //TODO parse fields
        builder.append(String.format("|| Number of items in the method ID list: %d\n", mHeader.method_ids_size));
        builder.append(String.format("|| Method ID list offset: 0x%x\n", mHeader.method_ids_off));
        for (int i = 0; i < mMethods.length; i++) {
            builder.append(String.format("|| Method %d \n", i));
            builder.append(String.format("\tclass_idx='0x%x'\n", mMethods[i].class_idx));
            builder.append(String.format("\tproto_idx='0x%x'\n", mMethods[i].proto_idx));
            builder.append(String.format("\tname_idx='0x%x'\n", mMethods[i].name_idx));
            builder.append(String.format("\t\tstring_content='%s'\n", getStringData(mMethods[i].name_idx)));
        }
        builder.append(String.format("|| Number of items in the class definitions list: %d\n", mHeader.class_defs_size));
        builder.append(String.format("|| Class definitions list offset: 0x%x\n", mHeader.class_defs_off));
        builder.append(String.format("|| Data section size: %d bytes\n", mHeader.data_size));
        builder.append(String.format("|| Data section offset: 0x%x\n", mHeader.data_off));
        for (int i = 0; i < mClasses.length; i++) {
            builder.append(String.format("|| Class %d \n", i));
            builder.append(String.format("\tclass_idx='0x%x'\n", mClasses[i].class_idx));
            builder.append(String.format("\taccess_flags='0x%x'\n", mClasses[i].access_flags));
            builder.append(String.format("\tsuperclass_idx='0x%x'\n", mClasses[i].superclass_idx));
            builder.append(String.format("\tinterfaces_off='0x%x'\n", mClasses[i].interfaces_off));
            builder.append(String.format("\tsource_file_idx='0x%x'\n", mClasses[i].source_file_idx));
            builder.append(String.format("\tannotations_off=0x%x\n", mClasses[i].annotations_off));
            builder.append(String.format("\tclass_data_off=0x%x (%d)\n", mClasses[i].class_data_off, mClasses[i].class_data_off));
            builder.append(String.format("\tstatic_values_off=0x%x (%d)\n", mClasses[i].static_values_off, mClasses[i].static_values_off));

            if (mClasses[i].class_data_off == 0) {
                builder.append(String.format("\t\t0 static fields\n"));
                builder.append(String.format("\t\t0 instance fields\n"));
                builder.append(String.format("\t\t0 direct methods\n"));
            } else {
                builder.append(String.format("\t\t0 static_fields_size %d\n", mClasses[i].static_fields_size));

            }
        }

        return builder.toString();

    }
}
