import java.util.Arrays;

public class UniqueSubstringFinder
{
    public static String longestUniqueSubstring(String s)
    {
        int[] lastSeenIndex = new int[26];
        Arrays.fill(lastSeenIndex, -1); 
        int left = 0;
        int len = 0;
        int maxLeft=0;

        char[] str =s.toCharArray();

        for (int i =0; i<str.length; i++)
        {
            int index = lastSeenIndex[str[i] - 'a']; // posledniq index
            if ( index != -1 && index >= left) 
                left = index +1;
            if(i - left + 1 > len)
            {
                len = i - left +1;
                maxLeft = left;
            }
            lastSeenIndex[str[i] - 'a'] = i; //yuk sme go vidqli za posledno    
        }

        return s.substring(maxLeft, maxLeft+len);
    }    

    public static void main(String[] args)
    {
        System.out.println(longestUniqueSubstring(""));
    }
}

