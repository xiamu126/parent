package com.sybd.znld.web;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import java.io.FileInputStream;
import java.io.IOException;

public class FastDFSTest {
    @Test
    public void test() throws IOException, MyException {
        var filePath = new ClassPathResource("fdfs_client.conf").getFile().getAbsolutePath();
        ClientGlobal.init(filePath);
        var trackerClient = new TrackerClient();
        var trackerServer = trackerClient.getConnection();
        var storageClient = new StorageClient(trackerServer, null);
        storageClient.delete_file("group1", "M00/00/00/wKgLZV3UzsWAcNZiAAAAvW-shhI70.conf");
        var info = storageClient.get_file_info("group1", "M00/00/00/wKgLZV3UzsWAcNZiAAAAvW-shhI70.conf");
        System.out.println(info);
        //var file = ResourceUtils.getFile("classpath:fdfs_client.conf");
        //var inputStream = new FileInputStream(file);
        //var ret = storageClient.upload_file(inputStream.readAllBytes(), "conf", null);
        //System.out.println(ret.length);

    }
}
