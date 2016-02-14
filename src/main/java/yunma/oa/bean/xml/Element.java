package yunma.oa.bean.xml;

import java.util.List;
import java.util.Map;
/***
 * xml 文件的节点
 * @author Administrator
 *
 */
public class Element {
	/***
	 * 如果是节点,则是节点名称,如bean,tr等,<br>如果是文本节点,则就是文本内容
	 */
	private String name;
	/***
	 * 节点的属性,<br />如果是文本节点,则该成员变量为null
	 */
	private Map<String,Object> attributes;
	/***
	 * 是有序的,子节点,<br />如果是文本节点,则该成员变量为null
	 */
	private List<Element> children;
	/***
	 * 是否是文本节点
	 */
	private boolean isTextNode;
	/**
	 * 开始标签,比如<html>
	 */
	private String startTag;
	/***
	 * 结束标签,比如</html>
	 */
	private String endTag;
	/***
	 * 父节点,<br />如果是根节点,则该成员变量为null
	 */
	private Element parent;
	
	public String getName() {
		return name;
	}
	/***
	 * 还会设置startTag,endTag
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
		if(!isTextNode){
			this.startTag="<"+this.name+">";
			this.endTag="</"+this.name+">";
		}
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public List<Element> getChildren() {
		return children;
	}
	public void setChildren(List<Element> children) {
		this.children = children;
	}
	public boolean isTextNode() {
		return isTextNode;
	}
	public void setTextNode(boolean isTextNode) {
		this.isTextNode = isTextNode;
	}
	@Override
	public String toString() {
		return "Element ["+(isTextNode?"value":"name")+"=" + name + ", attributes=" + attributes
				+ ",type:" + (isTextNode?"文本节点":"复杂节点") + "]";
	}
	public String getStartTag() {
		return startTag;
	}
	public void setStartTag(String startTag) {
		this.startTag = startTag;
	}
	public String getEndTag() {
		return endTag;
	}
	public void setEndTag(String endTag) {
		this.endTag = endTag;
	}
	public Element getParent() {
		return parent;
	}
	public void setParent(Element parent) {
		this.parent = parent;
	}
	
	
}
