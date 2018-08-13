package ch.gaps.slasher.corrector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used to find the substrings with omitted spaces in the string
 * For example, it would find a substring "deardrugiz" in the string "hey hey hey my dear drugiz"
 */
public class SubstringFinder {
    List<Integer[]> index;
    List<Integer> spacesIndex;
    final static String SPACES = "\u000B\t\r\n ";
    String text;
    String textWithoutSpaces;

    public SubstringFinder(String text) {
        this.text = text;
        textWithoutSpaces = text.replaceAll("[ \\u000B\\t\\r\\n]", "");
        index = new ArrayList<>();
        spacesIndex = new LinkedList<>();
        findSpaces();
    }

    private void findSpaces() {
        for (int i = 0; i < text.length(); i++) {
            Character c = new Character(text.charAt(i));
            if (SPACES.indexOf(c.charValue()) != -1) {
                spacesIndex.add(i);
            }
        }
    }

    List<Integer[]> find(String substring) {
        int pos;
        int fromIndex = 0;
        while ((pos = textWithoutSpaces.indexOf(substring, fromIndex)) != -1) {
            Integer[] range = new Integer[2];
            range[0] = pos;
            range[1] = pos + substring.length()-1;
            index.add(range);
            fromIndex = pos + substring.length();
        }

        for (Integer spaceIndex : spacesIndex) {
            int cnt = 0;
            for (Integer[] i : index) {
                if (spaceIndex <= i[1]) {
                    if (spaceIndex <= i[0]) {
                        i[0]++;
                    }
                    i[1]++;
                    increaseIndexesFrom(cnt+1);
                    break;
                }
                cnt++;
            }
        }
        return index;
    }

    private void increaseIndexesFrom(int pos) {
        for (int i = pos; i < index.size(); i++) {
            index.get(i)[0]++;
            index.get(i)[1]++;
        }
    }

}
