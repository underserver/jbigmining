package org.sg.recognition.exceptions;

/**
 * Created with IntelliJ IDEA.
 * Author: Sergio Ceron
 * Date: 28/05/13
 * Time: 07:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class TypeConvertException extends Exception {

    public TypeConvertException( Object object, String type, Exception e ) {
        super("Cannot convert " + object + " to " + type + ": " + e.getMessage() );
    }
}
