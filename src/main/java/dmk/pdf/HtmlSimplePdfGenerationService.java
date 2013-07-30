package dmk.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

@Service
public class HtmlSimplePdfGenerationService implements SimplePdfGenerationService {
	Logger logger = LoggerFactory.getLogger(HtmlSimplePdfGenerationService.class);

	@Cacheable("genPdf")
	@Override
	public byte[] generateHelloWorld(String fileName) {
		Validate.notBlank(fileName);

		final File file = new File(fileName);
		FileOutputStream fstream = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			this.generateHelloWorld(baos);
			final byte[] bytes = baos.toByteArray();
			fstream = new FileOutputStream(file);
			IOUtils.write(baos.toByteArray(), fstream);
			return bytes;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void generateHelloWorld(final OutputStream ostream) {
		Validate.notNull(ostream);

		Document doc = null;
		PdfWriter pdfWriter = null;
		try {
			doc = new Document();
			doc.setPageSize(PageSize.A4);
			doc.setMargins(50, 50, 50, 50);
			
			// PDF Writer pipeline
			pdfWriter = PdfWriter.getInstance(doc, ostream);
			PdfWriterPipeline pdfWriterPipeline = new PdfWriterPipeline(doc,
					pdfWriter);

			// HTML pipeline
			CssAppliers cssApplier = null;
			HtmlPipelineContext htmlContext = new HtmlPipelineContext(
					cssApplier);
			htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
			HtmlPipeline htmlPipeline = new HtmlPipeline(htmlContext,
					pdfWriterPipeline);

			// CSS pipeline
			CSSResolver cssResolver = XMLWorkerHelper.getInstance()
					.getDefaultCssResolver(false);
			final String css = CssReader.getInstance().readCssFileFromCp("pdf.css");
			if(logger.isDebugEnabled()){
				logger.debug(css);
			}
			cssResolver.addCss(css, true);
			//			cssResolver.addCss(
//					"p{ border: 3px solid #000; color: #bbb; font-size: 12px; padding: 1px; margin: 1px;}",
//					true);
//			cssResolver.addCss("h2{ border-bottom: 1px dottted black; text-align: center; }", true);
//			cssResolver.addCss("body{ margin:0px auto; border: 1px 1px 1px 1px; width: 80%; }", true);
			CssResolverPipeline cssResvolerPipeline = new CssResolverPipeline(
					cssResolver, htmlPipeline);

			// parse and render, w/ pipeline
			XMLWorker xmlWorker = new XMLWorker(cssResvolerPipeline, true);
			XMLParser parser = new XMLParser(true, xmlWorker, Charset.forName("UTF-8"));
			String hello = cleanHtml(helloHtml());
			if (logger.isDebugEnabled()) {
				logger.debug(hello);
			}
			doc.open();
			parser.parse(new StringReader(hello));
			// this is simpler and seems to work if you dont have CSS
//			XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, doc, new StringReader(hello));
		} catch (Exception de) {
			throw new RuntimeException(de);
		} finally {
			if (doc != null && doc.isOpen()) {
				doc.close();
			}
			if (pdfWriter != null){
				pdfWriter.close();
			}
			IOUtils.closeQuietly(ostream);
		}
	}

	private String cleanHtml(final String unsafe) {
		Validate.notBlank(unsafe);
//		 return Jsoup.clean(unsafe, Whitelist.basic());
		return unsafe;
	}

	private String helloHtml() {
		
		final String dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
		final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		final String timeStamp = sdf.format(new Date());
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>");
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta charset=\"utf-8\"/>");
		sb.append("<title>simple itext demo</title>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<h2>Heading 1.</h2>");
		sb.append("<p><span class='boldness'>DMK</span> test w/ <span class='italics'>itext.</span></p>");
		sb.append("<h2>Heading 2.</h2>");
		sb.append("<p><span class='boldness'>Current timestamp:</span>" + timeStamp + "</p>");
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

}
