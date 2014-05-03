#!/bin/sh

exec /share/vim/vim73/vim "$@"

stickyMenu 是一个滑动时TabBar随ListView进行滑动到一定距离时固定在顶端，并且当tab之间切换时保持TabBar的位置不变的控件demo。
其中的MyListView支持下拉刷新、上滑加载更多（MyListView改进于XListView）。
![image](https://github.com/keshuangjie/stickyMenu/raw/master/screenshots/stickyMenu-screenShots.gif
