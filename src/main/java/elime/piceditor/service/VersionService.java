package elime.piceditor.service;

import nu.xom.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by Elime on 15-09-03.
 */
public class VersionService {

    private static Logger log = LogManager.getLogger();

    public String getTibiaVersion(String picSignature) {
        String tibiaVersion = null;
        try {
            Builder parser = new Builder();
            Document doc = parser.build(getClass().getClassLoader().getResourceAsStream("versions/versions.xml"));

            Element root = doc.getRootElement();

            Elements versions = root.getChildElements();

            for (int i = 0; i < versions.size(); i++) {
                if (versions.get(i).getAttributeValue("pic").equals(picSignature)) {
                    tibiaVersion = versions.get(i).getAttributeValue("string");
                    break;
                }
            }
        }
        catch (ParsingException | IOException e) {
            log.warn("Failed to create the versions.xml document");
            throw new RuntimeException(e);
        }

        return tibiaVersion;
    }


}
