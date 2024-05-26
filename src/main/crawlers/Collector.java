package crawlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Collector {
	protected String websiteSource;
	protected String fileName;
	protected File outputFile;
	protected BufferedWriter fw;
	protected String pageIndex;
	protected int count;

	protected boolean createOutputFile() {
		try {
			if (outputFile.createNewFile()) {
				System.out.println("File created: " + outputFile.getName());
				setFw();
				fw.write(
						"Article link, Website source, Article type, Article title, Content, Creation date, Author, Category, Tags, Summary\n");
				fw.close();
				System.out.println("Successfully written!");
			} else {
				System.out.println("File already exists!");
			}
			return true;
		} catch (IOException e) {
			System.out.println("Error with file!");
			e.printStackTrace();
			return false;
		}
	}

	protected boolean writeOutputFile(String url, String type, String title, String content, String dateCreated,
			String author, String category, String summary, String tags) {
		try {
			setFw();
			// Article link, Website source, Article type, Article title, Content, Creation
			// date, Author, Category, Tags, Summary
			fw.write("\"");fw.write(url);fw.write("\",");
			fw.write("\"");fw.write(websiteSource);fw.write("\",");
			fw.write("\"");fw.write(type);fw.write("\",");
			fw.write("\"");fw.write(title);fw.write("\",");
			fw.write("\"");fw.write(content);fw.write("\",");
			fw.write("\"");fw.write(dateCreated);fw.write("\",");
			fw.write("\"");fw.write(author);fw.write("\",");
			fw.write("\"");fw.write(category);fw.write("\",");
			fw.write("\"");fw.write(tags);fw.write("\",");
			fw.write("\"");fw.write(summary);fw.write("\"\n");
			fw.close();
			return true;
		} catch (IOException ex) {
			System.err.println("IOException: " + ex.getMessage());
			return false;
		}
	}

	public Document request(String url, boolean isRoot) {
		try {
			Connection con = Jsoup.connect(url);
			Document doc = con.get();
			if (con.response().statusCode() == 200) {
				return doc;
			}
		} catch (IOException e) {
			if (isRoot == true) {
				System.out.println("Can't connect.");
			} else {
				System.out.println("Can't connect leaf page.");
			}
			e.printStackTrace();
		}
		return null;
	}

	public File getOutputFile() {
		return outputFile;
	}

	protected void setOutputFile() {
		this.outputFile = new File(fileName);
	}

	public BufferedWriter getFw() {
		return fw;
	}

	protected void setFw() {
		try {
			this.fw = new BufferedWriter(
						new OutputStreamWriter(
							new FileOutputStream(fileName, true),
							StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			e.printStackTrace();
		}
	}

	public String getWebsiteSource() {
		return websiteSource;
	}

	protected void setWebsiteSource(String websiteSource) {
		this.websiteSource = websiteSource;
	}

	public String getFileName() {
		return fileName;
	}

	protected void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPageIndex() {
		return pageIndex;
	}

	protected void setPageIndex(String pageIndex) {
		this.pageIndex = pageIndex;
	}
	
	protected void setCount(int c) {
		this.count = c;
	}
}