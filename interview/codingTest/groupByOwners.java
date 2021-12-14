import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
//import java.lang.StringBuilder;

public class groupByOwners {
    public static class Solution {
    public Map<String, List<String>> group_by_owners (Map<String, String> map) {
        Map<String, List<String>> res = new HashMap<String, List<String>>();
        List<String> names;
        String owner;
        for (String key : map.keySet()) {
            owner = map.get(key);
            if (!res.containsKey(owner)) 
                names = new ArrayList<String>();
            else 
                names = res.get(owner);
            names.add(key);
            res.put(owner, names);

        }
        return res;
    }
    }

    public static void main(String [] args) {
        Solution result = new Solution();
        Map<String, String> map = new HashMap<String, String >();
        
        map.put("Input.txt", "Randy");
        map.put("Code.py", "Stan");
        map.put("Output.txt", "Randy");
        Map<String, List<String>> res = result.group_by_owners(map);
        for (String key : map.keySet()) {
            System.out.println(key + ": " + map.get(key));
        }
        
        System.out.println(res);
    }
}
