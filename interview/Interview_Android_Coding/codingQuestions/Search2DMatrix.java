import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Search2DMatrix {
    public static class Solution {
        public boolean searchMatrix(int [][] matrix, int target) {
            int m = matrix.length;
            int n = matrix[0].length;
            int bgn = 0;
            int end = m * n - 1;
            while (bgn <= end) {
                int mid = bgn + (end - bgn) / 2;
                int val = matrix[mid / n][mid % n];
                if (val == target) return true;
                if (val > target)
                    end = mid - 1;
                else bgn = mid + 1;
            }
            return false;
        }
    }

    public static void main (String [] args){
        Solution result = new Solution ();
        int [][] A = {{0, 1, 2, 4},
                      {1, 2, 6, 9},
                      {3, 5, 7, 10},
                      {7, 8, 9, 11}};
        boolean res = result.searchMatrix(A, 8);
        System.out.println (res);
    }
}