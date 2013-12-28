package com.warsong.android.learn;

/**
 * author: zhanqu
 * date: 13-5-5 下午4:42
 */
public class DemoItem {

    // 显示名
    private String name;

    // 跳转到的demo类名
    private String className;
    
    // 全包路径名（包含末尾的类名）
    private String fullPackageName;
    
	public DemoItem(String name, String fullPackageName) {
		this.name = name;
		this.fullPackageName = fullPackageName;
		// 解析获取类名
		int index = fullPackageName.lastIndexOf(".");
		this.className = fullPackageName.substring(index);
	}

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


	public String getFullPackageName() {
		return fullPackageName;
	}

	public void setFullPackageName(String fullPackageName) {
		this.fullPackageName = fullPackageName;
	}

}