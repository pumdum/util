package eu.anasta.bluemind.smileyms;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import net.bluemind.lmtp.backend.FilterException;
import net.bluemind.lmtp.backend.IMessageFilter;
import net.bluemind.lmtp.backend.LmtpEnvelope;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Header;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.dom.MessageWriter;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.message.BasicBodyFactory;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.message.DefaultMessageWriter;

/**
 * @author Dumont
 *
 */
public class SmileyMsFilter implements IMessageFilter {

	@Override
	public Message filter(LmtpEnvelope env, Message message, long messageSize)
			throws FilterException {
		// traitement uniquement des multipart 
        if (message.isMultipart()){
        	replacePart((Multipart) message.getBody());
        }
		return message;
	}
	
	

	

    /**
     * Create a node given a Multipart body.
     * Add the Preamble, all Body parts and the Epilogue to the node.
     *
     * @param multipart the Multipart.
     * @return the root node of the tree.
     */
    private void replacePart(Multipart multipart) {
        final int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
        	// parcour de tout les part et traitement des Text body
        	Entity e =  multipart.getBodyParts().get(i);
        	if (e.getBody() instanceof TextBody){
        		String text = getValue(e.getBody());
        		// si la font windings detecter ony remplace tout les span j, k ou l par le smiley correspondant
        		if (text.contains("font-family:Wingdings")){
        			text = text.replaceAll("(?s)(?m)(?i)<span[^>]*font-family\\s*:\\s*Wingdings[^>]*>j<\\/span>", ":-)");
        			text = text.replaceAll("(?s)(?m)(?i)<span[^>]*font-family\\s*:\\s*Wingdings[^>]*>k<\\/span>", ":-(");
        			text = text.replaceAll("(?s)(?m)(?i)<span[^>]*font-family\\s*:\\s*Wingdings[^>]*>l<\\/span>", ":-|");
        			BodyPart newBody = newBodyPart(e.getHeader(), text);
        			if (newBody!=null){
        			 Entity removed = multipart.removeBodyPart(i);
        		        // The removed body part no longer has a parent entity it belongs to so
        		        // it should be disposed of.
        		        removed.dispose();
        		        System.out.println(text);
        			multipart.addBodyPart(newBody, i);
        			}
        		}
        	}else if (e.getBody() instanceof Multipart){
        		replacePart((Multipart) e.getBody());
        	}
        }
    }

    
    public String getValue(Object entity) {
            if (entity instanceof TextBody){
                /*
                 * A text body. Display its contents.
                 */
                TextBody body = (TextBody) entity;
                StringBuilder sb = new StringBuilder();
                try {
                    Reader r = body.getReader();
                    int c;
                    while ((c = r.read()) != -1) {
                        sb.append((char) c);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return sb.toString();

            } else {
              return "";

            }  
    }
    /**
     * Creates a text part from the specified string.
     * @param header 
     */
    private static BodyPart newBodyPart( Header header, String text) {
    	BasicBodyFactory bbf = new BasicBodyFactory();
        TextBody body;
		try {
			body = bbf.textBody(text,"utf-8");
		    BodyPart bodyPart = new BodyPart();
	        bodyPart.setText(body);
	        bodyPart.setHeader(header);
	        return bodyPart;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
   

}
