package userInterface;

import interpreter.ExpressionEvaluator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

import lexicalAnalyzer.Analyzer;

import CommonClasses.Error;
import CommonClasses.Lexem;
import CommonClasses.SintaxElement;
import sintaticalAnalyzer.SintaxAnalyzer;
import symbolTable.Table;

/**
 *
 * @author Eduardo
 */
public class UI extends javax.swing.JFrame {
    public UI() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        output = new javax.swing.JTextArea();
        parse = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        source = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar2 = new javax.swing.JMenuBar();
        menu1 = new javax.swing.JMenu();
        newitem = new javax.swing.JMenuItem();
        open = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exit = new javax.swing.JMenuItem();
        menu2 = new javax.swing.JMenu();
        reset = new javax.swing.JMenuItem();
        show = new javax.swing.JMenuItem();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(531, 400));
        getContentPane().setLayout(null);

        output.setColumns(20);
        output.setRows(5);
        output.setEnabled(true);
        output.setEditable(false);
        jScrollPane1.setViewportView(output);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(10, 190, 500, 130);

        parse.setText("Interpretar");
        parse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parseActionPerformed(evt);
            }
        });
        getContentPane().add(parse);
        parse.setBounds(410, 160, 100, 23);

        source.setColumns(20);
        source.setRows(5);
        jScrollPane2.setViewportView(source);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(10, 20, 500, 130);

        jLabel1.setText("Sa�da:");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(10, 170, 80, 14);

        jLabel2.setText("C�digo-fonte:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(10, 0, 100, 14);

        menu1.setText("Arquivo");

        newitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        newitem.setText("Novo");
        newitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newitemActionPerformed(evt);
            }
        });
        menu1.add(newitem);

        open.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        open.setText("Abrir");
        open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openActionPerformed(evt);
            }
        });
        menu1.add(open);
        menu1.add(jSeparator1);

        exit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exit.setText("Sair");
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        menu1.add(exit);

        jMenuBar2.add(menu1);

        menu2.setText("Tabela de Simbolos");

        reset.setText("Resetar Tabela de Simbolos");
        reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetActionPerformed(evt);
            }
        });
        menu2.add(reset);

        show.setText("Exibir Tabela de Simbolos");
        show.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showActionPerformed(evt);
            }
        });
        menu2.add(show);

        jMenuBar2.add(menu2);

        setJMenuBar(jMenuBar2);

        pack();
    }// </editor-fold>

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void newitemActionPerformed(java.awt.event.ActionEvent evt) {
        this.source.setText("");
    }

    private void openActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void resetActionPerformed(java.awt.event.ActionEvent evt) {
        Analyzer.s = new Table();
    }

    private void update() {
		// TODO Auto-generated method stub
		
	}

	private void showActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void parseActionPerformed(java.awt.event.ActionEvent evt) {
    	FileWriter w;
    	File file = new File("___tempfile");
    	file.delete();
		LinkedList<Lexem> l;
		SintaxElement se;
		ExpressionEvaluator.ui = this;
    	try {
			w = new FileWriter(new File("___tempfile"));
			BufferedWriter b = new BufferedWriter(w);
			b.write(this.source.getText());
			b.close();
			ExpressionEvaluator.ui = this;
    	} catch (Exception e) {
			println("# "+ e.getMessage());
			return;
		}
		String arquivo = "___tempfile";
		
		try {
			if (Analyzer.s==null) Analyzer.s = new Table();
			l = Analyzer.parseFile(arquivo);
			
			if (l==null) return;
			/*for (int a=0; a<l.size(); a++) {
				Lexem x = l.get(a);
				System.out.println(x.getLex() + "\t" + x.getId());
			}*/
			
			se = SintaxAnalyzer.parseLexems(l);
			if (se!=null){
			ExpressionEvaluator.evalue(Analyzer.s,se);
			
			}
			
		} catch (Error e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			println("# " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public void println(String message) {
		this.output.append("\n"+message);
	}

	/**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new UI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify
    private javax.swing.JMenuItem exit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenu menu1;
    private javax.swing.JMenu menu2;
    private javax.swing.JMenuItem newitem;
    private javax.swing.JMenuItem open;
    private javax.swing.JTextArea output;
    private javax.swing.JButton parse;
    private javax.swing.JMenuItem reset;
    private javax.swing.JMenuItem show;
    private javax.swing.JTextArea source;
    // End of variables declaration
}
