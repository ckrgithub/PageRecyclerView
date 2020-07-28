>### GitAds
>[![GitAds](https://images.gitads.io/codelf)](https://tracking.gitads.io/?repo=codelf)
  
  <img src="https://user-images.githubusercontent.com/799578/50462941-8075fe80-09c3-11e9-89e7-af0cb7991406.png" width="80">
  
# PageRecyclerView
PageRecyclerView achieves page turning function and unlimited carousel [中文文档](README-ZH.md)。

## Effect
| carousel     | horizontal-grid-paging    | add-data    |
| ------------ | ------------------------- | ----------- |
| ![](screenRecorder/Screenshot_1.gif) | ![](screenRecorder/Screenshot_2.gif) | ![](screenRecorder/Screenshot_3.gif)

| remove-data  | vertical-linear-paging |
| ------------ | ------------------------- |
| ![](screenRecorder/Screenshot_4.gif) | ![](screenRecorder/Screenshot_5.gif) |

## Demo
[Download APK](apk/app-debug.apk)

## Dependencies
### add dependencies：
```
	dependencies {
		implementation 'ckrjfrog.Page:PageView:1.2.12'//gradle plugin 3.0(inclusive) above used
		//compile 'ckrjfrog.Page:PageView:1.2.12'//gradle plugin 3.0 below used
	}
```

## Function And Use
### 1.layout reference
```
    <com.ckr.pageview.view.PageView
        android:id="@+id/pageView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        app:autoplay="true"
        app:loop="true"
	app:autosize="false"
        app:loop_interval="3000"
        app:hide_indicator="false"
        app:indicator_container_background="#fff"
        app:indicator_container_height="@dimen/viewpager_indicator_container_height"
        app:indicator_group_alignment="center"
        app:indicator_margin="@dimen/viewpager_indicator_margin"
        app:layout_flag="linear"
        app:orientation="horizontal"
        app:overlap_layout="false"
        app:clipToPadding="false"
        app:pagePadding="0dp"
        app:page_background="#fff"
        app:page_column="one"
        app:page_row="one"
        app:selected_indicator_diameter="@dimen/viewpager_selected_indicator_diameter"
        app:selected_indicator_drawable="@drawable/shape_point_selected"
        app:unselected_indicator_diameter="@dimen/viewpager_unselected_indicator_diameter"
        app:unselected_indicator_drawable="@drawable/shape_point_unselected"/>
```
### 2.attributes description
| attributes         | description                   | type              | defaults         |
| ------------------ | ----------------------------- | ----------------- | ---------------- |
| loop               | start a carousel(valid when there is only one item per page)  | boolean    | false |
| loop_interval      | loop interval	     | int		   | 3000		 |
| autoplay           | automatically scroll to the next page	   | boolean      | false   |
| autosize           | automatically adjust the width or height of item(valid in the grid layout) | boolean | false |
| hide_indicator     | hide indicator  	     | boolean      | false	    |
| indicator_container_background| the background of indicator parent container 	 | drawable     | null		|
| indicator_container_height    | indicator parent container height(valid in the vertical orientation) 	  | int       | 90   |
| indicator_contianer_width     | indicator parent container width(valid in the horizontal orientation)   | int       | 90   |
| indicator_group_alignment     | the alignment of indicator group(eg: left,top,center)  | int        | 0x11(center)   |
| indicator_group_marginLeft    | the marginLeft of indicator group  	 	 | int          | 0 		        |
| indicator_group_marginTop     | the marginTop of indicator group  	 	 | int          | 0 		        |
| indicator_group_marginRight   | the marginRight of indicator group  	 	 | int          | 0 		        |
| indicator_group_marginBottom  | the marginBottom of indicator group  	 	 | int          | 0 		        |
| indicator_margin		| indicator spacing  	         | boolean      | false             |
| clipToPadding			| the clipTopadding of recyclerView  		 | int      	| 15     |
| pagePadding			| recyclerView.setPadding(pagePadding,0,pagePading,0)  	| int      	| 15           |
| layout_flag			| mark linear layout or grid layout                     | int          | 0(linear layout)  |
| orientation			| layout orientation  					| int          | 0(horizontal)     |
| overlap_layout		| Whether the indicator overlaps the page 		| boolean      | false     		|
| page_column			| the number of columns per page  			| int          | 1		            |
| page_row			| the number of rows per page  				| int          | 1		            |
| selected_indicator_color      | indicator color for the current page  		| int          | Color.RED         |
| selected_indicator_diameter   | indicator diameter for the current page  		| int          | 15                |
| selected_indicator_drawable   | indicator drawable for the current page(priority is higher than color)     | drawable    | null  |
| unselected_indicator_color	| indicator color for the non-current page  		| int          | Color.BLACK       |
| unselected_indicator_diameter	| indicator diameter for the non-current page  		| int          | 15 		        |
| selected_indicator_drawable   | indicator drawable for the non-current page(priority is higher than color) | drawable  | null |
| page_background		| the background of the current page			| drawable		| null		|
| enable_touch_scroll           | Whether to allow touch scrolling                      | boolean       | true  |
| max_scroll_duration           | Maximum scrolling time of the page                    | int           | 600   |
| min_scroll_duration           | Minimum scrolling time of the page                    | int           | 0     |
| sub_loop_interval             | When the index is adjusted, loop interval             | int           | 100   |

### 3.code to use
```
    mainAdapter = new MainAdapter(getContext(), itemLayoutId);//MainAdapter extend BasePageAdapter<T,ViewHolder>
    pageView.setAdapter(mainAdapter);
    pageView.updateAll(items);
```
## Thanks
[banner](https://github.com/youth5201314/banner)

## My Other Project
[CollapsingRefresh](https://github.com/ckrgithub/CollapsingRefresh)

[FlexItemDecoration](https://github.com/ckrgithub/FlexItemDecoration)

## Version Tracking
* **1.2.0-release**
  * add: the settings of autoplay
  * add: the animation of scrolling to the page

* **1.1.1-release**
  * repair: When fast sliding,the indicators don't update in time
  * repair: the method of setCurrentItem(1,false) doesn't work
  * add: clipToPadding and pagePadding Settings
 
* **1.1.0-release**
  * add: the indicator container alignment, margin Settings, background Settings, and overlap layout styles
  * other: optimize the code
 
* **1.0.9-release**
  * repair: When the carousel is broadcast, the data source changes to cause confusion in the indicator point.

## Community
Contact Me: 862950533  
Or scan the QR code below:  
![](screenRecorder/qq.png)

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
