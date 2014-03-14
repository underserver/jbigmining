package org.sg.recognition.exceptions;

/**
 * Created with IntelliJ IDEA.
 * Author: Sergio Ceron
 * Date: 28/05/13
 * Time: 07:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilterException extends Exception {

    public FilterException(String type, Object value, int index, Exception e) {
        super("Cannot convert feature " + index + "(" + value + ") to " + type + " type: " + e.getMessage() );
    }
}
