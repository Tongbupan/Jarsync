package org.metastatic.rsync.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.metastatic.rsync.ChecksumPair;
import org.metastatic.rsync.Rdiff;

public class Test {


	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		String old_file = "I:\\shareData\\9m.docx";
		//String new_file = "/home/emacle/adet/a2048-ch.txt";
		String rsync_new_file = "I:\\shareData\\9m-jgen.docx";
		//String rsync_new_file = "C:\\test\\rsync\\rsync_test_new.txt";
		
		Rdiff rdiff = new Rdiff();
		
		//生成signiture文件
		//OutputStream sig_out = new FileOutputStream("/home/emacle/adet/a2048.txt.sig");
		//OutputStream delta_out = new FileOutputStream("/home/emacle/adet/test.txt.jdelta");
		InputStream delta_in = new FileInputStream("I:\\shareData\\9m.docx.delta");
		//FileInputStream old_in = new FileInputStream(old_file);
		//FileInputStream new_in = new FileInputStream(new_file);
		
		OutputStream new_out = new FileOutputStream(rsync_new_file);
	/*	
		List<ChecksumPair> sigs = rdiff.makeSignatures(old_in);
		for(ChecksumPair checksum_pair: sigs){
			System.out.println(checksum_pair.toString());
		}
*/

		
		//rdiff.writeSignatures(sigs, sig_out);
		
		//生成delta文件
		//List deltas = rdiff.makeDeltas(sigs, new_in);
			System.out.println("------------------------make deltas--------------");
		//rdiff.writeDeltas(deltas, delta_out);
		//根据delta文件和源文件生成最新的文件
		//rdiff.rebuildFile(new File(old_file), deltas, new_out);
		rdiff.rebuildFile(new File(old_file), delta_in, new_out);
	}
	
}
