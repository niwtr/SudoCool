package team.sudocool.Eye;

import org.opencv.core.Mat;
import org.opencv.highgui.*;
import team.sudocool.ImgWorks.Recognizer;
import team.sudocool.ImgWorks.nImgProc.Utils;
import team.sudocool.Solver.ReadSudo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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

        E = new EyeFrame();
        S = new SudoFrame();
        V = new VideoCap();
        R = new Recognizer();

//        JFrame MainWindow = new JFrame();
//        MainWindow.setLayout(new GridLayout(1, 2));
//        MainWindow.add(E);
//        MainWindow.add(S);
//        MainWindow.setVisible(true);

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
                E.setCaptured(result);
                S.updateSudo(R.GetCurrentSudoku());
                E.repaint();

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
        private JButton pauseButton, shiftButton, solveButton, resetButton, fileOpenButton;
        private JPanel topPanel, bottomPanel;
        private JSpinner[][] sudoSpinner;


        /**
         * creat all the frame
         */
        public SudoFrame() {
            topPanel = new JPanel();
            bottomPanel = new JPanel();
            pauseButton = new JButton("PAUSE");
            shiftButton = new JButton("PRINTING");
            solveButton = new JButton("SOLVE");
            resetButton = new JButton("RESET");
            fileOpenButton = new JButton("OPEN");

            sudoSpinner = new JSpinner[9][9];
            for(int i = 0; i < 9; i++) {
                for(int j = 0; j < 9; j++) {
                    sudoSpinner[i][j] = new JSpinner();
                }
            }

            setEditSudo(false);
            if(mode == PRINTING)
                solveButton.setEnabled(false);
            fileOpenButton.setEnabled(false);
            solveButton.setEnabled(true);

            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("Soduku");

            JPanel contentPaneBoss = new JPanel();
            this.setContentPane(contentPaneBoss);

            contentPaneBoss.setLayout(new BoxLayout(contentPaneBoss, BoxLayout.Y_AXIS));
            contentPaneBoss.add(Box.createVerticalStrut(10));
            contentPaneBoss.add(topPanel);
            contentPaneBoss.add(Box.createVerticalStrut(10));
            contentPaneBoss.add(bottomPanel);
            contentPaneBoss.add(Box.createVerticalStrut(7));

            paintSudo();
            paintButton();

            this.setBounds(900, 100, 450, 450);
    //        this.pack();
            this.setResizable(true);
            this.setVisible(true);
        }

        /**
         * paint the sudo table
         */
        private void paintSudo() {
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
            topPanel.add(Box.createHorizontalStrut(10));
            JPanel sudoTable = new JPanel(new GridLayout(3, 3));
            topPanel.add(sudoTable);
            topPanel.add(Box.createHorizontalStrut(10));

            for(int w = 0; w < 3; w++)
            {
                for(int q = 0; q < 3; q++)
                {
                    JPanel boxPanel = new JPanel(new GridLayout(3, 3));
                    boxPanel.setBorder(BorderFactory.createLineBorder(Color.PINK));
                    sudoTable.add(boxPanel);

                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            JSpinner num = sudoSpinner[w*3+i][q*3+j];
                            SpinnerModel model = new SpinnerNumberModel(0, 0, 9, 1);
                            num.setModel(model);

//                            num.getEditor().getComponent(0).setForeground(Color.PINK);
                            boxPanel.add(num);
                        }
                    }
                }
            }
        }

        /**
         * paint the button on bottom
         */
        private void paintButton() {
            pauseButton.addActionListener(new pauseEventListener());
            shiftButton.addActionListener(new switchEventListener());
            solveButton.addActionListener(new solveEventListener());
            resetButton.addActionListener(new resetEventListener());
            fileOpenButton.addActionListener(new openEventListener());

            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(fileOpenButton);
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(pauseButton);
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(solveButton);
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(resetButton);
            bottomPanel.add(Box.createHorizontalGlue());
            bottomPanel.add(shiftButton);
            bottomPanel.add(Box.createHorizontalGlue());
        }

        private void setEditSudo(boolean edit) {
            for(int i = 0; i < 9; i++)
                for(int j = 0; j < 9; j++)
                {
                    sudoSpinner[i][j].setEnabled(edit);
                }
        }

        /**
         * when sudo changed, update the table
         * @param sudoData current sudoku
         */
        private void updateSudo(int[][] sudoData) {
            for(int i = 0; i < 9; i++)
                for(int j = 0; j < 9; j++)
                    sudoSpinner[i][j].setValue(sudoData[i][j] == -1 ? 0 : sudoData[i][j]);
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
//                    resetButton.setEnabled(false);
                    fileOpenButton.setEnabled(true);
                }
                else if(pauseButton.getText().equals("CONTINUE"))
                {
                    eyethread.resume();
                    pauseButton.setText("PAUSE");
                    setEditSudo(false);
                    solveButton.setEnabled(false);
//                    resetButton.setEnabled(true);
                    fileOpenButton.setEnabled(false);
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
                int[][] sudoData = new int[9][9];
                for(int i = 0; i < 9; i++)
                {
                    for(int j = 0; j < 9; j++)
                    {
                        sudoData[i][j] = (Integer) sudoSpinner[i][j].getValue();
                        if(sudoData[i][j] == 0)
                            sudoData[i][j] = -1;
                    }
                }


                if(!R.Solve(sudoData))
                    JOptionPane.showMessageDialog(null, "No answer!", "Alert", JOptionPane.ERROR_MESSAGE);
                else
                {
                    updateSudo(R.GetCurrentSudoku());
                    JOptionPane.showMessageDialog(null, "Let`s see it!", "Congratulation", JOptionPane.INFORMATION_MESSAGE);
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
                JFileChooser fd = new JFileChooser();
                //fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fd.showOpenDialog(null);
                File f = fd.getSelectedFile();
                if(f != null){
                    R.Reset();

                    Mat image = Highgui.imread(f.getPath());
                    Mat result = R.RecognizeAndSolve(image);
                    Utils.showResult(result);

                    updateSudo(R.GetCurrentSudoku());
                }
            }
        }
    }
}
