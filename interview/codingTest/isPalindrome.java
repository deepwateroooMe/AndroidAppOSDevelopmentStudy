import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class isPalindrome {
    public static class Solution {
        // valid: 0-9 A-Z a-z
        public boolean isPalindrome(String s) {
            int i = 0, j = s.length()-1;
            while (i < j) {
                /*
                // for checking if a String eg "A man, a plan, a canal: Panama" contain space, marks etc
                // but since we limited palindrome for a WORD, so skip these checks
                while (i < j && !Character.isLetterOrDigit(s.charAt(i))) i++;
                while (i < j && !Character.isLetterOrDigit(s.charAt(j))) j--;
                */
                if (Character.toLowerCase(s.charAt(i)) != Character.toLowerCase(s.charAt(j)))
                    return false;
                i++;
                j--;
            }
            return true;
        }
        /*        
        public boolean isPalindrome(String s) {
            if (s == null || s.length() == 0) return true;
            int i = 0, j = s.length()-1;
            char one, two;
            while (i < j) {
                one = s.charAt(i);
                while (i < s.length()-1 && i < j &&
                       (one < '0' || one > 'z'||
                        (one > '9' && one < 'A') ||
                        (one > 'Z' && one < 'a'))) {
                    i++;
                    one = s.charAt(i);
                }
                if (i == j) return true;
                
                two = s.charAt(j);
                while (j >= 1 &&
                       (two < '0' || two > 'z'||
                        (two > '9' && two < 'A') ||
                        (two > 'Z' && two < 'a'))) {
                    j--;
                    two = s.charAt(j);
                }
                if (one != two && Math.abs(one - two) != 32)                
                    return false;
                else {
                    i++;
                    j--;
                }
            }
            if (i >= j) return true;
            return (s.charAt(i) == s.charAt(j) || Math.abs(s.charAt(i) - s.charAt(j)) == 32);
        }
        */
    }

    public static void main(String[] args){
        Solution result = new Solution();
        String s = "Deleveled";
        boolean res = result.isPalindrome(s);
        
        System.out.println(res);
    }
}
