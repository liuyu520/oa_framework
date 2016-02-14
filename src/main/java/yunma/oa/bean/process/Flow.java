package yunma.oa.bean.process;

import java.util.List;

public class Flow {
	/***
	 * 流程名称
	 */
	private String name;
	/***
	 * 必须使用List,不能使用Set,因为是有序的
	 */
	private List<Step>steps;
	private boolean isSeq;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Step> getSteps() {
		return steps;
	}
	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}
	public boolean isSeq() {
		return isSeq;
	}
	public void setSeq(boolean isSeq) {
		this.isSeq = isSeq;
	}
	
	
}
