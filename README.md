# PageRecyclerView
recyclerView实现翻页功能。相对于Viewpager，recyclerView数据操作更灵活,还可以添加指示器。

## 效果演示
![](screenRecorder/Screenshot_1.gif)  ![](screenRecorder/Screenshot_2.gif)  ![](screenRecorder/Screenshot_3.gif)

## Demo
[下载 APK](apk/app-debug.apk)

## 依赖
#### 添加依赖：
```
	dependencies {
		implementation 'ckrjfrog.Page:PageView:1.0.1'//gradle plugin 3.0(包含)以上使用
		//compile 'ckrjfrog.Page:PageView:1.0.1'//gradle plugin 3.0一下使用
	}
```

## 功能及使用
#### 1.布局引用
```
     <com.ckr.pagesnaphelper.widget.PageView
        android:id="@+id/pageView"
        app:selected_indicator_color="@color/viewpager_selected_indicator_color"
        app:unselected_indicator_color="@color/viewpager_unselected_indicator_color"
        app:selected_indicator_diameter="@dimen/viewpager_selected_indicator_diameter"
        app:unselected_indicator_diameter="@dimen/viewpager_unselected_indicator_diameter"
        app:indicator_margin="@dimen/viewpager_indicator_margin"
        app:hide_indicator="false"
        app:selected_indicator_drawable="@drawable/shape_point_selected"
        app:unselected_indicator_drawable="@drawable/shape_point_unselected"
        app:indicator_group_height="@dimen/viewpager_indicator_group_height"
        app:orientation="horizontal"
        app:page_row="two"
        app:page_column="four"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
    //其中，selected_indicator_color：指示器颜色
    //indicator_margin：指示器圆点间的间距
    //hide_indicator：是否显示指示器
    //selected_indicator_drawable：指示器drawble(注：优先级高于颜色)
    //indicator_group_height：指示器的父布局的高度
    //orientation：分页控件的布局方向
    //page_row：每页的行数
    //page_column：每页的列数
```
#### 2.代码使用
```
    pageView.addOnPageChangeListener(this);//设置分页监听器
    mainAdapter = new MainAdapter(getContext(), itemLayoutId);//该MainAdapter需继承BasePageAdapter<T,ViewHolder>
    pageView.setAdapter(mainAdapter);//设置adapter
    pageView.updateAll(items);//更新数据
```


License
-------

    Copyright 2018 ckrgithub

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
