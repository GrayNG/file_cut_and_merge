package fsam.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 * @author Administrator
 *
 */
public class MainWindows extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	private static final String PARTS_SUFIX = "_parts";
	private static final int K = 1024;// 1MB缓冲区

	public MainWindows() {
		this("");
	}

	public MainWindows(String title) {
		super(title);
		init();
	}

	/**
	 * 初始化窗口，调用了初始化面板函数来初始化包含的内板
	 */
	private void init() {
		// 初始化窗口属性
		setSize(525, 455);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);

		// 初始化分割面板
		initSplitPanel();

		// 初始化合并面板
		initMergePanel();

		// 显示窗口
		setVisible(true);
	}

	/**
	 * 初始化合并面板
	 */
	private void initMergePanel() {
		// 初始化面板属性
		JPanel mergePanel = new JPanel();
		mergePanel.setLayout(null);
		mergePanel.setBounds(2, 205, 500, 200);
		mergePanel.setBorder(new LineBorder(Color.BLACK, 1, true));

		// 初始化组件
		JLabel splitLabel = new JLabel("分割目录:");
		mergePanel.add(splitLabel);
		splitLabel.setBounds(5, 15, 60, 30);
		JTextField splitPath = new JTextField();
		mergePanel.add(splitPath);
		splitPath.setBounds(5, 40, 400, 30);
		JButton fileB = new JButton("选择");
		mergePanel.add(fileB);
		fileB.setBounds(410, 40, 80, 30);
		fileB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String path = selectPath();
				if (path != null)
					splitPath.setText(path);

			}
		});

		JLabel saveLabel = new JLabel("保存目录:");
		saveLabel.setToolTipText("为空则存放在分割文件目录内,可自定义文件名");
		mergePanel.add(saveLabel);
		saveLabel.setBounds(5, 75, 60, 30);
		JTextField savePath = new JTextField();
		mergePanel.add(savePath);
		savePath.setBounds(5, 100, 400, 30);
		JButton pathB = new JButton("选择");
		mergePanel.add(pathB);
		pathB.setBounds(410, 100, 80, 30);
		pathB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String path = selectPath();
				if (path != null)
					savePath.setText(path);

			}
		});

		JButton splitButton = new JButton("合并");
		splitButton.setBounds(230, 140, 80, 30);
		mergePanel.add(splitButton);
		splitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mergeFile(splitPath.getText(),savePath.getText());
				
			}
		});

		// 添加面板
		add(mergePanel);
	}

	protected void mergeFile(String splitPath, String savePath) {
		File splitFile= new File(splitPath);
		File saveFile = new File(savePath);
		String fileName = null;
		boolean flag = false;
		if(splitPath.equals("")) {
			flag=true;
		}else if(!splitFile.exists()) {
			flag=true;
		}
		else if(splitFile.isFile()) {
			flag=true;
		}
		if(flag) {
			showMessage("没有分割文件的目录");
			return;
		}
		if(savePath.equals("")) {
			saveFile = splitFile;
		}else if(!saveFile.exists()){
			showMessage("没有保存文件的目录");
			return;
		}else if(saveFile.isFile()){
			fileName=saveFile.getName();
			saveFile = new File(saveFile.getParent());
		}
		ArrayList<FileInputStream> al = new ArrayList<FileInputStream>();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		Properties p = new Properties();
		try {
			FileInputStream fis =new FileInputStream(splitPath+FILE_SEPARATOR+"propertys");
			p.load(fis);
			int times = Integer.parseInt(p.getProperty("num"));
			String storeName = p.getProperty("name");
			fis.close();
			String partsName = storeName.substring(0, storeName.lastIndexOf("."));
			if(fileName==null)
				fileName = storeName;
			for(int i=0;i<times;i++) {
				al.add(new FileInputStream(new File(splitFile,partsName+PARTS_SUFIX+i)));
			}
			Enumeration<FileInputStream> e =Collections.enumeration(al);
			SequenceInputStream sis = new SequenceInputStream(e);
			bis = new BufferedInputStream(sis);
			bos = new BufferedOutputStream(new FileOutputStream(new File(saveFile,fileName)));
			int ch;
			while((ch=bis.read())!=-1) {
				bos.write(ch);
			}
			showMessage("合并成功");
			Desktop.getDesktop().open(saveFile);
			
		} catch (FileNotFoundException e) {		
			showMessage("找不到文件");
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			
			try {
				bos.close();
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
	}

	/**
	 * 初始化分割面板
	 */
	private void initSplitPanel() {
		// 初始化面板属性
		JPanel splitPanel = new JPanel();
		splitPanel.setLayout(null);
		splitPanel.setBorder(new LineBorder(Color.BLACK, 1, true));
		splitPanel.setBounds(2, 5, 500, 200);
		// 初始化组件
		JLabel splitLabel = new JLabel("分割文件:");
		splitPanel.add(splitLabel);
		splitLabel.setBounds(5, 10, 60, 30);
		JTextField splitPath = new JTextField();
		splitPanel.add(splitPath);
		splitPath.setBounds(5, 35, 400, 30);
		JButton fileB = new JButton("选择");
		splitPanel.add(fileB);
		fileB.setBounds(410, 35, 80, 30);
		fileB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String path = selectPath();
				if (path != null)
					splitPath.setText(path);

			}

		});

		JLabel saveLabel = new JLabel("保存目录:");
		saveLabel.setToolTipText("为空则存放在分割文件所在目录下_parts目录内");
		splitPanel.add(saveLabel);
		saveLabel.setBounds(5, 70, 60, 30);
		JTextField savePath = new JTextField();
		splitPanel.add(savePath);
		savePath.setBounds(5, 95, 400, 30);
		JButton pathB = new JButton("选择");
		splitPanel.add(pathB);
		pathB.setBounds(410, 95, 80, 30);
		pathB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String path = selectPath();
				if (path != null)
					savePath.setText(path);

			}
		});

		JLabel sizeLabel = new JLabel("分文件大小:");
		splitPanel.add(sizeLabel);
		sizeLabel.setBounds(105, 135, 90, 30);
		JTextField sizeField = new JTextField("1024");
		splitPanel.add(sizeField);
		sizeField.setBounds(190, 135, 50, 30);
		JLabel sizeLabel2 = new JLabel("KB");
		sizeLabel2.setBounds(240, 135, 30, 30);
		splitPanel.add(sizeLabel2);

		JButton splitButton = new JButton("分割");
		splitButton.setBounds(280, 135, 80, 30);
		splitPanel.add(splitButton);
		splitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				splitFiles(splitPath.getText(), savePath.getText(), sizeField.getText());

			}
		});
		// 添加面板
		add(splitPanel);

	}

	private void splitFiles(String splitPath, String savePath, String size) {
		File splitFile = new File(splitPath);
		File saveFile = new File(savePath);
		String temp = splitFile.getName();
		String fileName = temp.substring(0, temp.lastIndexOf("."));
		if (splitPath.equals("") || !(splitFile.exists() && splitFile.isFile())) {
			showMessage("没有需要被分割的文件");
			return;
		}
		if (!savePath.equals("")) {
			if (!saveFile.exists()) {
				showMessage("保存目录有问题");
				return;
			} else if (saveFile.isFile()) {
				showMessage("保存目录有问题");
				return;
			}

		} else {
			String newSaveP = splitFile.getParent() + FILE_SEPARATOR + fileName + PARTS_SUFIX;
			saveFile = new File(newSaveP);
			if (!saveFile.exists())
				saveFile.mkdirs();

		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(splitPath);			
			int bufSize = (int) Long.parseLong(size);
			byte[] buf = new byte[bufSize * K];
			int time = (int) (splitFile.length() / bufSize / 1000) + 1;
			for (int i = 0; i < time; i++) {
				fos = new FileOutputStream(new File(saveFile, fileName + PARTS_SUFIX + i));
				int length = fis.read(buf);
				fos.write(buf, 0, length);
				fos.close();
			}
			
			Properties p = new Properties();
			p.setProperty("name", splitFile.getName());
			p.setProperty("num", ""+time);
			fos = new FileOutputStream(new File(saveFile,"propertys"));
			p.store(fos, "This is a important file.Please do not modify it.");
			showMessage("分割成功");
			Desktop.getDesktop().open(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void showMessage(String mes) {
		String title = "系统信息";
		JOptionPane.showMessageDialog(this.getContentPane(), mes, title, JOptionPane.INFORMATION_MESSAGE);
	}

	private String selectPath() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = chooser.showOpenDialog(MainWindows.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();

		}
		return null;
	}

}
