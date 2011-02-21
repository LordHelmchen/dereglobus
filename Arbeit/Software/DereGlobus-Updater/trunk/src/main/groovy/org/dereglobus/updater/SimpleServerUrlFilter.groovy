package org.dereglobus.updater

/**
 * Ersetzt eine Server-URL durch eine andere. Dabei werden nur ganze Strings ersetzt,
 * ohne Regular Expressions o.ä.
 * 
 * @author marcvonrenteln
 *
 */
class SimpleServerUrlFilter implements Filter {

	def oldUrl

	def newUrl

	public SimpleServerUrlFilter(String oldUrl, String newUrl) {
		this.oldUrl = oldUrl
		this.newUrl = newUrl
	}

	@Override
	public String filter(String source) {
		return source.replace(oldUrl, newUrl);
	}
}
