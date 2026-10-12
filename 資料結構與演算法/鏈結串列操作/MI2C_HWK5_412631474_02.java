import java.util.*;

public class MI2C_HWK5_412631474_02 {
    public static void main(String[] args) {
		LinkedList<Integer> list = new LinkedList<Integer>() ;
		for (int i = 0 ; i<10; i++)
			list.add((int)(Math.random()*101)-50);//-50~50
		System.out.println("list="+list) ;

		int sum = sumUpList(list) ;
                System.out.println("sum="+sum);
		LinkedList<Integer> posList = positiveList(list) ;
		System.out.println("posList="+posList) ;
	}	
	public static int sumUpList(LinkedList<Integer> L) {
            int a=0;
            for (int i = 0; i < L.size(); i++) {
                a+=L.get(i);
            }
            return a ;
	}
	public static LinkedList<Integer> positiveList(LinkedList<Integer> L){
            LinkedList<Integer> l1=new LinkedList<Integer>();
            for (int i = 0; i < L.size(); i++) {
                if (L.get(i)>0) {
                    l1.add(L.get(i));
                }
            }
            return l1 ;
	}

}
