package es.udc.redes.webserver;

import java.net.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerThread extends Thread {

    private final Socket socket;

    public ServerThread(Socket s) {
        // Store the socket s
        this.socket = s;
    }

    public void run() {
        try {
            // This code processes HTTP requests and generates
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter newOutput = new PrintWriter(socket.getOutputStream(), true);
            BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());

            // Lectura de la solicitud del cliente
            StringBuilder message = new StringBuilder();
            String read;

            while ((read = input.readLine()) != null && !read.isEmpty()) {
                message.append(read).append("\n");
            }
            String recMessage = message.toString();

            // Separación de la solicitud en líneas
            String[] request = recMessage.split("\n");

            if (request.length > 0) {
                String[] firstLineTokens = request[0].split("\\s+"); // Dividir la primera línea por espacios

                if (firstLineTokens.length >= 3) {
                    String method = firstLineTokens[0];
                    String fileContent = firstLineTokens[1];
                    String manner = firstLineTokens[2];

                    String modDate = null;
                    boolean isMod = false;
                    boolean isBad;

                    // Buscar el encabezado "If-Modified-Since" en las líneas de la solicitud
                    for (String tokens : request) {
                        if (tokens.startsWith("If-Modified-Since:")) {
                            modDate = tokens.substring(tokens.indexOf(" ") + 1).trim();
                            isMod = true;
                        }
                    }

                    // Crear y configurar el objeto Header según el método de solicitud
                    isBad = false;

                    if ("HEAD".equals(method)) {
                        processHead(newOutput, isBad, isMod, fileContent, modDate, manner);

                    } else if ("GET".equals(method)) {
                        processGet(newOutput, output, isBad, isMod, fileContent, modDate, manner);
                    } else {
                        isBad = true;
                        fileContent = "/error400.html";
                        processHead(newOutput, isBad, isMod, fileContent, modDate, manner);
                    }
                }
            }

            // Uncomment next catch clause after implementing the logic
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());

        } finally {
            if (!socket.isClosed() && socket != null) {
                try {
                    socket.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.err.println("Socket error <close>");
            }
        }
    }

    public String getDate() {
        SimpleDateFormat newDate = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Date date = new Date();

        return newDate.format(date);
    }

    public String getModDate(File file) {
        Date modDate = new Date(file.lastModified());
        SimpleDateFormat newDate = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String lastMod = newDate.format(modDate);

        return lastMod;
    }

    public String getFileType(File file) {
        String fileName = file.getName().toLowerCase();

        return switch (getExtension(fileName)) {
            case ".txt" -> "text/plain";
            case ".html", ".htm" -> "text/html";
            case ".gif" -> "image/gif";
            case ".png" -> "image/png";
            case ".ico" -> "image/ico";
            case ".pdf" -> "text/pdf";
            default -> "application/octet-stream";
        };
    }

    private static String getExtension(String fileName) {
        int indexPunto = fileName.lastIndexOf(".");
        if (indexPunto == -1) {
            return "";
        }
        return fileName.substring(indexPunto);
    }

    public void processGet(PrintWriter printOutput, BufferedOutputStream outputStream, boolean isBad, boolean isMod, String fileContent, String modDate, String manner) throws IOException {
        //Procesa la petición Get

        RequestCodes requestCode = getRequestCode(isBad, isMod, fileContent, modDate);
        File newFile = getFile(isBad, fileContent);

        printResponseHeaders(printOutput, manner, requestCode, newFile);

        if (requestCode == RequestCodes.OK) {
            sendFileContent(outputStream, newFile);
        }

        printOutput.flush();
    }

    public void processHead(PrintWriter printOutput, boolean isBad, boolean isMod, String fileContent, String modDate, String manner) throws IOException {
        //Procesa la petición Head

        RequestCodes requestCode = getRequestCode(isBad, isMod, fileContent, modDate);
        File newFile = getFile(isBad, fileContent);

        printResponseHeaders(printOutput, manner, requestCode, newFile);

        printOutput.flush();
    }

    private RequestCodes getRequestCode(boolean isBad, boolean isMod, String fileContent, String modDate) {
        //Revisa que codigo de la petición ha saltado

        RequestCodes requestCode;
        File thisFile = new File("p1-files" + fileContent);
        File newFile;

        if (isBad) {
            requestCode = RequestCodes.BAD_REQUEST;
            newFile = new File("p1-files/error400.html");

        } else if (thisFile.exists()) {
            newFile = new File("p1-files" + fileContent);

            if (!isMod) {
                requestCode = RequestCodes.OK;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                Date ifModifiedSinceDate = null;

                try {
                    ifModifiedSinceDate = dateFormat.parse(modDate);

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                long ifModifiedSinceTime = ifModifiedSinceDate.getTime();
                long lastModifiedTime = newFile.lastModified();

                if (lastModifiedTime >= ifModifiedSinceTime) {
                    requestCode = RequestCodes.NOT_MODIFIED;
                } else {
                    requestCode = RequestCodes.OK;
                }
            }
        } else {
            requestCode = RequestCodes.NOT_FOUND;
            newFile = new File("p1-files/error404.html");
        }

        return requestCode;
    }

    private File getFile(boolean isBad, String fileContent) {
        //Obtiene el archvio correspondiente al error

        if (isBad) {
            return new File("p1-files/error400.html");
        } else {
            return new File("p1-files" + fileContent);
        }
    }

    private void printResponseHeaders(PrintWriter printOutput, String manner, RequestCodes requestCode, File newFile) {
        //Imprime toda la información de la petición

        printOutput.println(manner + " " + requestCode.getRequest());
        printOutput.println("Date: " + getDate());
        printOutput.println("Last-Modified: " + getModDate(newFile));
        printOutput.println("Server: Chiqui Ibai's Server");
        printOutput.println("Content-Length: " + newFile.length());
        printOutput.println("Content-Type: " + getFileType(newFile) + "\n");
    }

    private void sendFileContent(BufferedOutputStream outputStream, File newFile) throws IOException {
        //Envía los bytes al nuevo archivo

        int tam = (int) newFile.length();
        FileInputStream inputStream = new FileInputStream(newFile);
        byte[] buffer = new byte[tam];

        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.flush();
    }
}
