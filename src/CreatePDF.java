import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;

public class CreatePDF {
	private PDDocument scanPDF;
	private String title;
	private String path;

	public CreatePDF(String title, String path) {
		scanPDF = new PDDocument();
		this.title = title;
		this.path = path;
		scanPDF.getDocumentInformation().setTitle(title);
	}

	public void addImagePage(ImageDl img) {
		PDPage page = new PDPage(new PDRectangle(img.getWidth(),img.getHeight()));
		scanPDF.addPage(page);
		try {
			PDImageXObject pdImg = JPEGFactory.createFromImage(scanPDF,img.getBuffImage());
			PDPageContentStream streamImg = new PDPageContentStream(scanPDF,page);
			streamImg.drawImage(pdImg,0,0);
			streamImg.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void savePDF() {
		try {
			scanPDF.save(path+title+".pdf");
			scanPDF.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
