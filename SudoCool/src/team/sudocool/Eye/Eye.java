package team.sudocool.Eye;

import org.opencv.core.Mat;
import org.opencv.highgui.*;
import team.sudocool.ImgWorks.Recognizer;
import team.sudocool.ImgWorks.nImgProc.Utils;
import team.sudocool.Main;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * create all the frame
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/28
 */
public class Eye {
    private static final int PRINTING = 0;
    private static final int HANDWRITING = 1;

    private EyeFrame E;
    private EyeTransformed T;
    private SudoFrame S;
    private VideoCap V;
    private Recognizer R;

    private EyeThread eyethread;
    private int mode;

    public Eye() {
        try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

            UIManager.put("nimbusBase", Color.PINK);
//            UIManager.put("nimbusBlueGrey", Color.PINK);
//            UIManager.put("control", Color.PINK);

        } catch (Exception e) {
            e.printStackTrace();
        }

        T = new EyeTransformed();
        E = new EyeFrame();
        S = new SudoFrame();
        V = new VideoCap(0);
        R = new Recognizer();

        eyethread = new EyeThread();
        mode = PRINTING;

        eyethread.start();
    }

    /**
     * continuous image showed
     */
    private class EyeThread extends Thread{
        @Override
        public void run() {
            for (;;){
                Mat image = V.getMat();
                Mat result = mode == HANDWRITING
                        ? R.RecognizeOnly(image) : R.RecognizeAndSolve(image);
                S.updateSudo(R.GetCurrentSudoku());
                E.repaint(result);
                T.repaint(R.GetTransformedImage());

                try { Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * the sudoku table frame
     */
    public class SudoFrame extends JFrame {
        private JButton pauseButton, shiftButton, solveButton,
                resetButton, openButton, exportButton;
        private JComboBox suduTypeComboBox;
        private JPanel bottomPanel;
        private JTabbedPane topPanel;
        private JSpinner[][] sudoSpinner;


        /**
         * creat all the frame
         */
        public SudoFrame() {
            topPanel = new JTabbedPane();
            bottomPanel = new JPanel();

            paintButton();
            paintSudo();

            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("Soduku");

            JPanel contentPaneBoss = new JPanel();
            this.setContentPane(contentPaneBoss);

            contentPaneBoss.setLayout(new BoxLayout(contentPaneBoss, BoxLayout.Y_AXIS));
            contentPaneBoss.add(Box.createVerticalStrut(10));
            contentPaneBoss.add(topPanel);
            contentPaneBoss.add(Box.createVerticalStrut(7));
            contentPaneBoss.add(bottomPanel);
            contentPaneBoss.add(Box.createVerticalStrut(7));

            this.setBounds(900, 100, 450, 450);
    //        this.pack();
//            this.setResizable(false);
            this.setVisible(true);
        }

        /**
         * paint the sudo table
         */
        private void paintSudo() {
            SudoTablePanle sudoPanel = new SudoTablePanle();
            topPanel.addTab("Sudoku Table", sudoPanel);
            topPanel.addTab("Transformed", T);
            topPanel.setSelectedIndex(0);
        }

        /**
         * paint the button on bottom
         */
        private void paintButton() {
            pauseButton = new JButton("PAUSE");
            shiftButton = new JButton("PRINTING");
            solveButton = new JButton("SOLVE");
            resetButton = new JButton("RESET");
            openButton = new JButton("OPEN");
            exportButton = new JButton("EXPORT");

            String sudoTypes[] = {"4*4", "5*5", "6*6", "7*7", "8*8", "9*9"};
            suduTypeComboBox = new JComboBox(sudoTypes);
            suduTypeComboBox.setSelectedIndex(5);

            if(mode == PRINTING)
                solveButton.setEnabled(false);
            openButton.setEnabled(false);
            solveButton.setEnabled(true);

            pauseButton.addActionListener(new pauseEventListener());
            shiftButton.addActionListener(new switchEventListener());
            solveButton.addActionListener(new solveEventListener());
            resetButton.addActionListener(new resetEventListener());
            openButton.addActionListener(new openEventListener());
            exportButton.addActionListener(new exportEventListener());
            suduTypeComboBox.addActionListener(new sudoTypeEventListener());

            bottomPanel.setLayout(new FlowLayout());
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(pauseButton);
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(openButton);
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(exportButton);
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(solveButton);
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(resetButton);
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(shiftButton);
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(suduTypeComboBox);
            bottomPanel.add(Box.createHorizontalGlue());
        }

        private void setEditSudo(boolean edit) {
            for(int i = 0; i < sudoSpinner.length; i++)
                for(int j = 0; j < sudoSpinner[i].length; j++)
                {
                    sudoSpinner[i][j].setEnabled(edit);
                }
        }

        /**
         * when sudo changed, update the table
         * @param sudoData current sudoku
         */
        private void updateSudo(int[][] sudoData) {
            if(sudoData.length != sudoSpinner.length)
                return;

            for(int i = 0; i < sudoSpinner.length; i++)
            {
                if(sudoSpinner[i].length != sudoData[i].length)
                    return;

                for(int j = 0; j < sudoSpinner.length; j++)
                    sudoSpinner[i][j].setValue(sudoData[i][j] == -1 ? 0 : sudoData[i][j]);
            }
        }

        /**
         * Sudoku Table Panel with different size
         */
        private class SudoTablePanle extends JPanel {
            private JPanel sudoTablePanel;

            SudoTablePanle() {
                setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                add(Box.createHorizontalStrut(10));

                sudoTablePanel = new sudoTabel();
                add(sudoTablePanel);
                add(Box.createHorizontalStrut(10));

                int sudoSize = suduTypeComboBox.getSelectedIndex() + 4;
                initSudoSpinner(sudoSize);

                switch (sudoSize) {
                    case 4:
                        paintSudoTabel_Regular(2, 2, 4);
                        break;
                    case 5:
                        paintSudoTable_Irregular(5);
                        break;
                    case 6:
                        paintSudoTabel_Regular(3, 2, 6);
                        break;
                    case 7:
                        paintSudoTable_Irregular(7);
                        break;
                    case 8:
                        paintSudoTabel_Regular(4, 2, 8);
                        break;
                    case 9:
                        paintSudoTabel_Regular(3, 3, 9);
                        break;

                    default:
                        break;
                }
            }

            private void paintSudoTabel_Regular(int n, int m, int sudoSize) {
                sudoTablePanel.setLayout(new GridLayout(n, m));

                for(int w = 0; w < n; w++)
                {
                    for(int q = 0; q < m; q++)
                    {
                        int a = sudoSize/n;
                        int b = sudoSize/m;
                        JPanel boxPanel = new JPanel(new GridLayout(a, b));
                        boxPanel.setBorder(BorderFactory.createLineBorder(Color.PINK));
                        boxPanel.setOpaque(false);
                        sudoTablePanel.add(boxPanel);

                        for (int i = 0; i < a; i++) {
                            for (int j = 0; j < b; j++) {
                                JSpinner num = sudoSpinner[w*a+i][q*b+j];
                                SpinnerModel model = new SpinnerNumberModel(0, 0, sudoSize, 1);
                                num.setModel(model);

                                num.setOpaque(false);
                                num.getEditor().setOpaque(false);
                                ((JSpinner.DefaultEditor)num.getEditor()).getTextField().setOpaque(false);

                                boxPanel.add(num);
                            }
                        }
                    }
                }
            }

            private void paintSudoTable_Irregular(int sudoSize) {
                sudoTablePanel.setLayout(new GridLayout(sudoSize, sudoSize));
                sudoTablePanel.setBorder(BorderFactory.createLineBorder(Color.PINK));

                for(int w = 0; w < sudoSize; w++)
                {
                    for(int q = 0; q < sudoSize; q++)
                    {
                        JSpinner num = sudoSpinner[w][q];
                        SpinnerModel model = new SpinnerNumberModel(0, 0, sudoSize, 1);
                        num.setModel(model);

                        num.setOpaque(false);
                        num.getEditor().setOpaque(false);
                        ((JSpinner.DefaultEditor)num.getEditor()).getTextField().setOpaque(false);

                        sudoTablePanel.add(num);
                    }
                }
            }

            private void initSudoSpinner(int sudoSize) {
                sudoSpinner = new JSpinner[sudoSize][sudoSize];
                for(int i = 0; i < sudoSize; i++) {
                    for(int j = 0; j < sudoSize; j++) {
                        sudoSpinner[i][j] = new JSpinner();
                    }
                }
                setEditSudo(false);
            }

            private class sudoTabel extends JPanel {
                @Override
                public void paintComponent(Graphics g)
                {
                    super.paintComponent(g);
                    ImageIcon image = new ImageIcon("./resources/sudokuBackground.jpg");
                    g.drawImage(image.getImage(),0,0,this);
                }
            }
        }

        /**
         * pauseButton event handler
         */
        private class pauseEventListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pauseButton.getText().equals("PAUSE"))
                {
                    eyethread.suspend();
                    pauseButton.setText("CONTINUE");
                    setEditSudo(true);
                    solveButton.setEnabled(true);
                    openButton.setEnabled(true);
                }
                else if(pauseButton.getText().equals("CONTINUE"))
                {
                    eyethread.resume();
                    pauseButton.setText("PAUSE");
                    setEditSudo(false);
                    solveButton.setEnabled(false);
                    openButton.setEnabled(false);
                }
            }
        }

        /**
         * switch event handler
         * switch in two modes: handwriting and print
         */
        private class switchEventListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(shiftButton.getText().equals("HANDWRITING"))
                {
                    shiftButton.setText("PRINTING");
                    mode = PRINTING;
                }
                else if(shiftButton.getText().equals("PRINTING")){
                    shiftButton.setText("HANDWRITING");
                    mode = HANDWRITING;
                }

                R.Reset();
            }
        }

        /**
         * solve button listener
         */
        private class solveEventListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                int size = sudoSpinner.length;

                int[][] sudoData = new int[size][size];
                for(int i = 0; i < size; i++)
                {
                    for(int j = 0; j < size; j++)
                    {
                        sudoData[i][j] = (Integer) sudoSpinner[i][j].getValue();
                        if(sudoData[i][j] == 0)
                            sudoData[i][j] = -1;
                    }
                }


                if(!R.Solve(sudoData))
                    JOptionPane.showMessageDialog(null,
                            "No answer!",
                            "Alert",
                            JOptionPane.ERROR_MESSAGE);
                else
                {
                    updateSudo(R.GetCurrentSudoku());
                    JOptionPane.showMessageDialog(null,
                            "Let`s see it!",
                            "Congratulation",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }

        /**
         * reset button listener
         */
        private class resetEventListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                R.Reset();

                if(pauseButton.getText().equals("CONTINUE")) {
                    updateSudo(R.GetCurrentSudoku());
                }
            }
        }

        /**
         * open file button listener
         */
        private class openEventListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent eve) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                String extj[] = {"jpeg", "jpg", "png", "bmp"};
                FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG Image", extj);
                chooser.setFileFilter(filter);

                int rtnval = chooser.showOpenDialog(bottomPanel);
                if(rtnval == JFileChooser.APPROVE_OPTION){
                    File f = chooser.getSelectedFile();

                    R.Reset();
                    Mat image = Highgui.imread(f.getPath());
                    Mat result = R.RecognizeAndSolve(image);
                    Utils.showResult(result);

                    updateSudo(R.GetCurrentSudoku());
                }
            }
        }

        /**
         * export the sudoku to an image
         */
        private class exportEventListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                Mat image = R.ExportAnswerToStandardSudokuImage();

                if(image == null) {
                    JOptionPane.showMessageDialog(null,
                            "No sudoku answer to export!",
                            "Alert", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                String extj[] = {"jpeg", "jpg", "png", "bmp"};
                FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG Image", extj);
                chooser.setFileFilter(filter);

                int rtnval = chooser.showSaveDialog(bottomPanel);
                if(rtnval == JFileChooser.APPROVE_OPTION){
                    File f = chooser.getSelectedFile();

                    File saveFile = new File(f.getAbsolutePath() + "/export.png");

                    if(saveFile.exists())
                    {
                        int overwriteSelect = JOptionPane.showConfirmDialog(bottomPanel,
                                "File " + saveFile.getName() + " already exists. Overwritten?",
                                "Overwritten?",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (overwriteSelect != JOptionPane.YES_OPTION)
                        {
                            return;
                        }
                    }

                    Highgui.imwrite(saveFile.getAbsolutePath(), image);
                }
            }
        }

        /**
         * select a sudo type
         */
        private class sudoTypeEventListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(suduTypeComboBox.getSelectedIndex() != -1) {
                    R.Reset();
                    R.SetSudokuSize(suduTypeComboBox.getSelectedIndex() + 4);

                    topPanel.removeAll();
                    paintSudo();
                }
            }
        }
    }
}
