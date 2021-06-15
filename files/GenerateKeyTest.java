/**
 * 
 */
package cn.test.demo.sequtil;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import cn.dev.demo.utils.DateHelper;


/**
 * @author kf1600-thf
 *
 */
public class GenerateKeyTest {
	
	@Test
	public void format() {
		
		long seq = GenerateKey.FACTORY.Instance.nextId();
		//当前时间
		System.out.println(DateHelper.format(new Date(), DateHelper.Format.SPLIT_FORMAT_23));
		//唯一键
		System.out.println(seq);
		//唯一键中的时间值
		Date seqDate=GenerateKey.FACTORY.Instance.formatSeqValForDate(seq);
		//唯一键的自循环值
		long val=GenerateKey.FACTORY.Instance.formatSeqValForSeq(seq);
		System.out.println(DateHelper.format(seqDate, DateHelper.Format.SPLIT_FORMAT_23));
		System.out.println(val);
		//还原出来的唯一键值
		System.out.println(GenerateKey.FACTORY.Instance.formatSeqValForGenerateKey(seqDate.getTime(), val));
		// 格式化
		System.out.println(DateHelper.format(seqDate, DateHelper.Format.FORMAT_18)+String.valueOf(val));
	}
	private static String formatSeq(long seq){
		//唯一键中的时间值
		Date seqDate=GenerateKey.FACTORY.Instance.formatSeqValForDate(seq);
		//唯一键的自循环值
		long val=GenerateKey.FACTORY.Instance.formatSeqValForSeq(seq);
		// 格式化
		return DateHelper.format(seqDate, DateHelper.Format.FORMAT_18)+String.valueOf(val);
	}
	private static String formatSeq2(int idcId,long seq){
		GenerateKey instance=GenerateKey.MAP_FACTORY.get(idcId);
		//唯一键中的时间值
		Date seqDate=instance.formatSeqValForDate(seq);
		//唯一键的自循环值
		long val=instance.formatSeqValForSeq(seq);
		long idc=instance.formatSeqValForIdc();
		// 格式化
		return DateHelper.format(seqDate, DateHelper.Format.SPLIT_FORMAT_23)+","+idc+","+String.valueOf(val);
	}
	private static String formatSeq2(long seq){
		return formatSeq2(0, seq);
	}

	@Test
	public void millog2Test(){
		Calendar c=Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.YEAR, 100);
		System.out.println(Math.log(System.currentTimeMillis())/Math.log(2));
		System.out.println(System.currentTimeMillis());
		System.out.println(Math.log(c.getTimeInMillis())/Math.log(2));
		System.out.println(c.getTimeInMillis());
	}
	
	@Test
	public void seqTest(){
		for(int i=0;i<12;i++){
			System.out.println(GenerateKey.FACTORY.Instance.nextId());
		}
	}
	@Test
	public void seqTest2(){
		for(int i=0;i<12;i++){
			System.out.println(formatSeq2(GenerateKey.FACTORY.Instance.nextId()));
		}
	}
	@Test
	public void seqTest3(){
		int idcId=1022;
		int idcId2=888;
		GenerateKey.MAP_FACTORY.regist(idcId2);
		if(GenerateKey.MAP_FACTORY.SUC.equals(GenerateKey.MAP_FACTORY.regist(idcId))){
			for(int i=0;i<200;i++){
				System.out.println(formatSeq2(idcId,GenerateKey.MAP_FACTORY.get(idcId).nextId()));
				System.out.println(formatSeq2(idcId2,GenerateKey.MAP_FACTORY.get(idcId2).nextId()));
			}
		}
	}
	@Test
	public void amazeSyncTest(){
		syncDone();
		System.out.println(map.get("test"));
	}
	private static final Map<String, Object> map=new ConcurrentHashMap<String, Object>();
	private static synchronized void syncDone(){
		map.put("test", "test-value");
		System.out.println(map.put("123", "34432243"));
	}
	@Test
	public void trimTest(){
		System.out.println(",1322一一渔船，1just a moment,3,".replaceAll(",?$", ""));
	}
	@Test
	public void replaceTest(){
		System.out.println("234432234234432".replaceAll("(234432{1}$)", "$1REP$1"));
	}
	@Test
	public void seqUniqTest() throws InterruptedException{
		final Set<Long> set=new ConcurrentSkipListSet<Long>();
		Runnable r=new Runnable() {
			
			public void run() {
				long l=GenerateKey.FACTORY.Instance.nextId();
					set.add(l);
			}
		};
		ExecutorService es=Executors.newFixedThreadPool(4);
		for (int i = 0; i < 300; i++) {
			es.execute(r);
		}
		es.shutdown();
		System.out.println(es.awaitTermination(1, TimeUnit.SECONDS)+",集合大小="+set.size());
		while(!es.isTerminated()){}
		System.out.println("集合大小="+set.size());
		for(Long seq:set){
			System.out.println(formatSeq2(seq));
		}
	}
	
	/*@Test
	public void seqUniq2Test() throws InterruptedException{
		final Set<Long> set=new ConcurrentSkipListSet<Long>();
		final GenerateKey k1=new GenerateKey(1);
		final GenerateKey k2=new GenerateKey(2);
		Runnable r=new Runnable() {
			
			public void run() {
				long l=Thread.currentThread().getId()%2==0?k1.nextId():k2.nextId();
				if(set.contains(l)){
					System.out.println(l);
					System.out.println(set.contains(l));
				}else{
					set.add(l);
				}
			}
		};
		ExecutorService es=Executors.newFixedThreadPool(2);
		for (int i = 0; i < 40000; i++) {
			es.execute(r);
		}
		es.shutdown();
		System.out.println(es.awaitTermination(1, TimeUnit.SECONDS)+",集合大小="+set.size());
		while(!es.isTerminated()){}
		System.out.println("集合大小="+set.size());
	}*/
	@Test
	public void seqUniq3Test() throws InterruptedException{
		final Set<Long> set=new ConcurrentSkipListSet<Long>();
		GenerateKey.MAP_FACTORY.regist(1);
		GenerateKey.MAP_FACTORY.regist(2);
		GenerateKey.MAP_FACTORY.regist(0);
		Runnable r=new Runnable() {
			
			public void run() {
				long l=GenerateKey.MAP_FACTORY.get(new Random().nextInt(3)).nextId();
				if(set.contains(l)){
					System.out.println(l);
					System.out.println(set.contains(l));
				}else{
					set.add(l);
				}
			}
		};
		ExecutorService es=Executors.newFixedThreadPool(2);
		for (int i = 0; i < 4000000; i++) {
			es.execute(r);
		}
		es.shutdown();
		System.out.println(es.awaitTermination(1000, TimeUnit.SECONDS)+",集合大小="+set.size());
		while(!es.isTerminated()){}
		System.out.println("集合大小="+set.size());
	}
	
	@Test
	public void seqSpeedTest() throws InterruptedException{
		int counts=4000000;
		long mil=System.currentTimeMillis();
		
		Runnable r=new Runnable() {
			
			public void run() {
				//GenerateKey.FACTORY.Instance.nextId();
				for(int i=0;i<5;i++){
					long m=System.currentTimeMillis()+RandomUtils.nextLong()*1000000;
				}
			}
		};
		ExecutorService es=Executors.newFixedThreadPool(4);
		for (int i = 0; i < counts; i++) {
			es.execute(r);
		}
		es.shutdown();//
		System.out.println(es.awaitTermination(1, TimeUnit.SECONDS));
		while(!es.isTerminated()){}
		System.out.println("finish! 耗时："+(System.currentTimeMillis()-mil)+"ms,tps="
		+counts/(System.currentTimeMillis()-mil)*1000);
	}
	@Test
	public void seqSpeedTest2() throws InterruptedException{
		int counts=4000000;
		long mil=System.currentTimeMillis();
		for (int i = 0; i < counts; i++) {
			GenerateKey.FACTORY.Instance.nextId();
		}
		System.out.println("finish! 耗时："+(System.currentTimeMillis()-mil)+"ms,tps="
		+counts/(System.currentTimeMillis()-mil)*1000);
	}
	
	@Test
	public void leftpadTest(){
		String s=StringUtils.leftPad("123456", 3, '0');
		System.out.println(s);
		s=StringUtils.leftPad("123456", 13, '0');
		System.out.println(s);
		s=StringUtils.leftPad("123456", 17, "+_+");
		System.out.println(s);
		long i=0;
		long lastMil=System.currentTimeMillis();
		while(System.currentTimeMillis()-lastMil<1000){
			i++;
		}
		System.out.println(i);
	}
}
