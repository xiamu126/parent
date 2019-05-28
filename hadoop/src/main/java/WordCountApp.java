import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WordCountApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, URISyntaxException {
        System.setProperty("HADOOP_USER_NAME", "sybd");
        System.setProperty("hadoop.home.dir", "/home/sybd/Downloads/hadoop-2.6.0-cdh5.15.1");

        var configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://192.168.11.101:8020");

        var job = Job.getInstance(configuration);
        job.setJarByClass(WordCountApp.class);
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        job.setCombinerClass(WordCountReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        var fileSystem = FileSystem.get(new URI("hdfs://192.168.11.101:8020"), configuration, "sybd");
        var outputPath = new Path("/wordcount/output");
        if(fileSystem.exists(outputPath)) {
            fileSystem.delete(outputPath, true);
        }

        FileInputFormat.setInputPaths(job, new Path("/wordcount/input"));
        FileOutputFormat.setOutputPath(job, new Path("/wordcount/output"));

        System.out.println(job.waitForCompletion(true));
    }
}
