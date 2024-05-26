package crawlers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Collector101blockchains
	extends Collector
	implements IGetData
{
	@Override
	public void getData() {
		setWebsiteSource("https://101blockchains.com");
		setFileName("101blockchains1.csv");
		setPageIndex("/blog/page/");
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
			webPage = websiteSource + pageIndex + String.valueOf(count);
			doc = this.request(webPage, true);
			if(doc == null) break;
			urlsList = doc.getElementsByClass("entry-title");
			for(Element link: urlsList) {
				url = link.select("h2.entry-title > a").attr("href");
				if(url.isEmpty() == false) {
					pageTraverse(url);
				}
			}
			count++;
			System.out.println(count);
		} while(true);
	}

	@Override
	public void pageTraverse(String url) {
		Document doc = this.request(url, false);
		if(doc == null) return;
		String author, type = "Blog Post", title, content = "", dateCreated, category, summary = "", tags = ""; 
		
		Elements contents = doc	.getElementsByClass("post-content description ")
								.select("p:not(blockquote > p),h1,h2,h3,h4,h5,h6")
								.not("h2.pro-tip-title");
		
		category = doc.select("li:has(a.blog-category-green)").text().replace('\"', '\'');
		author = doc.select("li:has(a[href ^= https://101blockchains.com/author/])").text().replace('\"', '\'');
		dateCreated = doc.select("li:has(i.fa.fa-calendar)").text().replace("on ", "").replace('\"', '\'');
		title = doc.select("h1.pho-main-heading").text().replace('\"', '\'');
		
		for(Element e : contents) {
			String temp = e.text();
			if(!temp.isEmpty()) {
				content = content + temp;
			}
		}
		content = content.replace('\"', '\'');
		this.writeOutputFile(url, type, title, content, dateCreated, author, category, summary, tags);
		System.out.println("Done");
		System.gc();
	}
}