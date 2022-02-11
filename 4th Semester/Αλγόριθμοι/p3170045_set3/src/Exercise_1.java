import org.w3c.dom.ls.LSOutput;

public class Exercise_1 {
    private int[] array;

    public Exercise_1(int[] array){
        this.array = array;
    }

    public static int sortAndCount(int[] a, int l, int r)
    {
        int inv = 0;
        if (l < r) {
            int m = (l + r) / 2;
            inv+=sortAndCount(a, l, m);
            inv+=sortAndCount(a, m + 1, r);
            inv+=merge(a, l, m, r);
        }
        return inv;
    }

    private static int merge(int[] array, int left, int mid, int right) {
        int inv = 0;
        int len1 = mid - left + 1;
        int len2 = right - mid;
        int[] leftArr = new int[len1];
        int[] rightArr = new int[len2];
        //fill leftArr
        for (int i = 0; i < len1; i++)
            leftArr[i] = array[left + i];
        //fill rightArr
        for (int j = 0; j < len2; j++)
            rightArr[j] = array[mid + j + 1];
        int k = 0;
        int j = 0;
        int index = left;
        while (j < len2 && k < len1) {
            if (leftArr[k] <= rightArr[j]) {
                array[index] = leftArr[k];
                k++;
            } else {
                inv+= len1-k;
                array[index] = rightArr[j];
                j++;
            }
            index++;
        }
        if (j < len2) {
            while (j < len2) {
                array[index++] = rightArr[j++];
            }
        } else {
            while (k < len1) {
                array[index++] = leftArr[k++];
            }
        }
        return inv;
    }

    public void printSolution(){
        System.out.println(sortAndCount(array, 0, array.length-1));
    }
}
