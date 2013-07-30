package dmk.pdf;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class CssReader {
	final static Logger logger = LoggerFactory.getLogger(CssReader.class);

	// Private constructor prevents instantiation from other classes
	private CssReader() {
	}

	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 */
	private static class CssReaderHolder {
		public static final CssReader CSSREADER = new CssReader();
	}

	public static CssReader getInstance() {
		return CssReaderHolder.CSSREADER;
	}

	public String readCssFileFromCp(final String name) {

		InputStream stream = null;
		try {
			stream = ClassLoader.getSystemClassLoader().getResourceAsStream(
					name);
			if (stream == null) {
				stream = CssReader.class.getResourceAsStream(name);
			}

			final String contents = IOUtils.toString(stream);
			return contents;
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new RuntimeException(e.getCause());
		} finally {
			IOUtils.closeQuietly(stream);
		}

	}
}