package fsam.start_up;

import fsam.gui.MainWindows;

/*需求：图形界面的文件分割、合并程序。
   	细节：
   	1.点击按钮，在文件选择界面选择一个文件
   	2.选择一个目录进行分割文件的保存，默认在被分割文件目录下创建一个分文件保存目录
   	3.选择一个分文件大小，具有默认值1m
   	4.点击分割进行分割，同时生成配置文件记录信息
   	1.点击按钮，在文件选择界面选择一个分文件目录
   	2.点击按钮选择生成的目录，可输入合并的文件名，默认在分文件目录内生成，
   	3.点击合并按钮，进行合并*/

public class StartUp {
	public static void main(String[] args) {
		new MainWindows("分割合并文件");
	}
}
