package org.dereglobus.updater

/**
 * Eine Filter-Implementierung, die schlicht nichts tut.
 * 
 * @author marcvonrenteln
 *
 */
class NullFilter implements Filter {

	@Override
	public String filter(String source) {
		return source;
	}
}
