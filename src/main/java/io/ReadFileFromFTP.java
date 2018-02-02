package io;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.SocketException;

/**
 * 从FTP读取数据到本地
 */
public class ReadFileFromFTP {

    private static void getInputStreamFromFtp() throws IOException {
        FTPClient ftpClient = new FTPClient();;
        String ip = "110.172.211.13";
        String userName = "edi";
        String userPwd = "edi1qaz@WSX3edc";
        String path = "/NIKE/articleinfo";
        String fileName = "Product_20180103185339361.txt";
        String outFileName = "E://testProduct.txt";
        try{
            ftpClient.connect(ip);
            ftpClient.login(userName, userPwd);
            if(!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
                ftpClient.disconnect();
            }
            if(path!=null&&path.length()>0){
                ftpClient.changeWorkingDirectory(path);
            }
        }catch(SocketException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

        InputStream ins = null;
        ftpClient.enterLocalPassiveMode();//每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据.因为ftp server可能每次开启不同的端口来传输数据，但是在Linux上，由于安全限制，可能某些端口没有开启，所以就出现阻塞。
        try {
            ins = ftpClient.retrieveFileStream(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ins));
        PrintWriter writer = new PrintWriter(outFileName);
        String s;
        while((s = bufferedReader.readLine())!=null){
            writer.println(s);
        }
        ins.close();
        bufferedReader.close();
        writer.close();
        if(ins!=null){
            ins.close();
            ftpClient.completePendingCommand();//completePendingCommand()会一直在等FTP Server返回226 Transfer complete，但是FTP Server只有在接受到InputStream执行close方法时，才会返回。所以先要执行close方法
        }

        if(ftpClient.isConnected()){
            try{
                ftpClient.logout();
                ftpClient.disconnect();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        getInputStreamFromFtp();
    }
}
