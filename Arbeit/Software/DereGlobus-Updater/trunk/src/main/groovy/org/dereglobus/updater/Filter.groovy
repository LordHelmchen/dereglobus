package org.dereglobus.updater

/**
 * Ein Filter ersetzt bestimmte vorkommen in einem String durch vordefinierte Werte.
 *  
 * @author marcvonrenteln
 *
 */
interface Filter {

	String filter(String source)
}
