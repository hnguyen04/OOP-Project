package crawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CollectorBlockchainMagazine 
	extends Collector
	implements IGetData {

	@Override
	public void getData() {
		setWebsiteSource("https://blockchainmagazine.net");
		setFileName("blockchainmagazine.txt");
		setOutputFile();
		createOutputFile();
		setFw();
		Document doc = request(websiteSource, true);
		Elements subWebs = doc.select("ul.sub-menu > li");
		for(Element subWeb : subWebs) {
			setPageIndex(subWeb.select("a").attr("href").replace("https://blockchainmagazine.net", "") + "page/");
			System.out.println(pageIndex);
			webTraverse();
		}
	}

	@Override
	public void webTraverse() {
		count = 0;
		String webPage = new String(""),
				url = new String ("");
		Document doc;
		Elements urlList;
		do {
			webPage = websiteSource + pageIndex + String.valueOf(count);
			System.out.println(webPage);
			doc = this.request(webPage, true);
			if(doc == null) break;
			urlList = doc.select("div.stm_news_grid__image");
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
		} while(count != 30);
	}

	@Override
	public void pageTraverse(String url) {
		System.out.println(url);
		Document doc = this.request(url, false);
		if(doc == null) return;
		doc = Jsoup.parse(doc.getElementById("main").toString());
		String author, type = "Blog Post", title, content = "", dateCreated, category, summary = "", tags = ""; 
		
		content = doc.select("div.stm_post_view__content").text().replace('\"', '\'');
		category = doc.select("div.stm_post_view_categories").text().replace('\"', '\'');
		dateCreated = doc.select("div.date > i").text()
					.replace(" by ", "").replace('\"', '\'');
		author = doc.select("div.date > strong").text().replace('\"', '\'');
		title = doc.select("h1.stm_post_view__title").text().replace('\"', '\'');

		this.writeOutputFile(url, type, title, content, dateCreated, author, category, summary, tags);
		System.out.println("Done");
		System.gc();
	}
}
