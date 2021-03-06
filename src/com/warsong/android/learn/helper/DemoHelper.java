package com.warsong.android.learn.helper;

import java.util.ArrayList;
import java.util.List;

import com.warsong.android.learn.DemoItem;

public class DemoHelper {

	public static List<DemoItem> getDemoList() {
		List<DemoItem> demos = new ArrayList<DemoItem>();
		demos.add(new DemoItem("layer list", PackageHelper.PACKAGE_NAME + ".ui.LayerListActivity"));
		demos.add(new DemoItem("dynamic inflate", PackageHelper.PACKAGE_NAME
				+ ".ui.DynamicInflateActivity"));
		demos.add(new DemoItem("autofix view", PackageHelper.PACKAGE_NAME
				+ ".ui.AutoFixViewActivity"));
		demos.add(new DemoItem("camera preview", PackageHelper.PACKAGE_NAME
				+ ".ui.CameraPreviewActivity"));
		demos.add(new DemoItem("view lifecycle", PackageHelper.PACKAGE_NAME
				+ ".ui.ViewLifeCycleActivity"));
		demos.add(new DemoItem("pullrefresh", PackageHelper.PACKAGE_NAME
				+ ".ui.PullRefreshActivity"));

		demos.add(new DemoItem("binder test", PackageHelper.PACKAGE_NAME + ".ui.BinderTestActivity"));
		demos.add(new DemoItem("local service", PackageHelper.PACKAGE_NAME + ".ui.LocalServiceActivity"));
		demos.add(new DemoItem("界面生命周期", PackageHelper.PACKAGE_NAME + ".ui.ActLifeCycleActivity"));
		demos.add(new DemoItem("view拦截器", PackageHelper.PACKAGE_NAME + ".ui.ViewInterceptActivity"));
		demos.add(new DemoItem("view自动拦截器", PackageHelper.PACKAGE_NAME
				+ ".ui.ViewAutoInterceptActivity"));

		demos.add(new DemoItem("相机测试", PackageHelper.PACKAGE_NAME + ".ui.CameraTestActivity"));
		demos.add(new DemoItem("shape测试", PackageHelper.PACKAGE_NAME + ".ui.ShapeTestActivity"));
		demos.add(new DemoItem("img延迟加载测试", PackageHelper.PACKAGE_NAME
				+ ".ui.ImageLazyLoadActivity"));
		demos.add(new DemoItem("textview selectable测试", PackageHelper.PACKAGE_NAME
				+ ".ui.TextViewSelectableActivity"));
		demos.add(new DemoItem("view尺寸测试", PackageHelper.PACKAGE_NAME + ".ui.LayoutSizeActivity"));
		demos.add(new DemoItem("内存占用测试", PackageHelper.PACKAGE_NAME + ".ui.MemoryUseActivity"));
		demos.add(new DemoItem("scrollwebview测试", PackageHelper.PACKAGE_NAME + ".ui.ScrollWebViewActivity"));
		demos.add(new DemoItem("dashline测试", PackageHelper.PACKAGE_NAME + ".ui.DashLineActivity"));
		demos.add(new DemoItem("round clip测试", PackageHelper.PACKAGE_NAME + ".ui.RoundClipActivity"));
		demos.add(new DemoItem("LinearTextView测试", PackageHelper.PACKAGE_NAME + ".ui.LinearTextViewActivity"));
		demos.add(new DemoItem("scroll viewpager测试", PackageHelper.PACKAGE_NAME + ".ui.ScrollViewPagerActivity"));
		demos.add(new DemoItem("window测试", PackageHelper.PACKAGE_NAME + ".ui.WindowTestActivity"));
		demos.add(new DemoItem("tab scroll webview测试", PackageHelper.PACKAGE_NAME + ".ui.TabScrollWebViewActivity"));
		return demos;
	}
}
