package crawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CollectorCryptonews 
extends Collector 
implements IGetData {

	@Override
	public void getData() {
		setWebsiteSource("https://cryptonews.com");
		setFileName("cryptonews.txt");
		setOutputFile();
		createOutputFile();
		setFw();
		setPageIndex("/news/page/");
		webTraverse();
	}

	@Override
	public void webTraverse() {
		count = 111;
		String webPage = new String(""),
				url = new String ("");
		Document doc;
		Elements urlList;
		do {
			webPage = websiteSource + pageIndex + String.valueOf(count);
			doc = this.request(webPage, true);
			if(doc == null) break;
			urlList = doc.select("div.news-one");
			if(urlList.isEmpty()) break;
			for(Element link: urlList) {
				url = link.select("a").attr("href");
				if(url.isEmpty() == false) {
					pageTraverse(url);
				}
			}
			count++;
			System.out.println(count);
			System.gc();
		} while(true);
	}

	@Override
	public void pageTraverse(String url) {
		Document doc = this.request(url, false);
		Elements tagLs;
		if (doc == null) return;
		doc = Jsoup.parse(doc.getElementsByClass("main").toString());
		String author, type = "News Article", title, content, dateCreated, category = "News", summary = "", tags = ""; 
		title = doc.getElementsByClass("mb-10").text().replace('\"', '\'');
		author = doc.select("div.author-title").text().replace('\"', '\'');
		dateCreated = doc.select("div.fs-14.date-section").text().replace('\"', '\'');
		content = doc.select("div.article-single__content.category_contents_details > p,div.article-single__content.category_contents_details > h1,div.article-single__content.category_contents_details > h2,div.article-single__content.category_contents_details > h3,div.article-single__content.category_contents_details > h4,div.article-single__content.category_contents_details > h5,div.article-single__content.category_contents_details > h6").text().replace('\"', '\'');
		tagLs = doc.select("div.article-tag-box.text-lg-right > a");
		for(Element t : tagLs) {
			tags += t.text().replace('\"', '\'') + " - ";
		}
		writeOutputFile(url, type, title, content, dateCreated, author, category, summary, tags);
		System.gc();
	}
}
