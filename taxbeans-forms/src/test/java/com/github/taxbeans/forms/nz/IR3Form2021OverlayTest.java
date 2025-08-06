package com.github.taxbeans.forms.nz;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.cache.FileCache;

public class IR3Form2021OverlayTest {
	
	final static Logger logger = LoggerFactory.getLogger(IR3Form2021OverlayTest.class);
	
	@Test
	public void test() throws Exception {
		
		PDDocument overlayDoc = new PDDocument();
		PDPage page = new PDPage(PDRectangle.A4);
		//page.
		overlayDoc.addPage(page);
		Overlay overlayObj = new Overlay();
		PDFont font = PDType1Font.COURIER_BOLD_OBLIQUE;

		PDPageContentStream contentStream = new PDPageContentStream(overlayDoc, page);
		
		FileCache.populateCacheFromCurrentDirectory("target/classes/ir3-2021.pdf");
		PDDocument originalDoc = PDDocument.load(FileCache.obtainFileFromCache("ir3-2021.pdf"));
		PDDocumentCatalog pdCatalog = originalDoc.getDocumentCatalog();
		PDAcroForm pdAcroForm = pdCatalog.getAcroForm();

		contentStream.setFont(font, 8);
		contentStream.setNonStrokingColor(0);
		
		int count = 0;
		int previousPageNum = 0;
		
		ArrayList<File> filePages = new ArrayList<File>();
		
		//sort the fields by page:
		List<PDField> fields1 = pdAcroForm.getFields();
		Map<Integer, List<PDField>> map = new HashMap<Integer, List<PDField>>();  // fields = new ArrayList<PDField>();
		
		int highestPage = 0;
		for(PDField pdField : fields1) {
			List<PDAnnotationWidget> widgets = pdField.getWidgets();
			if (widgets.size() == 0) {
				continue;
			}
			PDAnnotationWidget pdAnnotationWidget = widgets.get(0);
			PDPage page2 = pdAnnotationWidget.getPage();
			int pageNum = originalDoc.getPages().indexOf(page2);
			highestPage = Math.max(pageNum, highestPage);
			List<PDField> list1 = map.get(pageNum);
			if (list1 == null) {
				map.put(pageNum, (list1 = new ArrayList<PDField>()));
			}
			list1.add(pdField);
		}
		
		List<PDField> fields = new ArrayList<PDField>();
		for (int i=0;i<=highestPage;i++) {
			fields.addAll(map.get(i));
		}
		//done sorting
		
		
		int numFields = fields.size();
		int fieldCount = 0;
		for(PDField pdField : fields){
			count ++;
			fieldCount++;
			int widgetListSize = pdField.getWidgets().size();
			if (widgetListSize == 0 ) {
				continue;
			}
			PDAnnotationWidget pdAnnotationWidget = pdField.getWidgets().get(0);
			String fieldName = pdField.getFullyQualifiedName();
			float x = pdAnnotationWidget.getRectangle().getLowerLeftX();
			float y = pdAnnotationWidget.getRectangle().getLowerLeftY();
			PDPage page2 = pdAnnotationWidget.getPage();
			int pageNum = originalDoc.getPages().indexOf(page2);
			if (pageNum > previousPageNum) {
				logger.info("entered new page, updating content stream");
				
				contentStream.close();
				File f = FileCache.newOrReplaceExistingFileInCache("ir3-2021-overlay-page"+pageNum+".pdf");
				overlayDoc.save(f);
				filePages.add(f);
				overlayDoc.close();
				
				overlayDoc = new PDDocument();
				
				PDPage newPage = new PDPage(PDRectangle.A4);
				overlayDoc.addPage(newPage);
				
				contentStream = new PDPageContentStream(overlayDoc, newPage);
				contentStream.setFont(font, 8);
				contentStream.setNonStrokingColor(0);
				previousPageNum = pageNum;
				
			}
			logger.info("found at page: " + pageNum);

			contentStream.beginText();
			contentStream.moveTextPositionByAmount(x, y);
			contentStream.drawString(fieldName);  // deprecated. Use showText(String text)
			contentStream.endText();

			if (count == 1000) {
			break;
			}
			if (fieldCount == numFields) {
				logger.info("reached last field, updating content stream");
				
				contentStream.close();
				File f = FileCache.newOrReplaceExistingFileInCache("ir3-2021-overlay-page"+(pageNum+1)+".pdf");
				overlayDoc.save(f);
				filePages.add(f);
				overlayDoc.close();
			}
		}
		contentStream.close();
		
		overlayObj.setOverlayPosition(Overlay.Position.FOREGROUND);
		overlayObj.setInputPDF(originalDoc);
		
		Map<Integer, String> ovmap = new HashMap<Integer, String>();
		int count2 = 0;
		logger.info("file pages size = " + filePages.size());
		for (File f : filePages) {
			count2++;
			logger.info("count2 = " + count2);
			ovmap.put(count2, f.getAbsolutePath());
		}

		overlayObj.overlay(ovmap);

		originalDoc.save(FileCache.newOrReplaceExistingFileInCache("ir3-2021-overlay.pdf"));
		overlayDoc.close();
		originalDoc.close();
	}

}
