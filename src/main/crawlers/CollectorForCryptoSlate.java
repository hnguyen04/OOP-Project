package crawlers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CollectorForCryptoSlate 
	extends Collector
	implements IGetData {
	
	@Override
	public void getData() {
		setWebsiteSource("https://cryptoslate.com");
		setFileName("cryptoslate.csv");
		setOutputFile();
		createOutputFile();
		setFw();
		setPageIndex("/news/page/");
		webTraverse();
		setPageIndex("/press-releases/page/");
		webTraverse();
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
			doc = this.request(webPage, true);
			if(doc == null) break;
			if(pageIndex.equals("/news/page/")) {
				urlList = doc.getElementsByClass("list-feed slate").select("div.list-post");
			}
			else {
				urlList = doc.getElementsByClass("posts clearfix").select("div.list-post");
			}
			if(urlList.isEmpty()) break;
			for(Element link: urlList) {
				url = link.selectFirst("article").select("a").attr("href");
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
		if (doc == null) return;
		Elements contents = doc.getElementsByClass("post-box clearfix");
		if(contents.isEmpty()) return; //not a post
		String author, type, title, content, dateCreated, category = "News", summary = "", tags = "", disclosure = ""; 
		//handle press release
		if(contents.select("article.full-article").isEmpty()) { 
			type = "News Article";
			author = "Chainware";
			title = doc.select("h1.post-title").text().replace('\"', '\'');
			content = doc.getElementsByClass("post-box clearfix").select("article > p,article > h1,article > h2,article > h3,article > h4,article > h5,article > blockquote,article > ul").text().replace('\"', '\'');
			dateCreated = doc.select("span.post-date.col").text().replace('\"', '\'');
			tags = "";
		}
		//handle article
		else { 
			type = "Blog Post";
			author = doc.select("div.author-info").select("a").text().replace('\"', '\'');
			title = doc.select("h1.post-title").text().replace('\"', '\'');
			content = doc.select("article.full-article").select("p,h1,h2,h3,h4,h5,blockquote,ul").text().replace('\"', '\'');
			dateCreated = doc.select("div.post-date").text().replace('\"', '\'');
			tags = doc.select("div.posted-in").text().replace('\"', '\'')
						.replace("Posted In: ", "").replace(", ", "-");
			summary = doc.getElementsByClass("post-subheading").text().replace('\"', '\'');
			disclosure = doc.getElementsByClass("full-article").select("div.top.disclaimer").text().replace('\"', '\'');
			//Handle sponsor post
			if(disclosure.isBlank() == false) {
				author = "Unknown";
				dateCreated = doc.getElementsByClass("post-meta-single sponsored").select("div.text").text().replace('\"', '\'')
									.replace("Published ", "");
				content.replaceFirst(disclosure, "");
			}
		}
		writeOutputFile(url, type, title, content, dateCreated, author, category, summary, tags);
	}
}
