import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class App {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        var configuration = new Configuration();
        configuration.set("dfs.replication", "1"); // 设置副本数为1
        var fileSystem = FileSystem.get(new URI("hdfs://192.168.11.101:8020"), configuration, "sybd");
        var path = new Path("/test");
        var result = fileSystem.mkdirs(path);
        var files = fileSystem.listStatus(new Path("/"));
        for (var file: files){
            System.out.println("file: " + file.toString());
        }
        System.out.println(result);
    }
}
