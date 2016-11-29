package team.sudocool.Eye;

import org.opencv.core.Mat;
import team.sudocool.ImgWorks.Recognizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * create all the frame
 * @author L.Laddie
 * @version 1.0
 * @since 2016/11/28
 */
public class Eye {
    public static final int PRINTING = 0;
    public static final int HANDWRITING = 0;

    private EyeFrame E;
    private SudoFrame S;
    private VideoCap V;
    private Recognizer R;

    private EyeThread eyethread;
    private int mode;

    public Eye() {
        try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

        } catch (Exception e) {
            e.printStackTrace();
        }

        E = new EyeFrame();
        S = new SudoFrame();
        V = new VideoCap();
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
        private JButton pauseButton, shiftButton, solveButton, resetButton;
        private JPanel topPanel, bottomPanel;
        private JSpinner[][] sudoSpinner;


        /**
         * creat all the frame
         */
        public SudoFrame() {
            topPanel = new JPanel();
            bottomPanel = new JPanel();
            pauseButton = new JButton("PAUSE");
            shiftButton = new JButton("to HANDWRITING");
            solveButton = new JButton("SOLVE");
            resetButton = new JButton("RESET");
            sudoSpinner = new JSpinner[9][9];
            for(int i = 0; i < 9; i++) {
                for(int j = 0; j < 9; j++) {
                    sudoSpinner[i][j] = new JSpinner();
                }
            }

            setEditSudo(false);
            if(mode == PRINTING)
                solveButton.setEnabled(false);

            this.setTitle("Soduku");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel contentPaneBoss = new JPanel();
            this.setContentPane(contentPaneBoss);

            contentPaneBoss.setLayout(new BoxLayout(contentPaneBoss, BoxLayout.Y_AXIS));
            contentPaneBoss.add(Box.createVerticalStrut(10));
            contentPaneBoss.add(topPanel);
            contentPaneBoss.add(Box.createVerticalStrut(10));
            contentPaneBoss.add(bottomPanel);
            contentPaneBoss.add(Box.createVerticalStrut(5));

            paintSudo();
            paintButton();

            this.setBounds(900, 100, 400, 400);
    //        this.pack();
            this.setResizable(true);
            this.setVisible(true);
        }

        /**
         * paint the sudo table
         */
        private void paintSudo() {
            topPanel.setLayout(new GridLayout(10, 10));

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (i == 0 && j == 0)
                        topPanel.add(new Label());
                    else {
                        if (j == 0) {
                            JLabel label = new JLabel(String.valueOf(i));
                            label.setHorizontalAlignment(JLabel.CENTER);
                            topPanel.add(label);
                        } else if (i == 0) {
                            JLabel label = new JLabel(String.valueOf(j));
                            label.setHorizontalAlignment(JLabel.CENTER);
                            topPanel.add(label);
                        } else {
                            JSpinner num = sudoSpinner[i-1][j-1];
                            SpinnerModel model = new SpinnerNumberModel(0, -1, 9, 1);
                            num.setModel(model);
                            topPanel.add(num);
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

            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
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
        public void updateSudo(int[][] sudoData) {
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
                }
                else if(pauseButton.getText().equals("CONTINUE"))
                {
                    eyethread.resume();
                    pauseButton.setText("PAUSE");
                    setEditSudo(false);
                    solveButton.setEnabled(false);
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
                if(shiftButton.getText().equals("to HANDWRITING"))
                {
                    shiftButton.setText("to PRINTING");
                    mode = HANDWRITING;
                }
                else if(shiftButton.getText().equals("to PRINTING")){
                    shiftButton.setText("to HANDWRITING");
                    mode = PRINTING;
                    solveButton.setEnabled(false);
                }

                R.Reset();
            }
        }

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
                    JOptionPane.showMessageDialog(null, "no answer!", "alert", JOptionPane.ERROR_MESSAGE);
            }
        }

        private class resetEventListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                R.Reset();
            }
        }
    }
}
