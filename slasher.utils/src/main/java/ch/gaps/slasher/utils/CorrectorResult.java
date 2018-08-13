package ch.gaps.slasher.utils;

import com.sun.tools.javac.util.Pair;

import java.util.Map;
import java.util.TreeMap;

public class CorrectorResult {
    private boolean ok;
    private Map<String, Pair<Integer, Integer>> errors;

    public CorrectorResult() {
        ok = true;
        errors = new TreeMap<>();
    }

    public void setNotOk() {
        ok = false;
    }

    public void addError(String msg, int firstIndex, int lastIndex) {
        Pair<Integer, Integer> index = new Pair<>(firstIndex, lastIndex);
        errors.put(msg, index);
        ok = false;
    }

    Map<String, Pair<Integer, Integer>> getErrors() {
        return errors;
    }

    public boolean isOk() {
        return ok;
    }
}
