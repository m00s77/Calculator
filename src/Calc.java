import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;

public class Calc extends JFrame implements KeyListener{

    private static final float BTN_FONT_SIZE = 20f;
    private static final String[][] BTN_LABELS = {
            {"Off", "C", "%", "/"},
            {"7", "8", "9", "x"},
            {"4", "5", "6", "-"},
            {"1", "2", "3", "+"},
            {"+/-", "0", ".", "="}
    };
    private static final int GAP = 4;
    private final JTextField display = new JTextField();
    private final JPanel mainPanel = new JPanel(new BorderLayout(GAP, GAP));
    private static JPanel buttonPanel;
    private static double op1Calc;
    private static double op2Calc;
    private static double accumulator;
    private String calcOP = "";
    private static final ArrayList<String> history= new ArrayList<>();
    private static boolean historyMode = false;
    private static int historyIndex = 0;


    public Calc() {

        int rows = BTN_LABELS.length;
        int cols = BTN_LABELS[0].length;
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(rows, cols, GAP, GAP));
        for (String[] btnLabelRow : BTN_LABELS) {
            for (String btnLabel : btnLabelRow) {
                if (btnLabel.trim().isEmpty()) {
                    buttonPanel.add(new JLabel());
                } else {
                    JButton btn = createButton(btnLabel);
                    if(btnLabel.equals("+") || btnLabel.equals("-") ||
                            btnLabel.equals("x") || btnLabel.equals("/") ||
                            btnLabel.equals("%") || btnLabel.equals("C") || btnLabel.equals("=")){
                        btn.setBackground(new Color(0xD395660F, true));
                    } else if(btnLabel.equals("Off")){
                        btn.setBackground(new Color(0x97E50F0F, true));
                    }
                    btn.setFocusable(false);
                    btn.setOpaque(true);
                    buttonPanel.add(btn);
                }
            }
        }
        setUndecorated(true);
        display.setFont(display.getFont().deriveFont(BTN_FONT_SIZE));
        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFocusable(false);
        display.setBackground(Color.BLACK);
        display.setForeground(Color.GREEN);
        display.setText("0");

        mainPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(display, BorderLayout.PAGE_START);
        mainPanel.addKeyListener(this);
        mainPanel.setFocusable(true);
        op2Calc = 0d;

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });
    }

    public JComponent getCalcComponent() {
        return mainPanel;
    }

    private static void createAndShowGui(){
        Calc mainPanel = new Calc();
        mainPanel.setTitle("Calculator");
        mainPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.setLocationRelativeTo(null);
        mainPanel.getContentPane().add(mainPanel.getCalcComponent());
        mainPanel.pack();
        mainPanel.setVisible(true);
        mainPanel.setMenuBar();
    }

    private void storeHistory(Double op1, String oper , Double op2, Double res) {

        DecimalFormat format = new DecimalFormat();
        format.setGroupingUsed(false);

        String str;

        if(oper.isEmpty()){
            str = format.format(op1);
        } else if(oper.equals("%") && op2 == 0d){
            str = format.format(op1) + " " + oper + " = " + format.format(res);
        } else {
            str = format.format(op1) + " " +
                    oper + " " +
                    format.format(op2) + " = " +
                    format.format(res);
        }
        history.add(str);

    }

    private void showHistory(int index) {
            display.setText(history.get(index));
    }

    private void selectHistory(int index) {
        if (index >= 0){
            String[] str = history.get(index).split(" ");
            display.setText(str[str.length - 1]);
        }
        historyMode = false;
        setHistoryNav(false);
    }

    private JButton createButton(String btnLabel) {
        JButton button = new JButton(btnLabel);
        button.setFont(button.getFont().deriveFont(BTN_FONT_SIZE));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readButton( (JButton)e.getSource() );
            }
        });
        return button;
    }

    private void setMenuBar(){

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menu.setFocusPainted(true);
        JMenuItem historyMenu = new JMenuItem("History");
        historyMenu.setFocusPainted(true);

        historyMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(history.size() != 0){
                    setHistoryNav(true);

                    if(!historyMode){
                        showHistory(0);
                        historyMode = true;
                    } else {
                        display.setText("0");
                        historyMode = false;
                        setHistoryNav(false);
                    }
                }
            }
        });

        JMenuItem helpMenu = new JMenuItem("Help");
        helpMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });

        menu.add(historyMenu);
        menu.add(helpMenu);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(mainPanel, """
                Simple calculator

                History  - select a line or ESCAPE
                '+/-'    - use SPACE key
                'Off'    - use ESCAPE key\s
                """);
    }

    private void setHistoryNav(boolean state) {

        Component[] components = buttonPanel.getComponents();

        if(state) {
            for (Component c : components) {
                c.setBackground(null);
                switch (((JButton) c).getText()) {
                    case "/":
                        ((JButton) c).setText("^");
                        break;
                    case "x":
                        ((JButton) c).setText("" + 'v');
                        break;
                    case "=":
                        break;
                    default:
                        ((JButton) c).setText("");
                        c.setEnabled(false);
                        break;
                }
            }
        } else{
            for (int i = 0; i < BTN_LABELS.length; i++) {
                for (int j = 0; j < BTN_LABELS[0].length; j++ ) {
                    ((JButton) components[(i * BTN_LABELS[0].length) + j]).setText(BTN_LABELS[i][j]);
                    components[(i * BTN_LABELS[0].length) + j].setEnabled(true);

                    if( ((JButton) components[(i * BTN_LABELS[0].length) + j]).getText().equals("+") ||
                            ((JButton) components[(i * BTN_LABELS[0].length) + j]).getText().equals("-") ||
                            ((JButton) components[(i * BTN_LABELS[0].length) + j]).getText().equals("x") ||
                            ((JButton) components[(i * BTN_LABELS[0].length) + j]).getText().equals("/") ||
                            ((JButton) components[(i * BTN_LABELS[0].length) + j]).getText().equals("%") ||
                            ((JButton) components[(i * BTN_LABELS[0].length) + j]).getText().equals("C") ||
                            ((JButton) components[(i * BTN_LABELS[0].length) + j]).getText().equals("=")){
                        components[(i * BTN_LABELS[0].length) + j].setBackground(new Color(0xD3956107, true));
                    } else if(((JButton) components[(i * BTN_LABELS[0].length) + j]).getText().equals("Off")){
                        components[(i * BTN_LABELS[0].length) + j].setBackground(new Color(0x97E50F0F, true));
                    }
                }
            }
        }
    }

    private void readButton(JButton btn) {

        DecimalFormat format = new DecimalFormat();
        format.setGroupingUsed(false);

        switch (btn.getText()) {
            case "Off":
                String userName = new com.sun.security.auth.module.NTSystem().getName();
                userName = Character.toUpperCase(userName.charAt(0)) + userName.substring(1).toLowerCase();
                display.setText("Good bye " + userName);

                // delay & exit on other thread
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                        }
                        System.exit(0);
                    }
                });
                break;
            case "C":
                display.setText("0");
                break;
            case "+":
                op1Calc = Double.parseDouble(display.getText());
                calcOP = "+";
                display.setText("");
                break;
            case "-":
                op1Calc = Double.parseDouble(display.getText());
                calcOP = "-";
                display.setText("");
                break;
            case "/":
                op1Calc = Double.parseDouble(display.getText());
                calcOP = "/";
                display.setText("");
                break;
            case "x":
                op1Calc = Double.parseDouble(display.getText());
                calcOP = "x";
                display.setText("");
                break;
            case "%":
                if (!display.getText().isBlank()){
                    op1Calc = Double.parseDouble(display.getText());
                    calcOP = "%";
                    display.setText("");
                }
                break;
            case ".":
                if (!display.getText().contains(".")) {
                    if (display.getText().equals("0") || display.getText().equals("")) {
                        display.setText("0.");
                    } else {
                        display.setText(display.getText() + ".");
                    }
                }
                break;
            case "+/-":
                op1Calc = Double.parseDouble(display.getText());
                display.setText(format.format(op1Calc * -1));
                break;
            case "=":
                if (historyMode){
                    selectHistory(historyIndex);
                } else {
                    executeOP(format);
                    calcOP = "";
                }
                break;
            case "^":
                if(historyMode && historyIndex > 0){
                    showHistory(--historyIndex);
                }
                break;
            case "v":
                if(historyMode && (historyIndex < (history.size() - 1))){
                    showHistory(++historyIndex);
                }
                break;

            default:
                if(display.getText().equals("0")){
                    display.setText(btn.getText());
                } else {
                    if ((display.getText() + btn.getText()).length() >= 10) {
                        break;
                    } else {
                        display.setText(display.getText() + btn.getText());
                        }
                    }
                }


    }

    private void readKey(KeyEvent evt) {

        DecimalFormat format = new DecimalFormat();
        format.setGroupingUsed(false);

        switch (evt.getKeyChar()) {
            case KeyEvent.VK_ESCAPE:
                String userName = new com.sun.security.auth.module.NTSystem().getName();
                userName = Character.toUpperCase(userName.charAt(0)) + userName.substring(1).toLowerCase();
                display.setText("Good bye " + userName);

                // delay & exit on other thread
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {

                        }
                        System.exit(0);
                    }
                });
                break;
            case KeyEvent.VK_DELETE:
                display.setText("0");
                break;
            case '+':
                op1Calc = Double.parseDouble(display.getText());
                calcOP = "+";
                display.setText("");
                break;
            case '*':
                op1Calc = Double.parseDouble(display.getText());
                calcOP = "x";
                display.setText("");
                break;
            case '-':
                op1Calc = Double.parseDouble(display.getText());
                calcOP = "-";
                display.setText("");
                break;
            case '/':
                op1Calc = Double.parseDouble(display.getText());
                calcOP = "/";
                display.setText("");
                break;
            case '%':
                op1Calc = Double.parseDouble(display.getText());
                calcOP = "%";
                display.setText("");
                break;
            case '.':
                if (!display.getText().contains(".")) {
                    if (display.getText().equals("0") || display.getText().equals("")) {
                        display.setText("0.");
                    } else {
                        display.setText(display.getText() + ".");
                    }
                }
                break;
            case KeyEvent.VK_SPACE:
                op1Calc = Double.parseDouble(display.getText());
                display.setText(format.format(op1Calc * -1));
                break;
            case KeyEvent.VK_ENTER:
                executeOP(format);
                calcOP = "";
                break;
            default:
                if (evt.getKeyChar() >= '0' && evt.getKeyChar() <= '9') {
                    if (display.getText().equals("0")) {
                        display.setText("" + evt.getKeyChar());
                    } else {
                        if ((display.getText() + evt.getKeyChar()).length() >= 10) {
                            break;
                        } else {
                            display.setText(display.getText() + evt.getKeyChar());
                        }
                    }
                }
        }
}

    private void executeOP(DecimalFormat format) {

        switch (calcOP) {
            case "+":
                op2Calc = Double.parseDouble(display.getText());
                display.setText(format.format(accumulator = op1Calc + op2Calc));
                break;
            case "-":
                op2Calc = Double.parseDouble(display.getText());
                display.setText(format.format(accumulator = op1Calc - op2Calc));
                break;
            case "x":
                op2Calc = Double.parseDouble(display.getText());
                display.setText(format.format(accumulator = op1Calc * op2Calc));
                break;
            case "/":
                op2Calc = Double.parseDouble(display.getText());
                display.setText(format.format(accumulator = op1Calc / op2Calc));
                break;
            case "%":
                if(display.getText().isEmpty()){
                    op2Calc = 0;
                    display.setText(format.format(accumulator = op1Calc / 100));
                } else{
                    op2Calc = Double.parseDouble(display.getText());
                    accumulator = (op1Calc / 100) * op2Calc;
                    display.setText(format.format(accumulator));
                }
                break;
            case "":
                op1Calc = Double.parseDouble(display.getText());
                break;
        }

        storeHistory(op1Calc, calcOP, op2Calc, accumulator);

        op1Calc = accumulator;
        op2Calc = 0;
        calcOP = "";
    }

    @Override
    public void keyTyped(KeyEvent e) {
        readKey(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(historyMode){
            switch(e.getKeyCode()){
                case KeyEvent.VK_DOWN:
                    if(historyIndex < (history.size() - 1)){
                        showHistory(++historyIndex);
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(historyIndex > 0){
                        showHistory(--historyIndex);
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    selectHistory(historyIndex);
                    break;
                case KeyEvent.VK_ESCAPE:
                    selectHistory(-1);
                    break;

            }


        }
    }


}