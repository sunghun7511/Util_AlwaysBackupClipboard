package com.SHGroup.ABC;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

public class Main extends Thread implements ClipboardOwner {
	private Clipboard sysClip = Toolkit.getDefaultToolkit()
			.getSystemClipboard();
	
	private FirstThread ft;

	public static void main(String[] args) {
		new Main().start();
	}

	@Override
	public void run() {
		ft = new FirstThread();
		ft.start();
		Transferable trans = sysClip.getContents(this);
		regainOwnership(trans);
	}

	@Override
	public void lostOwnership(Clipboard c, Transferable t) {
		try {
			sleep(50);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}

		FirstThread.log("호출되었습니다.");
		Transferable contents = null;
		while (contents == null) {
			contents = sysClip.getContents(this);
			try {
				sleep(50);
			} catch (Exception e) {
				System.out.println("Exception: " + e);
			}
		}
		final Transferable cont2 = contents;
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					start(cont2);
					regainOwnership(cont2);
				} catch (Exception e) {
					FirstThread.log("오류가 발생했습니다.");
					e.printStackTrace();
				}
			}
		}, 10l);
	}

	public void regainOwnership(Transferable t) {
		sysClip.setContents(t, this);
	}

	public void start(Transferable clip) {
		if(FirstThread.disable.isSelected()){
			return;
		}
		FirstThread.log("다운을 시작합니다.");
		String date = new SimpleDateFormat("yyyyMMdd_hhmmssSS")
				.format(new Date());
		File f = null;
		if (clip.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			try {
				Image img = (Image) clip
						.getTransferData(DataFlavor.imageFlavor);
				if (img == null)
					return;
				int width = img.getWidth(null);
				int height = img.getHeight(null);
				BufferedImage bi = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics g = bi.createGraphics();
				g.drawImage(img, 0, 0, null);
				g.dispose();
				String out = FirstThread.outpath.getText();
				f = new File(
						out == null || out.equals("") ? "D:/DevOutput/AutoBackupClipboard/"
								: out + date + ".png");
				ImageIO.write(bi, "png", f);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (FirstThread.check.isSelected()) {
				try {
					FirstThread
							.log("파일 자동 크기 설정을 시작합니다. (" + f.getName() + ")");
					Color color = new Color(Integer.parseInt(FirstThread.color
							.getText().substring(0, 2), 16), Integer.parseInt(
							FirstThread.color.getText().substring(2, 4), 16),
							Integer.parseInt(FirstThread.color.getText()
									.substring(4, 6), 16));
					BufferedImage image = ImageIO.read(f);
					int width = image.getWidth();
					int height = image.getHeight();
					int startx = 0;
					int starty = 0;
					int stopx = 0;
					int stopy = 0;
					byte temp = (byte) 0;
					int maxgraywidth = 0;
					int maxgrayheight = 0;
					for (int x = 0; x < width; x++) {
						int allheight = 0;
						for (int y = 0; y < height; y++) {
							if (isSame(image.getRGB(x, y), color)) {
								allheight += 1;
							}
						}
						if (maxgrayheight < allheight) {
							maxgrayheight = allheight;
						}
					}
					for (int y = 0; y < height; y++) {
						int allwidth = 0;
						for (int x = 0; x < width; x++) {
							if (isSame(image.getRGB(x, y), color)) {
								allwidth += 1;
							}
						}
						if (maxgraywidth < allwidth) {
							maxgraywidth = allwidth;
						}
					}
					for (int x = 0; x < width; x++) {
						int ally = 0;
						for (int y = 0; y < height; y++) {
							if (isSame(image.getRGB(x, y), color)) {
								ally += 1;
							}
						}
						if (ally >= maxgrayheight - 10) {
							if (temp == 0) {
								temp = 1;
								continue;
							} else if (temp == 2) {
								stopx = x;
								break;
							}
						} else {
							if (temp == 1) {
								startx = x;
								temp = 2;
								continue;
							}
						}
					}
					temp = 0;
					for (int y = 0; y < height; y++) {
						int allx = 0;
						for (int x = 0; x < width; x++) {
							if (isSame(image.getRGB(x, y), color)) {
								allx += 1;
							}
						}
						if (allx >= maxgraywidth - 10) {
							if (temp == 0) {
								temp = 1;
								continue;
							} else if (temp == 2) {
								stopy = y;
								break;
							}
						} else {
							if (temp == 1) {
								starty = y;
								temp = 2;
								continue;
							}
						}
					}
					File file = new File(f.getPath());
					f.delete();
					file.getParentFile().mkdirs();
					ImageIO.write(
							image.getSubimage(startx, starty, width - startx
									- (width - stopx), height - starty
									- (height - stopy)), "png", file);
					FirstThread.log("파일 자동 크기 설정을 성공하였습니다. (" + file.getPath()
							+ ")");
				} catch (Exception ex) {
					FirstThread.log("파일 자동 크기 설정에 실패하였습니다. (" + f.getPath()
							+ ")");
					FirstThread.log("ERROR - " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		} else if (clip.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				String txt = (String) clip
						.getTransferData(DataFlavor.stringFlavor);
				String out = FirstThread.outpath.getText();
				f = new File(
						out == null || out.equals("") ? "D:/DevOutput/AutoBackupClipboard/"
								: out
										+ new SimpleDateFormat(
												"yyyyMMdd_hhmmssSS")
												.format(new Date()) + ".txt");
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				bw.append(txt);
				bw.flush();
				bw.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (f == null) {
			FirstThread.log("저장하지 못했습니다.");
		} else {
			FirstThread.log("저장하였습니다.");
		}
	}

	private boolean isSame(int rgb, Color color2) {
		Color c = new Color(rgb);
		if (c.getRed() != color2.getRed()) {
			return false;
		}
		if (c.getGreen() != color2.getGreen()) {
			return false;
		}
		if (c.getBlue() != color2.getBlue()) {
			return false;
		}
		return true;
	}

}
