package yunma.oa.bean;

public class Person {
	/***
	 * 唯一标识,可能是数据库的ID
	 */
	private String code;
	
	/***
	 * 级别
	 */
	private int level;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	
}
