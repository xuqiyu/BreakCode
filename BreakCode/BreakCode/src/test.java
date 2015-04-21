import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class test {
	public static void main(String args[]) throws IOException{
		
		BufferedImage image=ImageIO.read(new File("D:/yzm/imagesplit/J.png"));
//		for(int i=0;i<=15;i++){
//		image.setRGB(0, i, Color.RED.getRGB());
		image.setRGB(1, 18, Color.WHITE.getRGB());
		ImageIO.write(image, "PNG", new File("D:/yzm/imagesplit/J.png"));
		System.out.println("Íê³É");
		
	}
}
