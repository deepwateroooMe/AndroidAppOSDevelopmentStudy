public class binarySearch {
    public static class Solution {
        public int binarySearchRecusive(int [] arr, int bgn, int end, int target) {
            if (arr.length == 0 || end < bgn) return -1;
            else if (arr.length == 1) {
                if (arr[bgn] == target) return bgn;
                else return -1;
            }
            int n = arr.length;
            int mid = arr[(end - bgn) / 2];
            if (arr[mid] == target) return mid;
            else if (arr[mid] < target) { 
                return binarySearchRecusive(arr, bgn, mid, target);
            } else {
                return binarySearchRecusive(arr, mid + 1, end, target);
            }
        }
        
        public int binarySearch(int [] arr, int target) {
            return binarySearchRecusive(nums, 0, nums.length, target);
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        //          0  1  2  3  4  5  6  7  8  9  10 11
        int [] A = {1, 2, 2, 3, 4, 4, 4, 4, 5, 6, 7, 8};
        //                         5
        int res = solution.binarySearch(A, 4);
        System.out.println("result: " + res);
    }
}
