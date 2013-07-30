package dmk.pdf;

import java.io.OutputStream;

/**
 * 
 * @author dmknopp
 *
 */
public interface SimplePdfGenerationService {

	/**
	 * write to the fileName and return the PDF bytes
	 * @param fileName of the File to write to
	 * @return byte array representation of the PDF
	 */
	public byte[] generateHelloWorld(final String fileName);
	
	public void generateHelloWorld(final OutputStream ostream);
}
