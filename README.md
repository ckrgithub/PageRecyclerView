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
     <com.ckr.pageview.view.PageView
        android:id="@+id/pageView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        app:hide_indicator="false"
        app:indicator_group_height="@dimen/viewpager_indicator_group_height"
        app:indicator_margin="@dimen/viewpager_indicator_margin"
        app:layout_flag="grid"
        app:orientation="horizontal"
        app:page_column="two"
        app:page_row="two"
        app:selected_indicator_color="#ec04f9"
        app:selected_indicator_diameter="5.8dp"
        app:unselected_indicator_color="#666"
        app:unselected_indicator_diameter="@dimen/viewpager_unselected_indicator_diameter"/>
    //其中，selected_indicator_color：指示器颜色
    //indicator_margin：指示器圆点间的间距
    //hide_indicator：是否显示指示器
    //layout_flag:标记线性或网格布局
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
