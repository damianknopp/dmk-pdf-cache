package dmk.pdf;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= { dmk.spring.config.CacheConfig.class })
public class HtmlSimplePdfGenerationServiceTest {
	Logger logger = LoggerFactory.getLogger(HtmlSimplePdfGenerationServiceTest.class);

	@Autowired
	HtmlSimplePdfGenerationService pdfGenerationService = null;
	String tmpFileName = null;
	
	@Before
	public void setup() {
//		pdfGenerationService = new HtmlSimplePdfGenerationService();
		tmpFileName = "dmk-pdf1.pdf";
		File tmpFile = new File(tmpFileName);
		if(tmpFile.exists()){
			logger.debug("deleting file " + tmpFile.getAbsolutePath());
			tmpFile.delete();
		}
	}

	@After
	public void after() {
		pdfGenerationService = null;
	}

	@Test
	public void sanity() throws IOException {
		assertNotNull(pdfGenerationService);
		
//		final File tmpFile = File.createTempFile("dmk-pdf.", ".pdf");
		File tmpFile = new File(tmpFileName);
		pdfGenerationService.generateHelloWorld(tmpFile.getAbsolutePath());

		final long byteCount = FileUtils.sizeOf(tmpFile);
		if(logger.isDebugEnabled()){
			logger.debug("created file " + tmpFile.getAbsolutePath());
			final String size = FileUtils.byteCountToDisplaySize(byteCount);
			logger.debug("size = " + size);
		}
		
		assertTrue(byteCount > 0);
//		tmpFile.deleteOnExit();
		
	}
	
	@Test
	public void testCache() throws Exception {
		assertNotNull(pdfGenerationService);
		
		File tmpFile1 = new File("dmk-pdf-cache1.pdf");
		final byte[] run1Bytes = pdfGenerationService.generateHelloWorld(tmpFile1.getAbsolutePath());

		long byteCount = FileUtils.sizeOf(tmpFile1);
		if(logger.isDebugEnabled()){
			logger.debug("created file " + tmpFile1.getAbsolutePath());
			final String size = FileUtils.byteCountToDisplaySize(byteCount);
			logger.debug("size = " + size);
		}
		assertTrue(byteCount > 0);
		assertTrue(byteCount == run1Bytes.length);
		final String md51 = DigestUtils.md5Hex(new FileInputStream(tmpFile1));
		
		Thread.sleep(100);
		if(tmpFile1.exists()){
			tmpFile1.delete();
		}
		
//		File tmpFile2 = new File("dmk-pdf-cache1.pdf");
		final byte[] run2Bytes = pdfGenerationService.generateHelloWorld(tmpFile1.getAbsolutePath());
		byteCount = FileUtils.sizeOf(tmpFile1);
		if(logger.isDebugEnabled()){
			logger.debug("created file " + tmpFile1.getAbsolutePath());
			final String size = FileUtils.byteCountToDisplaySize(byteCount);
			logger.debug("size = " + size);
		}
		assertTrue(byteCount > 0);
		assertTrue(byteCount == run2Bytes.length);
		
		final String md52 = DigestUtils.md5Hex(new FileInputStream(tmpFile1));
		logger.debug("first run md5= " + md51);
		logger.debug("second run md5= " + md52);
		
		assertThat(md51, equalTo(md52));
	}
}