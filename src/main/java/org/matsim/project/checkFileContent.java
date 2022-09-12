package org.matsim.project;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class checkFileContent {
    public static void main(String[] args) throws IOException {
        Path path1 = Paths.get("D:","Users","miljana","simulations","output_14578_14_4");
        Path path2 = Paths.get("D:","Users","miljana","simulations","output_14578_10_1");
        File file1 = new File(path1.toString());
        File file2 = new File(path2.toString());
        boolean isTwoEqual = FileUtils.contentEquals(file1,file2);
        System.out.println(isTwoEqual);
    }
}
