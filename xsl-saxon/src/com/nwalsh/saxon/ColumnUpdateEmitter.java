package com.nwalsh.saxon;

import javax.xml.transform.TransformerException;

import com.icl.saxon.Controller;
import com.icl.saxon.om.NamePool;
import com.icl.saxon.tree.AttributeCollection;

/**
 * <p>Saxon extension to scan the column widths in a result tree fragment.</p>
 *
 * <p>Copyright (C) 2000 Norman Walsh.</p>
 *
 * <p>This class provides a
 * <a href="http://saxon.sourceforge.net/">Saxon 6.*</a>
 * implementation to scan the column widths in a result tree
 * fragment.</p>
 *
 * <p>The general design is this: the stylesheets construct a result tree
 * fragment for some colgroup environment. That result tree fragment
 * is "replayed" through the ColumnUpdateEmitter; the ColumnUpdateEmitter watches
 * the cols go by and extracts the column widths that it sees. These
 * widths are then made available.</p>
 *
 * <p><b>Change Log:</b></p>
 * <dl>
 * <dt>1.0</dt>
 * <dd><p>Initial release.</p></dd>
 * </dl>
 *
 * @author Norman Walsh
 * <a href="mailto:ndw@nwalsh.com">ndw@nwalsh.com</a>
 *
 */
public class ColumnUpdateEmitter extends CopyEmitter {
  /** The number of columns seen. */
  protected int numColumns = 0;
  protected String width[] = null;
  protected NamePool namePool = null;

  /** The FO namespace name. */
  protected static String foURI = "http://www.w3.org/1999/XSL/Format";
  /** The XHTML namespace name. */
  protected static String xhtmlURI = "http://www.w3.org/1999/xhtml";

  /** Construct a new ColumnUpdateEmitter. */
  public ColumnUpdateEmitter(Controller controller,
			     NamePool namePool,
			     String width[]) {
    super(controller, namePool);
    numColumns = 0;
    this.width = width;
    this.namePool = namePool;
  }

  /** Examine for column info. */
  public void startElement(int nameCode,
		    org.xml.sax.Attributes attributes,
		    int[] namespaces, int nscount)
    throws TransformerException {

    int thisFingerprint = namePool.getFingerprint(nameCode);
    int colFingerprint = namePool.getFingerprint("", "col");
    int XHTMLcolFingerprint = namePool.getFingerprint(xhtmlURI, "col");
    int foColFingerprint = namePool.getFingerprint(foURI, "table-column");

    if (thisFingerprint == colFingerprint || thisFingerprint == XHTMLcolFingerprint ) {
      AttributeCollection attr = new AttributeCollection(namePool, attributes);
      int widthFingerprint = namePool.getFingerprint("", "width");

      if (attr.getValueByFingerprint(widthFingerprint) == null) {
	attr.addAttribute(widthFingerprint, "CDATA", width[numColumns++]);
      } else {
	attr.setAttribute(widthFingerprint, "CDATA", width[numColumns++]);
      }
      attributes = attr;
    } else if (thisFingerprint == foColFingerprint) {
      AttributeCollection attr = new AttributeCollection(namePool, attributes);
      int widthFingerprint = namePool.getFingerprint("", "column-width");

      if (attr.getValueByFingerprint(widthFingerprint) == null) {
	attr.addAttribute(widthFingerprint, "CDATA", width[numColumns++]);
      } else {
	attr.setAttribute(widthFingerprint, "CDATA", width[numColumns++]);
      }
      attributes = attr;
    }

    rtfEmitter.startElement(nameCode, attributes, namespaces, nscount);
  }
}


