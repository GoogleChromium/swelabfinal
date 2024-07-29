package artcreator.creator.impl;

import artcreator.domain.port.Domain;
import artcreator.statemachine.port.StateMachine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CreatorImpl {

	private BufferedImage startImage;
	private BufferedImage endImage;
	private StringBuilder pixelInfo;

	public StringBuilder getPixelInfo() {
		return pixelInfo;
	}

	public void setPixelInfo(StringBuilder pixelInfo) {
		this.pixelInfo = pixelInfo;
	}

	public void setStartImage(BufferedImage startImage) {
		this.startImage = startImage;
	}

	public BufferedImage getStartImage() {
		return startImage;
	}

	public void setEndImage(BufferedImage endImage) {
		this.endImage = endImage;
	}

	public BufferedImage getEndImage() {
		return endImage;
	}

	public CreatorImpl(StateMachine stateMachine, Domain domain) {

	}

	public void sysop(String str) {
		System.out.println(str);
		
	}

	public BufferedImage processImage(int pixelSize) {
		System.out.println("Creating pointillism image...");
		StringBuilder pixelInfo = new StringBuilder();
		try {
			BufferedImage original = startImage;
			BufferedImage pointillismImage = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2d = pointillismImage.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			for (int y = 0; y < original.getHeight(); y += pixelSize) {
				for (int x = 0; x < original.getWidth(); x += pixelSize) {
					int r = 0, g = 0, b = 0, count = 0;

					for (int dy = 0; dy < pixelSize; dy++) {
						for (int dx = 0; dx < pixelSize; dx++) {
							if (x + dx < original.getWidth() && y + dy < original.getHeight()) {
								Color color = new Color(original.getRGB(x + dx, y + dy));
								r += color.getRed();
								g += color.getGreen();
								b += color.getBlue();
								count++;
							}
						}
					}

					r /= count;
					g /= count;
					b /= count;

					Color averageColor = new Color(r, g, b);
					g2d.setColor(averageColor);
					g2d.fillOval(x, y, pixelSize, pixelSize);

					pixelInfo.append(String.format("Pixel at (%d, %d): #%02x%02x%02x\n", x, y, r, g, b));

				}
			}
			setPixelInfo(pixelInfo);
			g2d.dispose();
			setStartImage(pointillismImage);
			System.out.println("Conversion complete.");
			setEndImage(pointillismImage);
			return pointillismImage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void exportImage(File file) throws IOException {
		ImageIO.write(endImage, "png", file);
	}
}
