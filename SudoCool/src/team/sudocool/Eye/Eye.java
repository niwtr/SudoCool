package team.sudocool.Eye;

/**
 * Created by Heranort on 16/11/15.
 */
import org.opencv.core.Mat;
import team.sudocool.ImgWorks.GridSquareExtractor;
import team.sudocool.ImgWorks.nImgProc.Utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;





public class Eye extends JFrame implements ActionListener {
    private JPanel contentPane;

    public  Mat captured;
    private JButton snap;

  /*
     * Launch the application.
     */
    public void Watch() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Eye frame = new Eye();
                    SnapGUI();
                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Eye() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 650, 490);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        new eyeThread().start();
    }


    public void SnapGUI(){
        JFrame jf=new JFrame();
        JButton jb=new JButton("Snap!");
        jf.add(jb);
        jf.pack();
        jb.addActionListener(this);
        jb.setActionCommand("snap");
        jf.setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        if ("snap".equals(e.getActionCommand())) {
            Mat u= snapAPhoto();
            Utils.showResult(u);

        }
    }

    VideoCap videoCap = new VideoCap();

    public void paint(Graphics g){
        g = contentPane.getGraphics();
        BufferedImage bfi=videoCap.getOneFrame();
        g.drawImage(bfi, 0, 0, this);
    }

    public Mat snapAPhoto(){

        this.captured=videoCap.captured;
        return captured;
    }


    private class eyeThread extends Thread{
        @Override
        public void run() {
            for (;;){
                 repaint();

                try { Thread.sleep(30);
                } catch (InterruptedException e) {    }
            }
        }
    }
}
