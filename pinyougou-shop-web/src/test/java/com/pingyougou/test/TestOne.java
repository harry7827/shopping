package com.pingyougou.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;

import java.io.IOException;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.jboss.netty.handler.codec.http.multipart.FileUpload;
import org.junit.Test;

public class TestOne {
	public static void main(String[] args) throws Exception {
		// 1、加载配置文件，配置文件中的内容就是 tracker 服务的地址。
		ClientGlobal.init("D:\\java\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fdfs_client.conf");
		// 2、创建一个 TrackerClient 对象。直接 new 一个。
		TrackerClient trackerClient = new TrackerClient();
		// 3、使用 TrackerClient 对象创建连接，获得一个 TrackerServer 对象。
		TrackerServer trackerServer = trackerClient.getConnection();
		// 4、创建一个 StorageServer 的引用，值为 null
		StorageServer storageServer = null;
		// 5、创建一个 StorageClient 对象，需要两个参数 TrackerServer 对象、StorageServer的引用
		StorageClient storageClient = new StorageClient(trackerServer, storageServer);
		// 6、使用 StorageClient 对象上传图片。
		//扩展名不带“.”
		String[] strings = storageClient.upload_file("F:/360Downloads/benchi.jpg", "jpg",null);
		// 7、返回数组。包含组名和图片的路径。
		for (String string : strings) {
			System.out.println(string);
		}
	}
	@Test
	public void func1() {
		System.out.println(this.getClass().getResourceAsStream("/config/fdfs_client.conf"));
	}
}
