/**
 * 
 */
package cn.dev.demo.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 */
public class DateHelper {

	public interface Format{
		String FORMAT_18="yyyyMMddHHmmssSSSS";
		String FORMAT_17="yyyyMMddHHmmssSSS";
		String FORMAT_14="yyyyMMddHHmmss";
		String FORMAT_8="yyyyMMdd";
		
		String SPLIT_FORMAT_10="yyyy-MM-dd";
		String SPLIT_FORMAT_19="yyyy-MM-dd HH:mm:ss";
		String SPLIT_FORMAT_23="yyyy-MM-dd HH:mm:ss.SSS";
	}
	/**
	 * 获取输入日期的day日hour时minute分的日期
	 */
	public static Date getExpextDate(Date date, int day, int hour, int minute) {
		checkArgs("getExpextDate", date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, day);
		calendar.add(Calendar.HOUR_OF_DAY, hour);
		calendar.add(Calendar.MINUTE, minute);
		return calendar.getTime();
	}
	public static String format(Date date, String fmt) {
		checkArgs("format", date,fmt);
		DateFormat df = new SimpleDateFormat(fmt);
		return df.format(date);
	}
	
	public static Date parse(String s, String fmt) {
		checkArgs("parse", s,fmt);
		DateFormat df = new SimpleDateFormat(fmt);
		try {
			return df.parse(s);
		} catch (ParseException e) {
			throw new RuntimeException("str to date error", e);
		}
	}

	private static void checkArgs(String name,Object... args){
		for (int i = 0; i < args.length; i++) {
			if(args[i]==null){
				throw new IllegalArgumentException("DateHelper-"+name+" args illegal");
			}
		}
	}
}
