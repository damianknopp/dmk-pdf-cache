package dmk.pdf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= { dmk.spring.config.CacheConfig.class })
public class HtmlSimplePdfGenerationServiceTest {
	Logger logger = LoggerFactory.getLogger(HtmlSimplePdfGenerationServiceTest.class);

	@Autowired
	@Qualifier("htmlSimplePdfGenerationService")
	SimplePdfGenerationService htmlSimplePdfGenerationService;
	String tmpFileName = null;
	
	@Before
	public void setup() {
		tmpFileName = "dmk-pdf1.pdf";
		File tmpFile = new File(tmpFileName);
		if(tmpFile.exists()){
			logger.debug("deleting file " + tmpFile.getAbsolutePath());
			tmpFile.delete();
		}
	}

	@After
	public void after() { }

	@Test
	public void sanity() throws IOException {
		assertNotNull(htmlSimplePdfGenerationService);
		
//		final File tmpFile = File.createTempFile("dmk-pdf.", ".pdf");
		File tmpFile = new File(tmpFileName);
		htmlSimplePdfGenerationService.generateHelloWorld(tmpFile.getAbsolutePath());

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
		assertNotNull(htmlSimplePdfGenerationService);
		
		File tmpFile1 = new File("dmk-pdf-cache1.pdf");
		if(tmpFile1.exists()){
			tmpFile1.delete();
		}
		final byte[] run1Bytes = htmlSimplePdfGenerationService.generateHelloWorld(tmpFile1.getAbsolutePath());

		long byteCount = FileUtils.sizeOf(tmpFile1);
		if(logger.isDebugEnabled()){
			logger.debug("created file " + tmpFile1.getAbsolutePath());
			final String size = FileUtils.byteCountToDisplaySize(byteCount);
			logger.debug("size = " + size);
		}
		assertTrue(byteCount > 0);
		assertTrue(byteCount == run1Bytes.length);
		final String md51 = DigestUtils.md5Hex(new FileInputStream(tmpFile1));
		final String md5Run1FromBytes = DigestUtils.md5Hex(run1Bytes);
		Thread.sleep(100);
		if(tmpFile1.exists()){
			tmpFile1.delete();
		}
		
		final byte[] run2Bytes = htmlSimplePdfGenerationService.generateHelloWorld(tmpFile1.getAbsolutePath());
		assertTrue(byteCount == run2Bytes.length);
		final String md5Run2FromBytes = DigestUtils.md5Hex(run2Bytes);
		logger.debug("first run md5 by file= " + md51);
		logger.debug("first run md5 by bytes= " + md5Run1FromBytes);
		logger.debug("second run md5 by bytes= " + md5Run2FromBytes);
		assertThat(md5Run1FromBytes, equalTo(md5Run2FromBytes));
		//	if we deleted the tmpFile1 on the first run, and the second run was a cache hit, then no tmp file will be created.
		assertFalse(tmpFile1.exists());
	}
}