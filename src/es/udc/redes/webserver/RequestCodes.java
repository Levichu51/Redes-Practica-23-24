package es.udc.redes.webserver;

public enum RequestCodes { //enum de codigos para mejor manejo
    OK("200 OK"),
    NOT_FOUND("404 NOT MODIFIED"),
    NOT_MODIFIED("304 NOT MODIFIED"),
    BAD_REQUEST("400 BAD REQUEST");

    private final String requestCode;

    RequestCodes(String code){
        this.requestCode = code;
    }

    public String getRequest(){
        return this.requestCode;
    }

}
