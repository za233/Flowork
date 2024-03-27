package io.github.r1mao.algorithm;

import io.github.r1mao.DataType;

import java.util.ArrayList;

public class DescriptorParser {
    public String desc, returnDesc;
    public boolean isMethodDesc;

    public DescriptorParser(String desc, boolean isMethodDesc) {
        this.isMethodDesc = isMethodDesc;
        if (isMethodDesc) {
            assert desc.startsWith("(");
            int index = desc.lastIndexOf(")");
            this.desc = desc.substring(1, index);
            this.returnDesc = desc.substring(index + 1);

            assert returnDesc.length() != 0;
        } else {
            this.desc = desc;
        }

    }

    public int getArgumentNum() {
        return this.getTypes().size();
    }

    public int getArgumentSizeInSlot() {

        int size = 0;
        for (DataType type : this.getTypes())
            size += type.getSizeInSlot();
        return size;
    }

    private ArrayList<DataType> getTypesByString(String str) throws Exception {
        int ptr = 0;
        ArrayList<DataType> result = new ArrayList<>();
        while (ptr != str.length()) {
            ArrayList<String> bucket = new ArrayList<>();
            ptr += nextType(str.substring(ptr), bucket);
            result.add(getTypeByChar(bucket.get(0).charAt(0)));
        }
        return result;
    }

    private DataType getTypeByChar(char firstChar) throws Exception {
        switch (firstChar) {
            case 'B':
                return DataType.TYPE_BYTE;
            case 'C':
                return DataType.TYPE_CHAR;
            case 'D':
                return DataType.TYPE_DOUBLE;
            case 'F':
                return DataType.TYPE_FLOAT;
            case 'I':
                return DataType.TYPE_INTEGER;
            case 'J':
                return DataType.TYPE_LONG;
            case 'S':
                return DataType.TYPE_SHORT;
            case 'Z':
                return DataType.TYPE_BOOLEAN;
            case 'V':
                return DataType.TYPE_VOID;
            case '[':
            case 'L':
                return DataType.TYPE_REFERENCE;
            default:
                throw new Exception("Invalid Descriptor Character!");
        }
    }

    public ArrayList<DataType> getTypes() {
        try {
            return this.getTypesByString(this.desc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public DataType getType() {
        return this.getTypes().get(0);
    }

    public DataType getReturnType() {
        assert this.isMethodDesc == true;
        try {
            return this.getTypesByString(this.returnDesc).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int nextType(String input, ArrayList<String> bucket) throws Exception {
        if (input.length() == 0)
            return 0;
        char c = input.charAt(0);
        int step = 0;
        switch (c) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'Z':
            case 'V':
                step = 1;
                bucket.add(input.substring(0, 1));
                break;
            case '[':
                bucket.add("[");
                step = nextType(input.substring(1), bucket) + 1;
                break;
            case 'L':
                while (input.charAt(step) != ';')
                    step++;
                step++;
                bucket.add(input.substring(0, step));
                break;
            default:
                throw new Exception("Invalid Descriptor Character!");

        }
        return step;
    }
}
