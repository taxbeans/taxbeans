import fs from 'fs';
import path from 'path';
import { getDocument, GlobalWorkerOptions, PDFDocumentProxy } from 'pdfjs-dist/legacy/build/pdf.mjs';

// Set workerSrc for pdfjs-dist legacy build in Node.js, resolving from node_modules
(GlobalWorkerOptions as any).workerSrc = new URL('./node_modules/pdfjs-dist/legacy/build/pdf.worker.mjs', import.meta.url).toString();

function isTargetLabel(str: string) {
  const s = str.toLowerCase();
  return s.includes('22g') || s.includes('22h') || s.includes('22i');
}

async function analyzePdf(pdfPath: string) {
  const data = new Uint8Array(fs.readFileSync(pdfPath));
  const loadingTask = getDocument({ data });
  const pdf: PDFDocumentProxy = await loadingTask.promise;

  for (let pageNum = 1; pageNum <= pdf.numPages; pageNum++) {
    const page = await pdf.getPage(pageNum);
    const annotations = await page.getAnnotations();
    const textContent = await page.getTextContent();

    // Collect all text items with their positions
    const textItems = textContent.items.map((item: any) => ({
      str: item.str,
      x: item.transform[4],
      y: item.transform[5],
    }));

    // Find target labels
    const targetLabels = textItems.filter(t => isTargetLabel(t.str));
    for (const label of targetLabels) {
      console.log(`Found label: '${label.str}' at (${label.x}, ${label.y})`);
      // Find the field directly below the label (closest y < label.y, x within 50 units)
      let minYDiff = Infinity;
      let closestField = null;
      for (const ann of annotations) {
        if (ann.subtype === 'Widget' && ann.fieldName) {
          const fieldX = ann.rect[0];
          const fieldY = ann.rect[1];
          const xDiff = Math.abs(fieldX - label.x);
          const yDiff = label.y - fieldY;
          if (xDiff < 50 && yDiff > 0 && yDiff < minYDiff) {
            minYDiff = yDiff;
            closestField = ann;
          }
        }
      }
      if (closestField) {
        console.log(`On page ${pageNum}, the field directly below the label is:`);
        console.log(`  Field name: ${closestField.fieldName}`);
        console.log(`  Coordinates: [${closestField.rect[0]}, ${closestField.rect[1]}]`);
      } else {
        console.log(`On page ${pageNum}, no field found directly below the label.`);
        // Show all nearby fields for debugging
        console.log('Nearby fields:');
        for (const ann of annotations) {
          if (ann.subtype === 'Widget' && ann.fieldName) {
            const fieldX = ann.rect[0];
            const fieldY = ann.rect[1];
            const xDiff = Math.abs(fieldX - label.x);
            const yDiff = Math.abs(fieldY - label.y);
            if (xDiff < 100 && yDiff < 100) {
              console.log(`  ${ann.fieldName}: [${fieldX}, ${fieldY}] (xDiff: ${xDiff.toFixed(1)}, yDiff: ${yDiff.toFixed(1)})`);
            }
          }
        }
      }
    }

    // Show all fields on page 3 for debugging
    if (pageNum === 3) {
      console.log('\nAll fields on page 3:');
      for (const ann of annotations) {
        if (ann.subtype === 'Widget' && ann.fieldName) {
          console.log(`  ${ann.fieldName}: [${ann.rect[0]}, ${ann.rect[1]}]`);
        }
      }
    }
  }
}

if (process.argv.length < 3) {
  console.error('Usage: ts-node analyze-pdf.ts <pdf-path>');
  process.exit(1);
}

const pdfPath = process.argv[2];
analyzePdf(pdfPath).catch(err => {
  console.error('Error analyzing PDF:', err);
}); 