package com.cfcc.codegen.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.cfcc.codegen.entity.CodeFile;
import com.cfcc.codegen.generator.GenerateType;

public class FileUtil {

	public static final String UTF8 = "UTF-8";
	public static final String NEXT_LINE = "\r\n";
	public static final String GEN_JAVA_SRC_MS_DIR = "gen_java_ms_src"; // 保存生成微服务java文件的路径
	public static final String GEN_JAVA_SRC_WEB_DIR = "gen_java_web_src"; // 保存生成前端java文件的路径
	public static final String GEN_JSP_SRC_DIR = "gen_jsp_src"; // 保存生成jsp文件的路径

	private static final String ZIP_COMMENT = "本文件由代码生成工具autoCode生成.\r\n --by hua";

	/**
	 * 读文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readFromClassPath(String fileName) {
		Resource resource = new ClassPathResource(fileName);
		StringBuilder sb = new StringBuilder();
		BufferedReader read = null;
		InputStream is = null;
		try {
			try {
				resource.getFile();
				read = new BufferedReader(new FileReader(resource.getFile()));
			} catch (Exception e) {
				is = FileUtil.class.getResourceAsStream("/" + fileName);
				read = new BufferedReader(new InputStreamReader(is, UTF8));
			}

			char[] buf = new char[4096];
			int ret = 0;
			ret = read.read(buf);
			while (ret > 0) {
				sb.append(new String(buf, 0, ret));
				ret = read.read(buf);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (read != null) {
					read.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String read(String fileName) {
		StringBuilder sb = new StringBuilder();
		BufferedReader read = null;
		try {
			File file = new File(fileName);
			read = new BufferedReader(new FileReader(file));
			char[] buf = new char[(int) file.length()];
			read.read(buf);
			sb.append(buf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 写文件
	 */
	public static void write(String txt, String fileName, String charset) {
		OutputStreamWriter osw = null;
		try {
			if (charset == null) {
				charset = UTF8;
			}
			osw = new OutputStreamWriter(new FileOutputStream(fileName), charset);
			osw.write(txt);
			osw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				osw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void write(String txt, String fileName) {
		write(txt, fileName, UTF8);
	}

	/**
	 * copy file or folder
	 * 
	 * @param srcFile
	 * @param desFile
	 * @throws IOException
	 */
	public static void copy(File srcFile, File desFile) {
		if (!srcFile.exists()) {
			return;
		}
		if (!desFile.exists()) {
			desFile.mkdirs();
		}
		if (srcFile.isFile()) {
			doCopy(srcFile, desFile);
		} else {
			String objFolder = desFile.getPath() + File.separator + srcFile.getName();
			File _objFolderFile = new File(objFolder);
			_objFolderFile.mkdirs();
			for (File sf : srcFile.listFiles()) {
				copy(sf, new File(objFolder));
			}
		}
	}

	private static void doCopy(File srcFile, File desFile) {
		InputStream ins = null;
		FileOutputStream outs = null;
		try {
			File objFile = new File(desFile.getPath() + File.separator + srcFile.getName());
			ins = new FileInputStream(srcFile);
			outs = new FileOutputStream(objFile);
			byte[] buffer = new byte[ins.available()];
			int length;
			while ((length = ins.read(buffer)) != -1) {
				outs.write(buffer, 0, length);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ins.close();
				outs.flush();
				outs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * 将InputStream中的UTF8内容读取到字符串中
	 * 
	 * @param in
	 *            读入流
	 * @return 记录流中内容的字符串
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFileUtf8(InputStream in) throws IOException {
		StringBuffer content = new StringBuffer();
		if (in == null) {
			// 如果流为空，那么返回空字符串
			return content.toString();
		}
		char[] buf = new char[4096];
		int ret = 0;
		BufferedReader bin = new BufferedReader(new InputStreamReader(in, "utf-8"));
		ret = bin.read(buf);
		while (ret > 0) {
			content.append(new String(buf, 0, ret));
			ret = bin.read(buf);
		}
		bin.close();
		return content.toString();
	}

	/**
	 * 将utf-8文件中的内容读取到字符串中
	 * 
	 * @param fileName
	 *            文件的绝对路径
	 * @return 保存文件内容的字符串
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFileUtf8(String fileName) throws FileNotFoundException, IOException {
		return readFileUtf8(new FileInputStream(fileName));
	}

	/**
	 * 创建文件夹
	 * 
	 * @param folderName
	 */
	public static void createFolder(String folderName) {
		File file = new File(folderName);
		if (file.exists()) {
			return;
		}
		file.mkdirs();
	}

	/**
	 * 删除空目录
	 * 
	 * @param dir
	 *            将要删除的目录路径
	 */
	public static void doDeleteEmptyDir(String dir) {
		(new File(dir)).delete();
	}

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * 
	 * @param dir
	 *            将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful. If a
	 *         deletion fails, the method stops attempting to delete and returns
	 *         "false".
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	public static void createFolders(List<String> folders) {
		for (String folder : folders) {
			createFolder(folder);
		}
	}

	/**
	 * 重命名
	 * 
	 * @param srcFileName
	 * @param desFileName
	 */
	public static void renameFile(String srcFileName, String desFileName) {
		File desFile = new File(desFileName);
		if (desFile.exists()) {
			return;
		}
		File srcFile = new File(srcFileName);
		srcFile.renameTo(desFile);
	}

	/**
	 * 把"."操作符转化成"/"
	 * 
	 * @param path
	 * @return
	 */
	public static String replacePointToSep(String path) {
		return path.replaceAll("\\.", "/");
	}

	/**
	 * zip("D:/ccc", "D:/ccc.zip");
	 * 
	 * @param inputFileName
	 *            目标文件夹
	 * @param desZipFile
	 *            生成zip
	 * @throws Exception
	 */
	public static void zip(String inputFileName, String desZipFile) throws Exception {
		zip(desZipFile, new File(inputFileName));
	}

	/**
	 * 保存生成的文件
	 * 
	 * @param packageName
	 *            包名
	 * @param savePath
	 *            保存路径(包括模块名)
	 * @param fileName
	 *            文件名
	 * @param content
	 *            文件内容
	 * @param charset
	 *            编码格式
	 */
	public static void writeCodeFile(CodeFile codeFile, String charset) {
		String packageName = codeFile.getTableName();
		String fileName = codeFile.getTemplateName();
		String content = codeFile.getContent();
		String savePath = codeFile.getSavePath();
		String fileSavePath = replacePointToSep(packageName) + File.separator + savePath;
		GenerateType type = codeFile.getGenerateType();
		if (StringUtils.endsWithIgnoreCase(fileName, ".java")) {
			if (type.equals(GenerateType.CONTROLLER_MS) || type.equals(GenerateType.SERVICE)) { // 微服务和服务层
				fileSavePath = GEN_JAVA_SRC_MS_DIR + File.separator + fileSavePath;
			} else if (type.equals(GenerateType.CONTROLLER)) { // 前端
				fileSavePath = GEN_JAVA_SRC_WEB_DIR + File.separator + fileSavePath;
			} else {
				throw new RuntimeException("暂不支持该类型文件：" + fileName);
			}
		} else if (StringUtils.endsWithIgnoreCase(fileName, ".jsp")) {
			fileSavePath = GEN_JSP_SRC_DIR + File.separator + savePath;
		} else {
			throw new RuntimeException("暂不支持该类型文件：" + fileName);
		}
		createFolder(fileSavePath);
		write(content, fileSavePath + File.separator + fileName, charset);
	}

	public static void writeCodeFileList(List<CodeFile> codeFileList, String charset) {
		for (CodeFile codeFile : codeFileList) {
			writeCodeFile(codeFile, charset);
		}
	}

	private static void zip(String zipFileName, File inputFile) throws Exception {
		/*
		 * ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
		 * zipFileName)); out.setComment(String.format(ZIP_COMMENT,
		 * inputFile.getName(), DateUtil.ymdFormat(new Date())));
		 * out.setEncoding("GBK"); zip(out, inputFile, ""); out.close();
		 */
	}

	/*
	 * private static void zip(ZipOutputStream out, File f, String base) {
	 * FileInputStream in = null; if (f.isDirectory()) { File[] fl =
	 * f.listFiles(); // 如果不加最外层文件夹作为zip名 base = (base.length() == 0 ?
	 * f.getName() : base); try { out.putNextEntry(new
	 * org.apache.tools.zip.ZipEntry(base + "/")); } catch (IOException e) {
	 * e.printStackTrace(); } base = base.length() == 0 ? "" : base + "/"; for
	 * (File file : fl) { zip(out, file, base + file.getName()); } } else { try
	 * { out.putNextEntry(new org.apache.tools.zip.ZipEntry(base)); in = new
	 * FileInputStream(f); byte[] buf = new byte[in.available()]; in.read(buf);
	 * out.write(buf); in.close(); } catch (IOException e) {
	 * e.printStackTrace(); } } }
	 */

	public static void main(String[] args) {
		/*
		 * String s = read("S:/Users/thc/Desktop/1.txt"); String[] arr =
		 * s.split(","); for (String str : arr) { System.out.println(
		 * "INSERT INTO table VALUE('" + str + "')"); }
		 */
		// String content = readFromClassPath("cfccvm/Controller.vm");
		// System.out.println(content);
		// try {
		// zip("D:/ccc", "D:/ccc.zip");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		createFolder(".\\gen_java_src");
	}
}
