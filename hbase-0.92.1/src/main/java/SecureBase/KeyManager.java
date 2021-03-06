package SecureBase;

import javax.crypto.KeyGenerator;
import javax.crypto.Cipher;
import javax.crypto.spec.*;

import org.apache.hadoop.hbase.util.Bytes;

import java.security.*;
import java.util.*;
import java.io.*;

/* The purpose of this class is to maintain a list of Table Hash Values 
 * and even if the table name is changed it maps to old hash to new name
 */
public class KeyManager
{
	private static String masterKey = "MasterKey"; 
	private static String logLocation = "KeyManagerLog.txt";

	
	KeyManager()
	{ }
	
	public static byte[] getTableHash(String tablename)
	{
		return getTableHash(Bytes.toBytes(tablename));
	}
	
	public static byte[] getTableHash(byte[] tablename)
	{
		MessageDigest md;			  
		try 
		{ 
			md = MessageDigest.getInstance("MD5");
			md.update(Bytes.toBytes(masterKey));
			md.update(tablename);
		  	return md.digest();
		}
		catch (Exception e)
		{ e.printStackTrace();}
		return null;
	} 
	
	public final static boolean has(String tableName) throws IOException
	{
		List<String> tableNameList = KeyManager.toList();
		return tableNameList.contains(tableName);
	}
	
	public final static void add(String tableName) throws IOException
	{
		List<String> tableNameList = KeyManager.toList();
		if(!tableNameList.contains(tableName))
		{
			tableNameList.add(tableName);
		}
		KeyManager.toFile(tableNameList);
	}
	
	public final static void del(String tableName) throws IOException
	{
		List<String> tableNameList = KeyManager.toList();
		tableNameList.remove(tableName);
		KeyManager.toFile(tableNameList);
	}
	
	private static List<String> toList() throws IOException
	{
		List<String> temp = new ArrayList<String>();
		File file = new File(logLocation);
		if(!file.exists())
		{
			return temp;
		}
		else
		{
			Scanner fileReader =  new Scanner(file);
			fileReader.useDelimiter(System.getProperty("line.separator"));
			while(fileReader.hasNext())
			{
				temp.add(fileReader.nextLine());	
			}
			fileReader.close();
			return temp;
		}
	}
	
	private static void toFile(List<String> tableNameList) throws IOException
	{
		File file = new File(logLocation);
		file.delete();
		file.createNewFile();
		
		FileWriter fileWriter = new FileWriter(file);
	    BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		
		for(String s: tableNameList)
	    {
	    	bufferWriter.write(s);
			bufferWriter.newLine();
	    }
	    
	    bufferWriter.close();
	    fileWriter.close();
	}
	
	
}
