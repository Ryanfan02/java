
import java.util.*;

public class MI3C_HWK5_412631474_01 {
    public static void main(String[] args) throws Exception{
        int []a=new int[100],b=new int[100];
        int []c=new int[100],d=new int[100];
        Random r=new Random(111);
        for (int i = 0; i < a.length; i++) {
            a[i]=50+r.nextInt(200-50+1);
            b[i]=50+r.nextInt(200-50+1);
        }
        
        Thread t1=new Thread(
                () -> {
                    for (int i = 0; i < a.length; i++) {
                        c[i]=a[i]+b[i];
                    }
                    
                }
        );
        
        Thread t2=new Thread(
                ()->{
                    for (int i = 0; i < a.length; i++) {
                        d[i]=a[i]-b[i];
                    }
                }
        );
        
        t1.start();t2.start();
        t1.join();t2.join();
        System.out.println("C[]="+Arrays.toString(c));
        System.out.println("d[]="+Arrays.toString(d));
    }
}
