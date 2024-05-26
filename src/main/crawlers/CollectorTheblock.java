package crawlers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CollectorTheblock 
	extends Collector 
	implements IGetData
{
	@Override
	public void getData() {
		setWebsiteSource("https://www.theblock.co");
		setFileName("theblock0.txt");
		setPageIndex("/latest?start=");
		setOutputFile();
		createOutputFile();
		webTraverse();
	}
	
	@Override
	public void webTraverse() {
		count = 0;
		String 	webPage = new String(""),
				url = new String("");
		Document doc;
		Elements urlsList;
		do {
			try {
				Thread.sleep(875);
			}
			catch(InterruptedException e) {
				break;
			}
			webPage = websiteSource + pageIndex + String.valueOf(count * 10);
			doc = this.request(webPage, true);
			if(doc == null) break;
			urlsList = doc.getElementsByClass("articleCard");
			if(urlsList.isEmpty()) {
				System.out.println("Page Empty! Done Data collecting!");
				break;
			}
			for(Element link : urlsList) {
				link = link.selectFirst("a");
				url = websiteSource + link.attr("href");
				pageTraverse(url);
			}
			System.out.println("Done attempt.");
			count++;
			System.out.println(count);
			System.gc();
		} while(true);
	}

	@Override
	public void pageTraverse(String url) {
		Document doc;
		String author, type = "News Article", title, content, dateCreated, category, summary, disclaimer, newsBox,tags = "";
		List<String> tagsList = new ArrayList<String>();
		Elements e;
		try {
			Thread.sleep(875);
		}
		catch(InterruptedException ex) {
		}
		doc = this.request(url, false);
		if(doc == null) return;
		e = doc.select("div.articleContent");
		
		title = e.select("article.articleBody > h1").text().replace('\"', '\'');
		author = e.select("a[href ^= https://www.theblock.co/author/]").text().replace('\"', '\'');
		category = e.select("a.categoryLink").text().replace('\"', '\'');
		dateCreated = e	.select("div.ArticleTimestamps > div").not("div:has(span)")
						.text().replace(category + " • ", "").replace('\"', '\'');
		summary = e.select("div.quickTake").text().replace("Quick Take ", "").replace('\"', '\'');
		disclaimer = e.select("div#articleContent > span > span.copyright").text().replace('\"', '\'');
		newsBox = e.select("div#articleContent > span > div.newsletterBox").text().replace('\"', '\'');
		content = e	.select("div#articleContent > span").text()
					.replace(disclaimer, "").replace(newsBox, "").replace('\"', '\'');
		e = doc.getElementsByClass("tag");
		for(Element tag : e) {
			tagsList.add(tag.text());
		}
		for(String tag : tagsList) {
			tags = tags + "-" + tag;
		}
		tags =  tags.replaceFirst("-", "").replace('\"', '\'');
		this.writeOutputFile(url, type, title, content, dateCreated, author, category, summary, tags);
		System.gc();
	}
}