package fr.dila.ss.core.birt;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.birt.report.engine.api.HTMLCompleteImageHandler;
import org.eclipse.birt.report.engine.api.IImage;
 
public class HTMLEmbeddedImageHandler extends HTMLCompleteImageHandler {
 
	/**
     * Default constructor
     */
    public HTMLEmbeddedImageHandler(){
    	super();
    }
    
	
  /**
   * Overrides the HTMLCompleteImageHandler to generate an encoded base64 representation of the image.
   */
  protected String handleImage(IImage image, Object context, String prefix, boolean needMap) {
    final Base64 base64  = new Base64();
    final byte[] encoded = base64.encode(image.getImageData());
    /** Remove the point in the image name */
    final String extension = image.getExtension().substring(1, image.getExtension().length());
 
    final StringBuilder sb = new StringBuilder("data:image/" + extension + ";base64,");
    final String stringEncoded = new String(encoded);
    sb.append(stringEncoded);
    return sb.toString();
  }
}

