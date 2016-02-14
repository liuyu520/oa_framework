package oa.bean.statistics;

/***
 * 统计数据
 * @author Administrator
 *
 */
public class StatisticsBean {
	/***
	 * 记录条数
	 */
	private long count;
	/***
	 * 截止日期
	 */
	private String endDay;
//	private String startDay;
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getEndDay() {
		return endDay;
	}
	public void setEndDay(String endDay) {
		this.endDay = endDay;
	}
	
	
}
