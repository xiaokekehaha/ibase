package com.wzk.mvc.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对文件重命名
 * @author zk
 *
 */
public class ReName {
	public static List<Map<String,String>> getFileNameAndPath(File path,String prefix,List<Map<String,String>> lm){
		if(!path.exists()){
			System.out.println("文件或文件夹不存在");
		}else{
			if(path.isFile()){
				if(path.getName().indexOf(prefix)>=0){
					Map<String,String> m = new HashMap<String,String>();
					m.put("path", path.getAbsolutePath());
					m.put("name", path.getName());
					lm.add(m);
				}
			}else{
				File[] files = path.listFiles();
				for(File f:files){
					getFileNameAndPath(f,prefix,lm);
				}
			}
		}
		return lm;
	}
	public static void removePrefix(String path,String prefix){
		File file = new File(path);
		String oldName = "";
		String newName = "";
		if(!file.exists()){
			System.out.println("文件夹不存在");
		}else{
			File[] files = file.listFiles();
			for(File oldFile:files){
				oldName = oldFile.getName();
				newName = oldName.replace(prefix, "");
	            File newFile=new File(path+"/"+newName); 
	            if(newFile.exists()){
	            	//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
	            	System.out.println(newName+"已经存在！"); 
	            }else{ 
	            	oldFile.renameTo(newFile); 
	            } 
			}
		}
	}
	public static void main(String[] args) {
//		removePrefix("D:/spark/123", "hhh");
		List<Map<String,String>> lm = new ArrayList<Map<String,String>>();
		lm = getFileNameAndPath(new File("D:/spark/123"),"h",new ArrayList<Map<String,String>>());
		System.out.println(lm.size());
	}
}
