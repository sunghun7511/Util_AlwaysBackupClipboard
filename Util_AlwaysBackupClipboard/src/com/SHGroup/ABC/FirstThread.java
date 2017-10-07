package com.SHGroup.ABC;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class FirstThread extends Thread{
	public static JTextArea jta;
	public static JFrame sjf;
	public static BufferedImage simage;
	public static JScrollPane jsp;
	public static JCheckBox disable, check;
	public static JTextField outpath;
	public static JTextField color;
	public static JTextField path;
	@Override
	public void run(){
		JFrame jf = new JFrame("AlwaysBackupClipboard");

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {} 
		
		jta = new JTextArea(){
			private static final long serialVersionUID = 40024424799624764L;
			@Override
			public void append(String n){
				String date = new SimpleDateFormat("[hh:mm:ss] ").format(new Date());
				System.out.println(date + n);
				n = (getText()==null||getText().equals("")?"":"\n")
						+ date + n;
				super.append(n);
				jta.setCaretPosition(jta.getDocument().getLength());
			}
		};
		jta.setEditable(false);
		jta.setBounds(5, 5, 485, 437);
		
		jsp = new JScrollPane(jta);
		jsp.setBounds(5, 5, 485, 437);
		
		JButton clear = new JButton("로그 지우기");
		clear.setBounds(4, 447, 485, 20);
		
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jta.setText("");
				jta.append("로그를 지웠습니다.");
			}
		});
		
		JLabel jl1 = new JLabel("저장 경로");
		jl1.setBounds(10, 475, 60, 20);
		
		outpath = new JTextField("D:/DevOutput/AutoBackupClipboard/");
		outpath.setBounds(80, 475, 340, 20);
		
		JButton open = new JButton("열기");
		open.setBounds(425, 475, 65, 20);
		
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					Desktop.getDesktop().open(new File(outpath.getText()));
				}catch(Exception ex){
					jta.append("경로가 올바르지 않습니다.");
				}
			}
		});
		
		disable = new JCheckBox("비활성화");
		disable.setBounds(10, 500, 120, 20);
		
		check = new JCheckBox("캡쳐하자마자 바로 리사이즈");
		check.setBounds(200, 500, 500, 20);
		
		check.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if(check.isSelected()){
					color.setEnabled(true);
				}else{
					color.setEnabled(false);
				}
			}
		});
		
		JLabel jl2 = new JLabel("색깔코드");
		jl2.setBounds(10, 525, 80, 20);
		
		color = new JTextField("F0F0F0");
		color.setBounds(80, 525, 410, 20);
		
		color.setEnabled(false);
		
		JLabel jl3 = new JLabel("색깔코드 추출 파일 경로");

		jl3.setBounds(10, 550, 150, 20);

		path = new JTextField("");
		path.setBounds(160, 550, 200, 20);
		
		JButton button = new JButton("색깔코드 추출!");
		button.setBounds(370, 550, 120, 20);
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if(sjf != null){
					sjf.setAlwaysOnTop(true);
					sjf.setAlwaysOnTop(false);
					return;
				}
				try{
					File f = new File(path.getText());
					if(!f.exists()){
						jta.append("오류가 발생했습니다. (파일이 존재하지 않습니다.)");
						return;
					}
					simage = ImageIO.read(f);
					if(simage == null){
						jta.append("오류가 발생했습니다. (이미지파일을 불러올 수 없습니다.)");
						return;
					}
					sjf = new JFrame("색깔코드 추출");
			        
					sjf.addWindowListener(new WindowListener() {
						@Override
						public void windowOpened(WindowEvent windowevent) {
						}
						@Override
						public void windowIconified(WindowEvent windowevent) {
						}
						@Override
						public void windowDeiconified(WindowEvent windowevent) {
						}
						@Override
						public void windowDeactivated(WindowEvent windowevent) {
						}
						@Override
						public void windowClosing(WindowEvent windowevent) {
							sjf.setVisible(false);
							sjf = null;
						}
						@Override
						public void windowClosed(WindowEvent windowevent) {
							sjf.setVisible(false);
							sjf = null;
						}
						@Override
						public void windowActivated(WindowEvent windowevent) {
						}
					});
					ImageSelection imgs = new ImageSelection(simage);
					imgs.addMouseListener(new MouseListener() {
						
						@Override
						public void mouseReleased(MouseEvent mouseevent) {
						}
						@Override
						public void mousePressed(MouseEvent mouseevent) {
						}
						@Override
						public void mouseExited(MouseEvent mouseevent) {
						}
						@Override
						public void mouseEntered(MouseEvent mouseevent) {
						}
						@Override
						public void mouseClicked(MouseEvent e) {
							Color c = new Color(simage.getRGB(e.getX(),e.getY()));
							FirstThread.color.setText(Integer.toHexString(c.getRed()).toUpperCase()
									+ Integer.toHexString(c.getGreen()).toUpperCase()
									+ Integer.toHexString(c.getBlue()).toUpperCase());
							sjf.setVisible(false);
							sjf = null;
						}
					});
					sjf.add(imgs);
			        sjf.pack();
			        sjf.setVisible(true);
				}catch(Exception ex){
					jta.append("오류가 발생했습니다.");
				}
			}
		});
		
		jf.add(jsp);
		jf.add(clear);
		jf.add(disable);
		jf.add(check);
		jf.add(jl1);
		jf.add(outpath);
		jf.add(jl2);
		jf.add(jl3);
		jf.add(open);
		jf.add(path);
		jf.add(button);
		jf.add(color);
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setLayout(null);
		jf.setResizable(false);
		jf.setBounds(10, 10, 500, 613);
		jf.setVisible(true);
		
		jta.append("프로그램이 실행되었습니다.");
	}
	
	public static void log(final String msg){
		jta.append(msg);
	}
	public class ImageSelection extends Component{
		private static final long serialVersionUID = 1L;
		private final BufferedImage img;
		public ImageSelection(BufferedImage image) throws IOException{
			img = image;
		}
		@Override
		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}

		public Dimension getPreferredSize() {
			if (img == null) {
				return new Dimension(100, 100);
			} else {
				return new Dimension(img.getWidth(null), img.getHeight(null));
			}
	     }
    }
}
