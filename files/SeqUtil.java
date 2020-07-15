package cn.test.demo.sequtil;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
/**
 * 单线程下唯一
 *
 */
public final class SeqUtil {

	private static final class Genkey{
		private static long lastTime=0L;
		private static int seq = 0;
		
		/**偏移位数*/
		private static final int SEQ_BITNUM=4;
		/**自循环变量最大值*/
		private static final int SEQ_MAX_VALUE=~(-1<<SEQ_BITNUM);
		
		private final static int IDC_BITNUM=3;
		
		private final static int IDC_ID=3;
		
		private static final int SUM_BIT_NUM=IDC_BITNUM+SEQ_BITNUM;;

		/**
		 * 单机下相对唯一
		 * 
		 * @return
		 */
		public synchronized static final long getSeq() {
			long nowTime = System.currentTimeMillis();
			if(nowTime<=lastTime){
				seq=(seq+1) & SEQ_MAX_VALUE;
				if(seq==0L){
					while(nowTime<=lastTime){
						nowTime=System.currentTimeMillis();
					}
				}
			}else{
				seq=0;
			}
			lastTime=nowTime;
			return (nowTime<<SUM_BIT_NUM)|seq|(IDC_ID<<SEQ_BITNUM);
		}
	}
	
	public static final long nextId(){
		return Genkey.getSeq();
	}
	
	/**
	 * 20000并发测试重复
	 * @throws InterruptedException
	 */
	@Test
	public void test() throws InterruptedException{
		final Set<Long> set=new ConcurrentSkipListSet<Long>();
		Runnable r=new Runnable() {
			
			public void run() {
				long l=SeqUtil.nextId();
				if(set.contains(l)){
					System.out.println(l);
					System.out.println(set.contains(l));
				}else{
					set.add(l);
				}
			}
		};
		ExecutorService es=Executors.newFixedThreadPool(1000);
		for (int i = 0; i < 20000; i++) {
			es.execute(r);
		}
		es.shutdown();
		if(es.awaitTermination(5, TimeUnit.SECONDS)){
			System.out.println("集合大小="+set.size());
		}
	}
	@Test
	public void test02(){
		System.out.println(SeqUtil.nextId());
	}
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println(SeqUtil.nextId());
		}
	}
}
