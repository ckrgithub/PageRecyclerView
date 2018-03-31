# PageRecyclerView
PageRecyclerView实现翻页功能及无限轮播。

## 效果演示
| 无限轮播     | 水平网格分页    | 添加数据    |
| ------------ | ------------------------- | ----------- |
| ![](screenRecorder/Screenshot_1.gif) | ![](screenRecorder/Screenshot_2.gif) | ![](screenRecorder/Screenshot_3.gif) |

| 移除数据  | 竖直线性分页 |
| ------------ | ------------------------- |
| ![](screenRecorder/Screenshot_4.gif) | ![](screenRecorder/Screenshot_5.gif) |

## Demo
[下载 APK](apk/app-debug.apk)

## 依赖
### 添加依赖：
```
	dependencies {
		implementation 'ckrjfrog.Page:PageView:1.1.0'//gradle plugin 3.0(包含)以上使用
		//compile 'ckrjfrog.Page:PageView:1.1.0'//gradle plugin 3.0一下使用
	}
```

## 功能及使用
### 1.布局引用
```
     <com.ckr.pageview.view.PageView
        android:id="@+id/pageView"
        android:layout_width="match_parent"
        android:layout_height="180dp"
        app:hide_indicator="false"
        app:indicator_container_background="#40000000"
        app:indicator_container_height="@dimen/viewpager_indicator_container_height"
        app:indicator_group_alignment="right|centerVertical"
        app:indicator_group_marginBottom="10dp"
        app:indicator_group_marginRight="10dp"
        app:indicator_margin="@dimen/viewpager_indicator_margin"
        app:layout_flag="grid"
        app:orientation="horizontal"
        app:overlap_layout="true"
        app:page_column="4"
        app:page_row="2"
        app:selected_indicator_color="@color/viewpager_selected_indicator_color"
        app:selected_indicator_diameter="@dimen/viewpager_selected_indicator_diameter"
        app:selected_indicator_drawable="@drawable/shape_point_selected"
        app:unselected_indicator_color="@color/viewpager_unselected_indicator_color"
        app:unselected_indicator_diameter="@dimen/viewpager_unselected_indicator_diameter"
        app:unselected_indicator_drawable="@drawable/shape_point_unselected"/>
```
### 2.属性说明
| 属性							| 描述									 | 类型			| 默认值			|
| ----------------------------- | -------------------------------------- | ----------   | ------------- |
| endless_loop                  | 是否启动无限轮播(当每页只有一个item时有效)	 | boolean  	| false			|
| loop_interval                 | 轮询时间间隔							 | int			| 3000			|
| hide_indicator                | 是否隐藏指示器  						 | boolean 		| false		 	|
| indicator_container_background| 指示器父容器的背景 	 	 			 	 | drawable     | null		    |
| indicator_contianer_heigt     | 指示器父容器的高度 					 	 | int     		| 90			|
| indicator_contianer_width     | 指示器父容器的高度 					 	 | int     		| 90			|
| indicator_group_alignment     | 指示器组的对齐方式  						 | int          | 0x11(center)  |
| indicator_group_marginLeft    | 指示器组的marginLeft  	 	 			 | int          | 0 		    |
| indicator_group_marginTop     | 指示器组的marginTop 	 	 			 | int          | 0 		    |
| indicator_group_marginRight   | 指示器组的marginRight  	 	 			 | int          | 0 		    |
| indicator_group_marginBottom  | 指示器组的marginBottom  	 	 		 | int          | 0 		    |
| indicator_margin				| 指示器间的间距  						 | int      	| 15            |
| layout_flag					| 标记线性布局或网格布局  					 | int      		| 0(线性)     	|
| orientation					| 布局方向  								 | int      	| 0(horizontal) |
| overlap_layout				| 指示器容器是否遮住PageRecyclerView		 | boolean      | false     	|
| page_column					| 每页的列数  							 | int       	| 1		    	|
| page_row						| 每页的行数  							 | int       	| 1		    	|
| selected_indicator_color      | 当前页的指示器颜色  						 | int       	| Color.RED	 	|
| selected_indicator_diameter   | 当前页的指示器直径  						 | int       	| 15		 	|
| selected_indicator_drawable   | 当前页的指示器drawable(优先级高于颜色) 	 | drawable  	| null	     	|
| unselected_indicator_color	| 非当前页的指示器颜色  					 | int      	| Color.BLACK   |
| unselected_indicator_diameter	| 非当前页的指示器直径  					 | int      	| 15 		 	|
| selected_indicator_drawable   | 非当前页的指示器drawable(优先级高于颜色) 	 | drawable 	| null	     	|
| page_background				| 当前页的背景							 | drawable		| null			|

### 3.代码使用
```
    mainAdapter = new MainAdapter(getContext(), itemLayoutId);//该MainAdapter需继承BasePageAdapter<T,ViewHolder>
    pageView.setAdapter(mainAdapter);//设置adapter
    pageView.updateAll(items);//更新数据
```
## 版本更新
* **1.1.0-release**
  * 添加：指示器容器的对齐方式、margin设置、背景设置和overlap布局样式
  * 其他：优化代码

* **1.0.9-release**
  * 修复：轮播时，数据源发生变化引起指示点错乱的问题

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
