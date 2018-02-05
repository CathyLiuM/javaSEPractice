package io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 补充：写文件追加方式：
 1.
 RandomAccessFile write = new RandomAccessFile(file,"rw");
 long len = write.length();
 write.seek(len);//定位到尾部
 write.writeBytes(new String(str.getBytes(),"iso8859-1"));
 2.
 FileWriter(,true)//追加
 3.
 FileOutputStream(,true)//追加
 4.
 FileChannel read = new RandomAccessFile(fileName,"r").getChannel();
 FileChannel writer = new RandomAccessFile(outFileName,"rw").getChannel();
 ByteBuffer bb = ByteBuffer.allocate(1024);
 while(read.read(bb)!=-1){
 bb.flip();//做好让别人来读取字节的准备
 writer.position(writer.size());//定位到尾部
 writer.write(bb);
 bb.clear();
 }
 read.close();
 writer.close();
 */
public class WriteFile {
	
	/**
	 * 使用FileWriter,使用BufferedWriter将其包装用于缓存输出，缓存往往能显著增加IO操作的性能
	 * @param fileName
	 * @throws IOException
	 */
	public static void writeFileByBufferWriter(String fileName,String outFileName) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		FileWriter fw = new FileWriter(new File(outFileName));
		BufferedWriter bw = new BufferedWriter(fw);
		String s;
		while((s = in.readLine())!=null){
			bw.write(s+"\n");
		}
		bw.close();
		fw.close();
		in.close();
	}
	
	/**
	 * PrintWriter仍旧使用缓存，是一种快捷方式，可以对数据进行格式化，以方便阅读
	 * @throws IOException 
	 */
	public static void writeFileByPrintWriter(String fileName,String outFileName) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		PrintWriter out = new PrintWriter(outFileName);
		String s;
		while((s = in.readLine())!=null){
			out.println(s);
		}
		out.close();
		in.close();
	}
	
	/**
	 * 使用FileOutputStream
	 * @param fileName
	 * @param outFileName
	 * @throws IOException
	 */
	public static void writeFileByFileOutputStream1(String fileName,String outFileName) throws IOException{
		FileInputStream in = new FileInputStream(new File(fileName));
		FileOutputStream out = new FileOutputStream(new File(outFileName));
		int b;
		while((b=in.read())!=-1){
			out.write(b);
		}
		in.close();
		out.close();
	}
	
	/**
	 * 使用FileOutputStream,自定义数组
	 * @param fileName
	 * @param outFileName
	 * @throws IOException 
	 */
	public static void writeFileByFileOutputStream2(String fileName,String outFileName) throws IOException{
		FileInputStream in = new FileInputStream(new File(fileName));
		FileOutputStream out = new FileOutputStream(new File(outFileName));
		int b = 0;
		byte[] bytes = new byte[8192];
		while((b = in.read(bytes))!=-1){
			out.write(bytes, 0, b);
		}
		in.close();
		out.close();
	}
	
	/**
	 * A.BufferedInputStream内置了一个缓冲区(数组),默认是8KB=8*1024=8192字节，从中读取一个字节时，
	 * BufferedInputStream会一次性从文件中读取8192个, 存在缓冲区中, 返回给程序一个，
	　* 程序再次读取时, 就不用找文件了, 直接从缓冲区中获取,直到缓冲区中所有的都被使用过, 才重新从文件中读取8192个
	　* 
	 * B.BufferedOutputStream也内置了一个缓冲区(数组)，
	　* 程序向流中写出字节时, 不会直接写到文件, 先写到缓冲区中,直到缓冲区写满, BufferedOutputStream才会把缓冲区中的数据一次性写到文件里
	 *
	 * @param fileName
	 * @param outFileName
	 * @throws IOException 
	 */
	public static void writeFileByBufferedOutputStream(String fileName,String outFileName) throws IOException{
		FileInputStream in = new FileInputStream(new File(fileName));
		FileOutputStream out = new FileOutputStream(new File(outFileName),true);
		
		BufferedInputStream bis = new BufferedInputStream(in);
		BufferedOutputStream bos = new BufferedOutputStream(out);
		
		int b;
		while((b = bis.read())!=-1){
			bos.write(b);
		}
		
		bis.close();
		bos.close();
	}
	
	/**
	 * 使用DataOutputStream写字符串并设置编码UTF-8或其他类型的数据，可以使用DataInputStream恢复数据,
	 * 但是为了保证所有度方法能正常工作，必须知道流中数据项所在的确切位置。
	 * @throws IOException 
	 */
	public static void writeFileByDataOutputStream(String outFileName) throws IOException{
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFileName)));
		out.writeDouble(3.1415);
		out.writeUTF("hello world");
		out.writeBoolean(false);
		out.close();
		
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(outFileName)));
		System.out.println(in.readDouble());
		System.out.println(in.readUTF());
		System.out.println(in.readBoolean());
	}
	/**
	 * 测试文件大小16.355M
	 */
	public static void main(String[] args) throws IOException{
		long testTime1 = System.currentTimeMillis();
		writeFileByBufferWriter("E://QAP_201711281836.txt","E://out14.txt");//342ms
//		writeFileByPrintWriter("E://QAP_201711281836.txt","E://out13.txt");//290ms
//		writeFileByPrintWriter("E://QAP_201711281836.txt","E://out13.txt");//290ms
//		writeFileByFileOutputStream1("E://QAP_201711281836.txt","E://out10.txt");//97589ms
//		writeFileByFileOutputStream2("E://QAP_201711281836.txt","E://out11.txt");//42ms
//		writeFileByBufferedOutputStream("E://QAP_201711281836.txt","E://out12.txt");//1025ms
//		writeFileByDataOutputStream("E://test.txt");
		long testTime2 = System.currentTimeMillis();
		System.out.print(testTime2-testTime1);
		System.out.println("ms");
	}
	/*
	 * PrintWriter使用了缓存，写法更简单
	 * 测试结果表明：定义小数组会比用BufferedOutputStream更快
	 */
}
