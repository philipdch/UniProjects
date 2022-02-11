class A { 
  static int rec(int i, int t[]){  
    t[i]=t[i]-1;  
    if (i==0) return 0; 
    i=i-1; 
    return i+2*rec(i,t); } } 
public class Recursion { 
   public static void main(String args[]) {  
     int i; int nums[] = new int[5]; 
     for(i=0; i <= 4; i++) nums[i] = i;    
     System.out.print(A.rec(i-1,nums)+" "); 
     for(i=0; i <= 4; i++) System.out.print(nums[i]+ " "); 
   }
}