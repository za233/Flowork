package io.github.r1mao;

import org.objectweb.asm.Opcodes;

public class DataType {
    public final static int VOID = -1, BOOLEAN = 0, BYTE = 1, CHAR = 2, DOUBLE = 3, FLOAT = 4, INTEGER = 5, LONG = 6, REFERENCE = 7, SHORT = 8;
    private final int slotSize;
    private final int type;
    public final static DataType TYPE_VOID = new DataType(VOID, 0, null);
    public final static DataType TYPE_BOOLEAN = new DataType(BOOLEAN, 4, null);
    public final static DataType TYPE_BYTE = new DataType(BYTE, 4, null);
    public final static DataType TYPE_CHAR = new DataType(CHAR, 4, null);
    public final static DataType TYPE_DOUBLE = new DataType(DOUBLE, 8, null);
    public final static DataType TYPE_FLOAT = new DataType(FLOAT, 4, null);
    public final static DataType TYPE_INTEGER = new DataType(INTEGER, 4, null);
    public final static DataType TYPE_LONG = new DataType(LONG, 8, null);
    public final static DataType TYPE_SHORT = new DataType(SHORT, 4, null);
    public final static DataType TYPE_REFERENCE = new DataType(REFERENCE, 4, null);

    private DataType(int type, int slotSize, String extraData) {
        this.type = type;
        this.slotSize = slotSize;
    }

    public int getSizeInSlot() {
        return this.slotSize;
    }

    public int getTypeId() {
        return this.type;
    }

    public String getTypeName() {
        return DataType.getTypeName(this);
    }

    public static String getTypeName(DataType type) {
        switch (type.getTypeId()) {
            case VOID:
                return "void";
            case BOOLEAN:
                return "boolean";
            case BYTE:
                return "byte";
            case CHAR:
                return "char";
            case DOUBLE:
                return "double";
            case FLOAT:
                return "float";
            case INTEGER:
                return "int";
            case LONG:
                return "long";
            case SHORT:
                return "short";
            case REFERENCE:
                return "object";
            default:
                return null;
        }
    }

    public static DataType getTypeByTypeName(String name) {
        switch (name) {
            case "void":
                return DataType.TYPE_VOID;
            case "boolean":
                return DataType.TYPE_BOOLEAN;
            case "byte":
                return DataType.TYPE_BYTE;
            case "char":
                return DataType.TYPE_CHAR;
            case "double":
                return DataType.TYPE_DOUBLE;
            case "float":
                return DataType.TYPE_FLOAT;
            case "int":
                return DataType.TYPE_INTEGER;
            case "long":
                return DataType.TYPE_LONG;
            case "short":
                return DataType.TYPE_SHORT;
            case "object":
                return DataType.TYPE_REFERENCE;
            default:
                return null;
        }
    }

    public static DataType getTypeByArrayTypeID(int id) {
        switch (id) {
            case Opcodes.T_BOOLEAN:
                return DataType.TYPE_BOOLEAN;
            case Opcodes.T_BYTE:
                return DataType.TYPE_BYTE;
            case Opcodes.T_CHAR:
                return DataType.TYPE_CHAR;
            case Opcodes.T_SHORT:
                return DataType.TYPE_SHORT;
            case Opcodes.T_FLOAT:
                return DataType.TYPE_FLOAT;
            case Opcodes.T_INT:
                return DataType.TYPE_INTEGER;
            case Opcodes.T_LONG:
                return DataType.TYPE_LONG;
            case Opcodes.T_DOUBLE:
                return DataType.TYPE_DOUBLE;
        }
        return null;
    }

    public static DataType getTypeByObject(Object obj) {
        ;
        String typeName = obj.getClass().getTypeName();
        if (typeName.equals("java.lang.Integer"))
            return DataType.TYPE_INTEGER;
        else if (typeName.equals("java.lang.Boolean"))
            return DataType.TYPE_BOOLEAN;
        else if (typeName.equals("java.lang.Double"))
            return DataType.TYPE_DOUBLE;
        else if (typeName.equals("java.lang.Float"))
            return DataType.TYPE_FLOAT;
        else if (typeName.equals("java.lang.Long"))
            return DataType.TYPE_LONG;
        else if (typeName.equals("java.lang.Byte"))
            return DataType.TYPE_BYTE;
        else if (typeName.equals("java.lang.Short"))
            return DataType.TYPE_SHORT;
        else if (typeName.equals("java.lang.Char"))
            return DataType.TYPE_CHAR;
        else
            return DataType.TYPE_REFERENCE;
    }

    public static DataType getType(int type) {
        switch (type) {
            case VOID:
                return TYPE_VOID;
            case BOOLEAN:
                return TYPE_BOOLEAN;
            case BYTE:
                return TYPE_BYTE;
            case CHAR:
                return TYPE_CHAR;
            case DOUBLE:
                return TYPE_DOUBLE;
            case FLOAT:
                return TYPE_FLOAT;
            case INTEGER:
                return TYPE_INTEGER;
            case LONG:
                return TYPE_LONG;
            case SHORT:
                return TYPE_SHORT;
            case REFERENCE:
                return TYPE_REFERENCE;
            default:
                return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DataType))
            return false;
        DataType t = (DataType) obj;
        return this.type == t.type;
    }
}
