package cn.dev.demo.utils;

/**
 * 获取当前执行方法的方法名，路径等
 * @date 2018年9月20日
 */
public class CurrentExcuteInfo {

	/**
	 * 调用栈深度为3
	 */
	private static final int originStackIndex = 3;

	/**
	 * 获取调用方法名
	 * 
	 * @return
	 */
	public static String getMethodFullPath() {
		StackTraceElement ste = getStackTrace(originStackIndex);
		return String.format("%s.%s", ste.getClassName(), ste.getMethodName());
	}

	/**
	 * 获取调用方法名 获得调用栈中index层的路径
	 * 
	 * @param index
	 *            -index=0时获得调用getMethodFullPath的method全路径
	 * @return
	 */
	public static String getMethodFullPath(int index) {
		index = originStackIndex + index;
		StackTraceElement ste = getStackTrace(index);
		return String.format("%s.%s", ste.getClassName(), ste.getMethodName());
	}

	/**
	 * 私用
	 * 
	 * @return
	 */
	private static StackTraceElement getStackTrace(int index) {
		return Thread.currentThread().getStackTrace()[index];
	}

}

