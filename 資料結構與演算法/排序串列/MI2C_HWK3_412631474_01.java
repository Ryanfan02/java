
import java.util.* ;
class SortedList<T extends Integer> extends ArrayList<T> {	
	public SortedList() { super(); } // super() 可省略
	public boolean add(T item) {
            // 使用super.get(i)為item在串列中找到適當的位置
            // 再用super.add(index,item) 將item加入
            // 為使程式精簡，super.可省略
            int index=0;
            for (int i = 0; i < size();i++) {
                if (get(i)>=item) {
                    index=i;
                    break;
                }else{
                    index=i+1;
                }
                
            }
            add(index,item);
            
            return true; // 請勿刪除此行
	}
	public void remove(T item) { super.remove(item); } // super.可省略
	public String toString() { return super.toString(); }
}


public class MI2C_HWK3_412631474_01 {
    public static void main(String[] args) {
        SortedList<Integer> SL1 = new SortedList<>() ;
        SL1.add(18); SL1.add(10); SL1.add(15); SL1.add(4); SL1.add(13); 
        System.out.println("SL1="+SL1); // SL1=[4, 10, 13, 15, 18]
    }
}
