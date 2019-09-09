import java.awt.BorderLayout;
 import java.awt.Container;
 import javax.swing.JApplet;
 import javax.swing.JFrame;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.JTextArea;
 import javax.swing.event.TableModelEvent;
 import javax.swing.event.TableModelListener;
 import javax.swing.table.AbstractTableModel;

 public class JTableDemo extends JApplet {
 private JTextArea txt = new JTextArea(4, 20);

 // The TableModel controls all the data:
 class DataModel extends AbstractTableModel {
String one = "<html><body color='#0000FF'>one</body></html>";
 Object[][] data = { { one, "two", "three", "four" },
 { "five", "six", "seven", "eight" },
 { "nine", "ten", "eleven", "twelve" }, };

 public int getColumnCount() {
 return data[0].length;
 }

 public int getRowCount() {
 return data.length;
 }

 public Object getValueAt(int row, int col) {
 return data[row][col];
 }

 public void setValueAt(Object val, int row, int col) {
 data[row][col] = val;
 // Indicate the change has happened:
 fireTableDataChanged();
 }


 }
 public void init() {
 Container cp = getContentPane();
 JTable table = new JTable(new DataModel());
 cp.add(new JScrollPane(table));
 cp.add(BorderLayout.SOUTH, txt);
 }

 public static void main(String[] args) {
 run(new JTableDemo(), 350, 200);
 }

 public static void run(JApplet applet, int width, int height) {
 JFrame frame = new JFrame();
 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 frame.getContentPane().add(applet);
 frame.setSize(width, height);
 applet.init();
 applet.start();
 frame.setVisible(true);
 }
 }
