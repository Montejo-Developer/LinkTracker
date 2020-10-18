/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package linktracker.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import linktracker.model.WebPage;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author Usuario
 */
public class FileUtils {

    private static String baseDir = "";
    private static String webFile = "urls.txt";

    public FileUtils() {
    }

    public static List<WebPage> loadPages(File fichero) {
        List<WebPage> webs = new ArrayList<>();
        try {
            FileReader fr = new FileReader(fichero);
            BufferedReader br = new BufferedReader(fr);

            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
                String[]data = linea.split(";");
                WebPage temp =new WebPage(data[0], data[1]);
                webs.add(temp);
            }

            fr.close();
            System.out.println("Se ha leido correctamente el fichero");
        } catch (Exception e) {
            System.out.println("Excepcion leyendo fichero " + fichero + ": " + e);
            
        }
        return webs;
    }

}
