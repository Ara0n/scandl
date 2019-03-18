import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageDl {
	private URL urlImage;
	private BufferedImage buffImage;
	private String path;
	private int width;
	private int height;
	private static int idImageDl = 1;

	public ImageDl(String urlImage, String folderPath, boolean download) throws IOException {
		try {
			this.urlImage = new URL(urlImage);
			path = folderPath+"source/"+idImageDl+".jpg";

			buffImage = ImageIO.read(this.urlImage);
			width = buffImage.getWidth();
			height = buffImage.getHeight();

			if (download) {
				(new File(folderPath + "source/")).mkdirs();
				ImageIO.write(buffImage, "jpg", new File(path));
				idImageDl++;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public URL getUrlImage() {
		return urlImage;
	}

	public BufferedImage getBuffImage() {
		return buffImage;
	}

	public String getPath() {
		return path;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public static int getIdImageDl() {
		return idImageDl;
	}

	public static void resetIdImageDl() {
		idImageDl = 1;
	}
}
