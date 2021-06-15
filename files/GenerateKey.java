package cn.test.demo.sequtil;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.xml.internal.txw2.IllegalSignatureException;

/**
 * 唯一key
 * 
 * 
 */
public final class GenerateKey {
	/** 自增量偏移位数 */
	private static final int SEQ_BIT_NUM = 12;
	/** 节点偏移位数 */
	private static final int IDC_BIT_NUM = 10;
	/** 时间戳偏移位数 */
	private static final int SUM_BIT_NUM = SEQ_BIT_NUM + IDC_BIT_NUM;
	/** 一经开始使用严禁修改！ */
	private static final long START_TIME = 1535969209223L;
	/** 节点id */
	private int idcBitId;
	/** 最大自增量 */
	private int maxSeqVal;
	/** 自增量 */
	private int seq;
	/***/
	private long lastMil;

	/** 格式 */
	public Date formatSeqValForDate(long v) {
		return new Date((v >> SUM_BIT_NUM) + START_TIME);
	}

	/** 格式 获取偏移值 */
	public long formatSeqValForSeq(long v) {
		return (~(-1 << SUM_BIT_NUM)) & v & (~(-1 << IDC_BIT_NUM));
	}
	/** 获取idc值 */
	public long formatSeqValForIdc() {
		return this.idcBitId;
	}

	/** 格式 */
	public long formatSeqValForGenerateKey(long time, long seq) {
		return ((time - START_TIME) << SUM_BIT_NUM) |(this.idcBitId << SEQ_BIT_NUM)| seq;
	}

	/** 指定节点Id */
	private GenerateKey(int idcBitId) {
		this.idcBitId = idcBitId;
		if (idcBitId < 0 || idcBitId > ~(-1 << IDC_BIT_NUM)) {
			throw new IllegalArgumentException("illegal arg idcBitNum,the max val is" + ((~(-1 << IDC_BIT_NUM)) - 1));
		}
		this.maxSeqVal = ~(-1 << SEQ_BIT_NUM);
		this.lastMil = System.currentTimeMillis() - START_TIME;
	}

	/** 工厂 */
	public static final class FACTORY {
		private FACTORY() {
		}

		private static final int DEFAULT_IDC_BIT_ID = 0;
		/** 默认单例 */
		public static GenerateKey Instance = null;

		/** 创造单例 */
		static {
			if (Instance == null) {
				MAP_FACTORY.REGISTER.put(DEFAULT_IDC_BIT_ID, new GenerateKey(DEFAULT_IDC_BIT_ID));
				Instance = MAP_FACTORY.REGISTER.get(DEFAULT_IDC_BIT_ID);
			}
		}
	}

	/** 工厂 */
	public static final class MAP_FACTORY {
		private MAP_FACTORY() {
		}
		public static final String FAIL_REGISTED="REGISTED";
		public static final String SUC="SUC";
		/** 默认单例 */
		private static final Map<Integer, GenerateKey> REGISTER = new ConcurrentHashMap<Integer, GenerateKey>();

		/** 注册 */
		public static String regist(int idcBitId) {
			if (REGISTER.containsKey(idcBitId)) {
				return FAIL_REGISTED;
			}
			REGISTER.put(idcBitId, new GenerateKey(idcBitId));
			return SUC;
		}

		public static GenerateKey get(int idcBitId) {
			return REGISTER.get(idcBitId);
		}
	}

	/** 唯一id */
	public synchronized long nextId() {
		// 2^40~
		long nowMil = System.currentTimeMillis() - START_TIME;
		if (nowMil < this.lastMil) {
			throw new IllegalSignatureException("nowMil<lastMil illegal state");
		}
		if (nowMil == lastMil) {
			// 自增1,且可以回滚
			this.seq = (this.seq + 1) & this.maxSeqVal;
			if (this.seq == 0) {
				int limit = 0;
				while (nowMil <= this.lastMil) {
					if (++limit > 1 << 5) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
						}
					}
					nowMil = System.currentTimeMillis() - START_TIME;
				}
			}
		} else {
			this.seq = 0;
		}
		this.lastMil = nowMil;

		return (nowMil << SUM_BIT_NUM) | (this.idcBitId << SEQ_BIT_NUM) | (this.seq);
	}

}
