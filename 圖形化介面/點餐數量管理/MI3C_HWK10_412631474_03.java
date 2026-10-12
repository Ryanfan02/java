import java.util.*;
import javax.swing.*;
public class MI3C_HWK10_412631474_03 extends javax.swing.JFrame {
    String []menu={"¥i¼Ö","Á¦±ø","¸t¥N","¬õ¯ù","ã¯ãÀ¥¤¯ù"};
    DefaultComboBoxModel dcnm=new DefaultComboBoxModel(menu); 
    DefaultListModel dlm=new DefaultListModel();
    public MI3C_HWK10_412631474_03() {
        initComponents();
        this.jComboBox1.setModel(dcnm);
        this.jList1.setModel(dlm);
    }

 
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        getContentPane().add(jComboBox1, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setViewportView(jList1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout());

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        jButton2.setText("Del");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String item=this.jComboBox1.getSelectedItem().toString();
        boolean found=false;
        
        for (int i = 0; i < dlm.size(); i++) {
            String oldText=dlm.elementAt(i).toString();
            
            String oldItem=oldText;
            int count=1;
            
            int startIndex=oldText.indexOf("*");
            if (startIndex!=-1) {
                oldItem=oldText.substring(0, startIndex);
                
                String countText=oldText.substring(startIndex+1);
                count=Integer.parseInt(countText);
            }
            if (oldItem.equals(item)) {
                count+=1;
                dlm.setElementAt(oldItem+"*"+count, i);
                found=true;
                break;
            }
            
        }
        
        if (found==false) {
                dlm.addElement(item);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
         int select = jList1.getSelectedIndex();

    if (select != -1) {
        String oldText = dlm.getElementAt(select).toString();

        String item = oldText;
        int count = 1;

        int starIndex = oldText.indexOf("*");

        if (starIndex != -1) {
            item = oldText.substring(0, starIndex);

            String countText = oldText.substring(starIndex + 1);
            count = Integer.parseInt(countText);
        }

        count = count - 1;

        if (count == 0) {
            dlm.remove(select);
        } else {
            if (count == 1) {
                dlm.setElementAt(item, select);
            } else {
                dlm.setElementAt(item + "*" + count, select);
            }
        }
    }
    }//GEN-LAST:event_jButton2ActionPerformed

   
    public static void main(String args[]) {
       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MI3C_HWK10_412631474_03().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
