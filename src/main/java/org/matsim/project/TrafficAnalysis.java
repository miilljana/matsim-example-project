package org.matsim.project;



import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TrafficAnalysis{
    public static void unzip(Path path1, Path path2) throws IOException {
        byte[] buffer = new byte[1024];
        FileInputStream fi = new FileInputStream(path1.toString());
        GZIPInputStream gz = new GZIPInputStream(fi);
        FileOutputStream fileOutputStream = new FileOutputStream(path2.toString());
        int bytes_read;

        while ((bytes_read = gz.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, bytes_read);
        }
        fi.close();
        gz.close();
    }
    public static void getTravelTime(ArrayList<String> moyuaLinks, ArrayList<String> surroundingLinks, Path path1, Path path2, Path result) throws IOException {


        unzip(path1,path2);

        BufferedReader bf = new BufferedReader(new FileReader(path2.toString()));
        String line = "";




            FileWriter fw = new FileWriter(result.toString());
            fw.write(",level1,,,level2,,,level3,\n");
            fw.write("tt_min,tt_avg,tt_max,tt_min,tt_avg,tt_max,tt_min,tt_avg,tt_max\n");

            boolean header = true;
            double[] tt_min = {0, 0, 0};
            double[] tt_avg = {0, 0, 0};
            double[] tt_max = {0, 0, 0};

            while ((line = bf.readLine()) != null) {
                String[] data = line.split("\t");
                if (header) {
                    header = false;
                } else {
                    String link = data[0];


                    double[] tt_pom = {0, 0, 0};
                    for (int i = 0; i < 24; i++) {
                        tt_pom[0] += Double.parseDouble(data[82 + 3 * i]);
                        tt_pom[1] += Double.parseDouble(data[83 + 3 * i]);
                        tt_pom[2] += Double.parseDouble(data[84 + 3 * i]);
                    }


                    try{
                    if (moyuaLinks.contains(Long.parseLong(link))) {
                        tt_min[0] += tt_pom[0];
                        tt_avg[0] += tt_pom[1];
                        tt_max[0] += tt_pom[2];

                    }
                    if (surroundingLinks.contains(Long.parseLong(link))) {
                        tt_min[1] += tt_pom[0];
                        tt_avg[1] += tt_pom[1];
                        tt_max[1] += tt_pom[2];
                    }}
                    catch (Exception e){
                        System.out.println("Link cannot be converted: "+link);
                    }
                    tt_min[2] += tt_pom[0];
                    tt_avg[2] += tt_pom[1];
                    tt_max[2] += tt_pom[2];


                }
            }
        fw.write(tt_min[0] + "," + tt_avg[0] + "," + tt_max[0] + "," + tt_min[1] + "," + tt_avg[1] + "," + tt_max[1] + "," + tt_min[2] + "," + tt_avg[2] + "," + tt_max[2] + "\n");
            bf.close();
            fw.close();

        }


    public static void main(String[] args) throws IOException {
        Path path1 = Paths.get("D:", "Users", "miljana", "simulation", "output_157_11_4","ITERS","it.50","50.linkstats.txt.gz");
        Path path2 = Paths.get("D:", "Users", "miljana", "simulation", "output_157_11_4","ITERS","it.50","50.linkstats.txt");
        Path resuls = Paths.get("D:", "Users", "miljana", "results","traffic_data.csv");
        ArrayList<String> linksMoyua = GetLinks.getLinks().get("1");
        ArrayList<String> linksSurrounding = GetLinks.getSurroundingLinks();


        getTravelTime(linksMoyua,linksSurrounding,path1, path2,resuls);


    }
}
