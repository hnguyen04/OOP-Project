package crawlers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParallelCollectorCryptonews
extends CollectorCryptonews
implements Runnable{
	private int id;
	
	public void webTraverse(int c) {
		count = c * 100;
		int n = 0;
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
			n++;
			System.out.println(count);
			System.gc();
		} while(n<100);
	}

	@Override
	public void run() {
		setWebsiteSource("https://cryptonews.com");
		setFileName("cryptonews" + String.valueOf(id) + ".txt");
		setOutputFile();
		createOutputFile();
		setFw();
		setPageIndex("/news/page/");
		webTraverse(id);
	}
	
	public void setId(int i) {
		this.id = i;
	}
}
