package crawlers;

public class Collect {
	public static void main(String[] args) {
		Collector101blockchains c0 = new Collector101blockchains();
		c0.getData();
		CollectorTheblock c1 = new CollectorTheblock();
		c1.getData();
		CollectorForCryptoSlate c2 = new CollectorForCryptoSlate();
		c2.getData();
		CollectorBlockonomi c3 = new CollectorBlockonomi();
		c3.getData();
		CollectorCryptonews c4 = new CollectorCryptonews();
		c4.getData();
		CollectorBlockchainMagazine c5 = new CollectorBlockchainMagazine();
		c5.getData();
		Thread t[] = new Thread[20];
		ParallelCollectorCryptonews[] c6= new ParallelCollectorCryptonews[20];
		for(int i = 2; i<20;++i) {
			c6[i] = new ParallelCollectorCryptonews();
			c6[i].setId(i);
			t[i] = new Thread(c6[i]);
			t[i].start();
		}
		for(int i = 2; i < 20 ; ++i) {
			try {
				t[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		CollectorBlockchainMagazine c7 = new CollectorBlockchainMagazine();
		c7.getData();
		CollectorNordFX c8 = new CollectorNordFX();
		c8.getData();
	}
}
