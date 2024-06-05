package es.udc.redes.tutorial.info;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Info {
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println("Usage: java Info <input path>");
            }

            File file = new File(args[0]);

            if (file.exists()) {
                String extension = obtenerExtensionArchivo(file.getName());
                String type = Files.probeContentType(file.toPath());
                String isDir = file.isDirectory() ? "directory" : "not a directory";

                Date modDate = new Date(file.lastModified());
                SimpleDateFormat newDate = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                String lastMod = newDate.format(modDate);

                System.out.println("Name: " + file.getName());
                System.out.println("Extension: " + extension);
                System.out.println("Type: " + (type != null ? type : "Unknown"));
                System.out.println("Size: " + file.length() + " bytes");
                System.out.println("Last Modified: " + lastMod);
                System.out.println("Is Directory: " + isDir);
                System.out.println("Absolute Path: " + file.getAbsolutePath());
            }
            else{
                System.err.println("File does not exist");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String obtenerExtensionArchivo(String fileName) {
        int indexPunto = fileName.lastIndexOf(".");
        if (indexPunto == -1) {
            return "";
        }
        return fileName.substring(indexPunto + 1);
    }
}
