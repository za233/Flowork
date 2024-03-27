package io.github.r1mao.ir.code;

import java.util.ArrayList;

public class Temp extends Value {
    private static ArrayList<Temp> cache = new ArrayList<>();

    private Temp() {
        super(Value.TEMP);
    }

    public static Temp getTemp(int index) {

        if (index >= cache.size()) {
            int s = index - cache.size() + 1;
            for (int i = 0; i < s; i++)
                cache.add(new Temp());

        }
        return cache.get(index);
    }

    public int getIndex() {
        return cache.indexOf(this);
    }

    @Override
    public String dump() {
        return "t" + this.getIndex();
    }

    public static ArrayList<Temp> getAllocatedTempValue() {
        return cache;
    }
}
