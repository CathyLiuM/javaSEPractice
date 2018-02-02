package io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

/**
 * 1.java io流分类：字节流和字符流,InputStream/OutputStream 面向字节形式的I/O,Reader/Writer 提供兼容Unicode与面向字符的I/O
 * 2.InputStreamReader可以把InputStream转为Reader,OutputStreamWriter可以把OutputStream转为Writer。
 * 3.设计Reader和Writer继承层次结构主要是为了支持处理16位的Unicode字符，且它的操作比旧类库更快
 * 4.明智的做法：尽量尝试Reader/Writer，一旦程序代码无法成功编译，再使用面向字节的类库。
 */
public class ReadFile {
	/**
	 * 以字节为单位读取文件,一次读取一字节,常用于图片、影像、声音
	 * @param fileName
	 * @throws IOException
	 */
	public static void readFileByByte(String fileName) throws IOException{
		InputStream in = new FileInputStream(new File(fileName));
		int tmpByte;
		while((tmpByte = in.read())!=-1){
			System.out.write((char)tmpByte);//read是以int形式返回下一字节，所以必须类型转化为char
		}
		in.close();
	}
	
	/**
	 * 以字节为单位读取文件,一次多字节,常用于图片、影像、声音
	 * 建立了小数组byte[] tmpBytes，使用了缓冲的思想，会比从输入流中一次读取一个字节快很多
	 * @param fileName
	 * @throws IOException
	 */
	public static void readFileByBytes(String fileName) throws IOException{
		InputStream in = new FileInputStream(new File(fileName));
		
		byte[] tmpBytes = new byte[100];
		int byteread = 0;
		
		System.out.println("当前字节输入流中字节数："+in.available());
		
		while((byteread = in.read(tmpBytes))!=-1){
			System.out.write(tmpBytes,0,byteread);
		}
		
		in.close();
	}
	
	/**
	 * 以字符为单位读取文件，一次读一个字符，常用于读文本，数字等类型的文件
	 * @param fileName
	 * @throws IOException
	 */
	public static void readFileByChar(String fileName) throws IOException{
		Reader reader = new InputStreamReader(new FileInputStream(new File(fileName)));
		
		int tmpChar;
		while((tmpChar = reader.read())!=-1){
			if (((char) tmpChar) != '\r')
			System.out.print((char)tmpChar);
		}
		reader.close();
	}
	
	/**
	 * 以字符为单位读取文件，一次读多个字符，常用于读文本，数字等类型的文件
	 * @param fileName
	 * @throws IOException
	 */
	public static void readFileByChars(String fileName) throws IOException{
		Reader reader = new InputStreamReader(new FileInputStream(new File(fileName)));
		char[] tmpChars = new char[100];
		
		int charread = 0;
		while((charread = reader.read(tmpChars))!=-1){
			if ((charread == tmpChars.length)
                    && (tmpChars[tmpChars.length - 1] != '\r'))
			System.out.print(tmpChars);
			else
				for (int i = 0; i < charread; i++) {
                    if (tmpChars[i] == '\r') {
                        continue;
                    } else {
                        System.out.print(tmpChars[i]);
                    }
                }
		}
		
		reader.close();
	}
	
	/**
	 * 以行为单位读取文件，用的最多的方法：BufferedReader，这是个很好的处理文件读取的类,缓冲输入文件，支持编码、线程安全、默认缓存为8KB=8*1024=8192个字节
	 * @param filename
	 * @throws IOException
	 */
	public static void readFileByBufferedReader(String filename,String outFileName) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(filename));
		PrintWriter writer = new PrintWriter(outFileName);
		String s;
//		StringBuilder sb = new StringBuilder();
		while((s = in.readLine())!=null){
			writer.write(s + "\n");
//			sb.append(s + "\n");
		}
		in.close();
		writer.close();
//		System.out.print(sb);
	}
	public static String read(String filename) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String s;
		StringBuilder sb = new StringBuilder();
		while((s = in.readLine())!=null){
			sb.append(s + "\n");
		}
		in.close();
		return sb.toString();
	}
	/**
	 * 从内存中读取输入
	 * @throws IOException 
	 * 
	 */
	public static void readFileByMemory() throws IOException{
		StringReader in = new StringReader(read("MemoryInput.java"));
		int c;
		while((c = in.read())!=-1){
			System.out.println(c);
		}
	}
	
	/**
	 * 读取格式化数据可以使用DataInputStream，它面向字节，因此必须使用InputStream类，不使用Reader类。
	 * @throws IOException 
	 */
	public static void readFileByFormatteredMemory() throws IOException{
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(read("").getBytes()));
		while(in.available()!=0){
			System.out.println((char)in.readByte());
		}
	}
	/**
	 * 使用java.nio.file.Files读取文件，这个类可以读取所有的内容到数组或者读取所有的行到一个列表 
	 * @param fileName
	 * @throws IOException
	 */
	public static void readFileByFiles(String fileName) throws IOException{
		byte[] bytes = Files.readAllBytes(Paths.get(fileName));
		List<String> allLines = Files.readAllLines(Paths.get(fileName));
		System.out.write(bytes);
		System.out.println(allLines.size());
	}
	
	/**
	 * 借助于apache commons的IOUtils类将InputStream复制到StingReader中
	 * @param fileName
	 * @throws IOException
	 */
	public static void readFileByIOUtil(String fileName) throws IOException{
		InputStream in = new FileInputStream(new File(fileName));
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer);
		System.out.println(writer.toString());
	}
	
	/**
	 * (可处理大文件)
	 * 使用org.apache.commons.io的LineIterator,在不重复读取与不耗尽内存的情况下处理大文件,
	 * BufferedReader通常在只有读到空格或者换行符时才会结束读取，攻击者很容易构内存攻击导致系统瘫痪，
	 * 出于安全考虑推荐使用io包的LineIterator，并且其在性能上也优于普通流。
	 * @param fileName
	 * @throws IOException
	 */
	public static void readFileByFilesUtils(String fileName) throws IOException{
//		FileUtils.readLines(new File(fileName), Charsets.UTF_8);   //读取所有行到内存中，数据量很大时，会产生OutOfMwmory
//		LineIterator it = FileUtils.lineIterator(new File(fileName));
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		LineIterator it = new LineIterator(reader);
		while(it.hasNext()){
			String line = it.nextLine();
			System.out.println(line);
		}
		LineIterator.closeQuietly(it);
	}
	/**
	 * (可处理大文件)
	 * Scanner读取文件，遍历文件中的所有行，允许对每一行进行处理，而不保持对它的引用，
	 * 使用这个扫描类可以根据一些正则表达式来读取相应的内容, 默认的是分割内容是空白,是不同步的非线程安全的类 
	 * @param fileName
	 * @throws IOException
	 */
	public static void readFileByScanner(String fileName) throws IOException{
//		Scanner scanner = new Scanner(new FileInputStream(fileName));
		Scanner scanner = new Scanner(Paths.get(fileName));
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			System.out.println(line);
		}
	}
	/**
	 * (可处理大文件)
	 * 利用FileChannel通道读写文件,采用字节模式，快速移动大量数据
	 * @param fileName
	 * @param outFileName
	 * @throws IOException
	 */
	public static void readFileByFileChannel(String fileName,String outFileName) throws IOException{
		FileChannel read = new RandomAccessFile(fileName,"r").getChannel();
		FileChannel writer = new RandomAccessFile(outFileName,"rw").getChannel();
		ByteBuffer bb = ByteBuffer.allocate(1024);
		while(read.read(bb)!=-1){
			bb.flip();//做好让别人来读取字节的准备
			writer.write(bb);
			bb.clear();
		}
		read.close();
		writer.close();
	}
	/**
	 * (可处理大文件)
	 * 使用自我独立的类：RandomAccessFile读写文件，类似于组合使用了DataInputStream和DataOutputStream,
	 * 它直接从Object派生，适用于由大小已知的记录组成的文件，
	 * 且只适用于文件，拥有和别的IO类型本质不同的行为，可以在一个文件内向前和向后移动，
	 * 在JDK1.4中，它的大多数功能被nio存储映射文件所取代。
	 * 
	 * @param fileName
	 * @param outFileName
	 * @throws IOException
	 */
	public static void readFileByRandomAccessFile(String fileName,String outFileName) throws IOException{
		RandomAccessFile reader = new RandomAccessFile(fileName,"r");
		RandomAccessFile writer = new RandomAccessFile(outFileName,"rw");
		int byteread = 0;
		byte[] b = new byte[1024];
		while((byteread = reader.read(b))!=-1){
//			System.out.write(b);
			writer.write(b, 0, byteread);
		}
		reader.close();
		writer.close();
	}
	/**
	 * (可处理大文件)
	 * 利用内存映射文件，假定整个文件都放在内存中，可以完全把它当做非常大的数组来访问。
	 * @param fileName
	 * @param outFileName
	 * @throws IOException
	 */
	public static void readFileByMappedBuffer(String fileName,String outFileName) throws IOException{
		FileChannel read = new FileInputStream(fileName).getChannel();
		FileChannel writer = new RandomAccessFile(outFileName,"rw").getChannel();
		MappedByteBuffer bb,cc = null;
		long i=0;
		long size = read.size()/30;
		while(i<read.size()&&(read.size()-i>size)){
			bb = read.map(FileChannel.MapMode.READ_ONLY, i, size);
			cc = writer.map(FileChannel.MapMode.READ_WRITE, i, size);
			cc.put(bb);
			i+=size;
			bb.clear();
			cc.clear();
		}
		long tmp = read.size()-i;
		bb = read.map(FileChannel.MapMode.READ_ONLY, i, tmp);
		cc = writer.map(FileChannel.MapMode.READ_WRITE, i, tmp);
		cc.put(bb);
		bb.clear();
		cc.clear();
		read.close();
		writer.close();
	}
	

	/**
	 * 测试文件大小16.355M
	 */
	public static void main(String[] args) throws IOException{
		long testTime1 = System.currentTimeMillis();
//		readFileByByte("E://QAP_201711281836.txt"); //41855ms
//		readFileByBytes("E://QAP_201711281836.txt");//2587ms
//		readFileByChar("E://QAP_201711281836.txt");//47078ms
//		readFileByChars("E://QAP_201711281836.txt");//2692ms
//		readFileByBufferedReader("E://QAP_201711281836.txt","E://out1.txt");//234ms
//		readFileByScanner("E://QAP_201711281836.txt");//4804ms
//		readFileByFiles("E://QAP_201711281836.txt");//2482ms
//		readFileByIOUtil("E://QAP_201711281836.txt");//2317ms
//		readFileByFilesUtils("E://QAP_201711281836.txt");//3296ms
//		readFileByFileChannel("E://QAP_201711281836.txt","E://out8.txt");//140ms
//		readFileByRandomAccessFile("E://QAP_201711281836.txt","E://out2.txt");//125ms
//		readFileByMappedBuffer("E://QAP_201711281836.txt","E://out9.txt");	//16ms
		long testTime2 = System.currentTimeMillis();
		System.out.print(testTime2-testTime1);
		System.out.println("ms");
	}
	/*
	 * 使用BufferedReader包裹FileReader，使用缓存是很好的用法，
	 * BufferedInputFile.read()用来从内存中读取数据，
	 * BufferedReader通常在只有读到空格或者换行符时才会结束读取，攻击者很容易构内存攻击导致系统瘫痪，出于安全考虑推荐使用io包的LineIterator，并且其在性能上也优于普通流，
	 * DataInputStream主要是用来读取格式化的数据，但是使用readLine()时不要使用DataInputStream，
	 * 为了读取大量数据，可以采用通道FileChannel和缓冲器ByteBuffer、Scanner、RandomAccessFile、内存映射文件MappedBuffer等。
	 * 测试结果表明：使用小数组比BufferedInputStream更快
	 */
}
