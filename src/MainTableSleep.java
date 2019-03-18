import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainTableSleep {
	public static void main(String[] args) {
		if (!(args.length == 3 || args.length == 4)) {
			System.out.println("Command need three arguments (and a 4th optional delay):\n" +
					"1) -f or -d to fetch or download the source images (an optional u to download a unique chapter)\n" +
					"2) link to the first page of the scan\n" +
					"3) folder to store the sources and the pdf\n" +
					"4) delay between each image (in ms) fetch/download, add some if bad connection (default 50ms)");
			System.exit(1);
		}
		if (!args[1].contains("fanfox.net")) {
			System.out.println("The first link need to be the first page of the manga in fanfox !");
			System.exit(2);
		}

		String startScan = args[1];
		String path = args[2];
		WebDriver driver = new FirefoxDriver();
		WebElement nextChapter;
		WebElement nextPage;
		List<WebElement> scanPage = null;
		String urlScanPage;
		String title = null;
		ImageDl imageDl;
		CreatePDF pdf;
		List<String> notFound404 = new ArrayList<>();
		boolean saveNo404;
		boolean downloadImage;
		boolean pageLoop;
		boolean chapterLoop = true;
		int delay = 50;
		(new File(path)).mkdirs();

		boolean containLoad;
		JavascriptExecutor jse;

		if (args.length == 4) {
			delay = Integer.parseInt(args[3]);
		}

		switch (args[0]) {
			case "-f":
				downloadImage = false;
				break;
			case "-d":
				downloadImage = true;
				break;
			case "-fu":
				downloadImage = false;
				chapterLoop = false;
				break;
			case "-du":
				downloadImage = true;
				chapterLoop = false;
				break;
			default:
				downloadImage = false;
				System.out.println("first argument needs to be either -f or -d to fetch or download the source images ! (u for optional unique download)");
				System.exit(3);
		}

		try {
			driver.get(startScan);

			//passing the age check if existent
			try {
				driver.findElement(By.id("checkAdult")).click();
				System.out.println("adult check needed");
			} catch (NoSuchElementException e) {
				System.out.println("no adult check needed");
			}

			do {
				//creating the pdf for the chapter
				title = driver.findElement(By.className("reader-header-title-2")).getText();
				pdf = new CreatePDF(title, path);
				saveNo404 = true;

				do {
					pageLoop = true;
					//wait for the image to load
					do {
						containLoad = false;
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						try {
							scanPage = driver.findElements(By.className("reader-main-img"));
							for (WebElement load : scanPage) {
								if (load.getAttribute("src").contains("loading")) {
									containLoad = true;
									break;
								}
							}
						} catch (StaleElementReferenceException e) {
							containLoad = true;
						}
						jse = ((JavascriptExecutor) driver);
						jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
					} while (containLoad);

					try {
						for (WebElement scanImages : scanPage) {
							urlScanPage = scanImages.getAttribute("src");
							imageDl = new ImageDl(urlScanPage, path, downloadImage);
							pdf.addImagePage(imageDl);
							System.out.print(".");
						}
					} catch (IOException e) {
						notFound404.add(title + ": " + driver.getCurrentUrl());
						System.out.println("404 can't download " + title);
						pdf.savePDF();
						(new File(path + title +".pdf")).delete();
						pageLoop = false;
						saveNo404 = false;
					}

					//changing to the next page if it exists
					try {
						nextPage = driver.findElement(By.cssSelector(".pager-list span a:last-child"));
						if (nextPage.getText().equals(">")) {
							nextPage.click();
						} else {
							pageLoop = false;
							System.out.println();
						}
					} catch (NoSuchElementException e) {
						pageLoop = false;
						System.out.println();
					}
				} while (pageLoop);

				if (saveNo404) {
					pdf.savePDF();
				}

				//changing to the next chapter if it exists
				if (chapterLoop) {
					try {
						nextChapter = driver.findElement(By.cssSelector(".pager-list-left > a:last-child"));
						nextChapter.click();
						scanPage = null;
						System.out.println("next chapter");
					} catch (NoSuchElementException e) {
						System.out.println("no more chapters");
						chapterLoop = false;
					}
				}
			} while (chapterLoop);
			System.out.println("end of the download");
			if (!notFound404.isEmpty()) {
				System.out.println("\ncouldn't download this chapter:");
				for (String notFound : notFound404) {
					System.out.println(notFound);
				}
			}
			driver.quit();
		} catch (WebDriverException e) {
			System.out.println("Currently was downloading " + title);
			if (!notFound404.isEmpty()) {
				System.out.println("couldn't download this chapter:");
				for (String notFound : notFound404) {
					System.out.println(notFound);
				}
			}
		}
	}
}
