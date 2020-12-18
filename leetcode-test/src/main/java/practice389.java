public class practice389 {

    public static void main(String[] args) {

    }

    public static char findTheDifference(String s, String t) {
        int[] array = new int[26];
        for (int i = 0; i < s.length(); i++) {
            array[s.charAt(i) - 'a'] ++;
        }
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            array[c - 'a'] --;
            if (array[c - 'a'] < 0) {
                return c;
            }
        }
        return ' ';
    }

}
