package yunma.oa.bean.process;

public class Step {
	/***
	 * 序号,流程的顺序
	 */
	private String seq;
	/***
	 * 提交者的角色/级别
	 */
	private String level;
	/***
	 * 关联的java对象
	 */
	private String bean;
	/***
	 * 要执行的java方法
	 */
	private String action;
	/***
	 * 绑定的数据
	 */
	private Object data;
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getBean() {
		return bean;
	}
	public void setBean(String bean) {
		this.bean = bean;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	
}
