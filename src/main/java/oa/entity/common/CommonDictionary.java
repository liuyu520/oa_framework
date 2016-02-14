package oa.entity.common;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Data Dictionary Entity
 */
@Entity
@Table(name = "t_dictionary")
public class CommonDictionary implements Cloneable, Serializable {

    private static final long serialVersionUID = 0x33d260729eadd26eL;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 组id
     */
    private String groupId;
    /**
     * 键<br />不能取值为key,因为key是关键字
     */
    private String key2;
    /**
     * 值
     */
    private String value;
    /**
     * 描述
     */
    private String description;

    @Id
	@GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    

    public String getKey2() {
		return key2;
	}

	public void setKey2(String key2) {
		this.key2 = key2;
	}

	public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CommonDictionary clone() throws CloneNotSupportedException {
        return (CommonDictionary) super.clone();
    }
}
