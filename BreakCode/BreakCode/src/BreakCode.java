import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;




public class BreakCode {
	
	
	private static Map<BufferedImage, String> trainMap = new HashMap<BufferedImage, String>();
	
	
	
	
	private static void downloadImage(int num) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        for (int i = 0; i < num; i++) {
            String url = "http://210.42.121.134/servlet/GenImg";
            HttpGet getMethod = new HttpGet(url);
            try {
                HttpResponse response = httpClient.execute(getMethod, new BasicHttpContext());
                HttpEntity entity = response.getEntity();
                InputStream instream = entity.getContent(); 
                OutputStream outstream = new FileOutputStream(new File("D:/yzm/images/", new Date().getTime() + ".png"));
                int l = -1;
                byte[] tmp = new byte[2048]; 
                while ((l = instream.read(tmp)) != -1) {
                    outstream.write(tmp);
                } 
                outstream.close();
            } finally {
                getMethod.releaseConnection();
            }
        }

        System.out.println("������֤����ϣ�");
    }
	
//	public static void generateStdDigitImgage() throws Exception {
//        File dir = new File("D:/yzm/images/");
//        File[] files = dir.listFiles(new ImageFileFilter("png"));
//        
//        int counter = 0;
//        for (File file : files) {
//            BufferedImage image = ImageIO.read(file);
//            
//            List<BufferedImage> digitImageList = splitImage(image);
//            for (int i = 0; i < digitImageList.size(); i++) {
//                BufferedImage bi = digitImageList.get(i);
//                ImageIO.write(bi, "PNG", new File("D:/yzm/imagesplit/", "temp_" + counter++ + ".png"));
//            }
//        }
//        System.out.println("���ɹ��ȶԵ�ͼƬ��ϣ��뵽Ŀ¼���ֹ�ʶ��������ͼƬ����ɾ�������޹�ͼƬ��");
//    }
	
	
	private static List<BufferedImage> splitImage(BufferedImage image) throws Exception {
        final int DIGIT_WIDTH = 12;
        final int DIGIT_HEIGHT = 16;

        List<BufferedImage> digitImageList = new ArrayList<BufferedImage>();
        digitImageList.add(image.getSubimage(3, 3, DIGIT_WIDTH, DIGIT_HEIGHT));
        digitImageList.add(image.getSubimage(15, 3, DIGIT_WIDTH, DIGIT_HEIGHT));
        digitImageList.add(image.getSubimage(25, 3, DIGIT_WIDTH, DIGIT_HEIGHT));
        digitImageList.add(image.getSubimage(38, 3, DIGIT_WIDTH, DIGIT_HEIGHT));

        return digitImageList;
    }
	
	private static List<BufferedImage> desplitImage(BufferedImage image) throws Exception{
		int width=image.getWidth();
		int height=image.getHeight();
		List<Integer> splitX=new ArrayList<Integer>(); 
		List<Integer> realSplitX=new ArrayList<Integer>();
		
		for(int i=0;i<width;i++){
			int unTargetCount=0;
			for(int j=0;j<height;j++){
				int rgb=image.getRGB(i, j);
				
				if(rgb!=Color.WHITE.getRGB()){
					unTargetCount++;
					
				}	
			}
//		���J��Q������
			if(image.getRGB(i,height-1)!=Color.WHITE.getRGB()){
				unTargetCount++;
			}
			
			if(unTargetCount<2&&image.getRGB(i, 11)==Color.WHITE.getRGB()){
				splitX.add(i);
			}
		}
		
		for(int i=0;i<splitX.size()-1;i++){
			int current=splitX.get(i);
			int next=splitX.get(i+1);
			
			if(next-current>3){
				
//				if(image.getRGB(current-1, image.getHeight()-1)==Color.WHITE.getRGB()||image.getRGB(current+1, image.getHeight()-1)==Color.WHITE.getRGB()){
				
					realSplitX.add(current);
					realSplitX.add(next);
//				}
			}
			
		}
		List<BufferedImage> digitImageList = new ArrayList<BufferedImage>();
		for(int i=0;i<8;i+=2){
			try{
			digitImageList.add(image.getSubimage(realSplitX.get(i)+1, 0, realSplitX.get(i+1)-realSplitX.get(i), image.getHeight()));
			}
			catch(Exception e){
				
			}
		}
//		if(digitImageList.size()==4){
//			return digitImageList;
//		}
//		else{
//			return null;
//		}
		return digitImageList;
		
	}
	
	public static BufferedImage removeInterference(BufferedImage image) throws Exception {
		int width=image.getWidth();
		int height=image.getHeight();
		for(int i=0;i<width;i++){
			
			for(int j=0;j<height;j++){
				int rgb=image.getRGB(i, j);
				
				Color color=new Color(rgb);
				if(judgePoint(color.getRed(),color.getGreen(),color.getBlue())!=2){
					image.setRGB(i, j, Color.WHITE.getRGB());
					
					
				}else{
					image.setRGB(i, j, Color.RED.getRGB());
				}
			}
		}
		return image;
	} 
	
	
	public static List<BufferedImage> removeAllInterferences(String dir) throws Exception{
		File imagesDir=new File(dir);
		File[] files=imagesDir.listFiles(new ImageFileFilter("png"));
		List<BufferedImage> cleanImages=new ArrayList<BufferedImage>();
		for (File file : files){
			String fileNameWithType=file.getName();
			String fileNameWithoutType=fileNameWithType.substring(fileNameWithType.length()-4, fileNameWithType.length());
			
			BufferedImage image=removeInterference(ImageIO.read(file));
			cleanImages.add(image);
			ImageIO.write(image, "PNG", new File(dir+fileNameWithType+"_tmp.png"));
		}
		System.out.println("����ͼƬ�Ѿ���ȥ����ɫ");
		return cleanImages;
	}
	
	
	private static int judgePoint(int red,int green,int blue){
		int result=0;
//		����ɫrgb������200
		if(red>=200&&green>=200&&blue>=200){
			result=0;
		}
		
//		��ɫ
		else if(red<100&&green<100){
			result=1;
		}
//		����ɫ
		else if(red>110&&!(green>57&&blue>58)&&(green+blue<=105)||(red>200&&green<100&&blue<100)){
			result=2;
		}
//		����ʶ��
		else{
			result=3;
		}
		return result;
	}
	
	
	
	static class ImageFileFilter implements FileFilter {
        private String postfix = ".png";
        
        public ImageFileFilter(String postfix) {
            if(!postfix.startsWith("."))
                postfix = "." + postfix;
            
            this.postfix = postfix;
        }
        
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(postfix);
        }
    }
	
	
	private static void getTrainMap(){
		try {
            // ��TRAIN_DIRĿ¼�Ĺ��ȶԵ�ͼƬװ�ؽ���
            File dir = new File("D:/yzm/imagesplit/");
            File[] files = dir.listFiles(new ImageFileFilter("png"));
            for (File file : files) {
                trainMap.put(ImageIO.read(file), file.getName().charAt(0) + "");
//                System.out.println(file.getName().charAt(0) + "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	private static String validateImage(File file) throws Exception{
		BufferedImage image=ImageIO.read(file);
		removeInterference(image);
		List<BufferedImage> digitImageList=desplitImage(image);
		int width=20;
		int height=20;
		StringBuilder sb=new StringBuilder();
		for(BufferedImage eachImage:digitImageList){
			String imageValue="*";
			int minDiffCount = height*width;
			
			
			for(BufferedImage bi:trainMap.keySet()){
//				System.out.println("222222");
//				Ҫ��֤�ķ���ͼƬ��ԭ����������
				int currentDiffCount=0;
				for(int i=0;i<width;i++){
//					System.out.println("33333333333333");
					for(int j=0;j<height;j++){
//						System.out.println("44444444444");
						if(i<eachImage.getWidth()&&i<bi.getWidth()){
							if(eachImage.getRGB(i, j)!=bi.getRGB(i, j)){
								currentDiffCount++;
								
							}
						}else{
							currentDiffCount++;
						}
						
						
					}
				}
				if(currentDiffCount<minDiffCount){
					minDiffCount=currentDiffCount;
					imageValue=trainMap.get(bi);
//					System.out.println("================================================");
				}
				
				
//				Ҫ��֤�ķ���ͼƬ����һ�����غ�ԭ����������
				currentDiffCount=0;
				for(int i=0;i<width;i++){
					for(int j=0;j<height;j++){
						if(i+1<eachImage.getWidth()&&i<bi.getWidth()){
							if(eachImage.getRGB(i+1, j)!=bi.getRGB(i, j)){
								currentDiffCount++;
//								if (currentDiffCount >= minDiffCount) 
//									break outer;
							}
						}else{
							currentDiffCount++;
						}
						
					}
				}
				if(currentDiffCount<minDiffCount){
					minDiffCount=currentDiffCount;
					imageValue=trainMap.get(bi);
//					System.out.println("-------------------------------------------");
				}
				
				
//				Ҫ��֤�ķ���ͼƬ����һ�����غ�ԭ����������
				currentDiffCount=0;
				for(int i=0;i<width;i++){
					for(int j=0;j<height;j++){
						if(i-1<eachImage.getWidth()&&i-1>=0&&i<bi.getWidth()){
							if(eachImage.getRGB(i-1, j)!=bi.getRGB(i, j)){
								currentDiffCount++;
//								if (currentDiffCount >= minDiffCount) 
//									break outer;
							}
						}else{
							currentDiffCount++;
						}
						
					}
				}
				if(currentDiffCount<minDiffCount){
					minDiffCount=currentDiffCount;
					imageValue=trainMap.get(bi);
//					System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
				}
				
			}
			sb.append(imageValue);
		}
		ImageIO.write(image, "PNG", new File("D:/yzm/result/", sb.toString() + ".png"));
//		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        return sb.toString();
		
	}
	
	public static String breakCode(){
//		try {
//			removeAllInterferences("D:/yzm/imagesplit/");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		getTrainMap();
//		���ص�ͼƬ��
		try {
			downloadImage(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		���ص�ͼƬ������
		File downloadDir=new File("D:/yzm/images/");
//		�г���ЩͼƬ
		File[] imageFiles=downloadDir.listFiles();
		String result=new String();
		for(File imageFile:imageFiles){
//			���
			try {
				result=validateImage(imageFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		return result;

		
	}
	
	
	public static void main(String[] args) {
		
		String result=new BreakCode().breakCode();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println(result);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
	}

}
