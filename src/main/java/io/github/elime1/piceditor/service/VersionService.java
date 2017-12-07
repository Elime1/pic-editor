package io.github.elime1.piceditor.service;

import lombok.extern.log4j.Log4j2;
import nu.xom.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Log4j2
@Service
public class VersionService {

    private static final String VERSION_XML_PATH = "versions/versions.xml";

    public String getTibiaVersion(String picSignature) {
        String tibiaVersion = null;
        try {
            tibiaVersion = parseVersion(picSignature);
        }
        catch (ParsingException | IOException e) {
            log.error("Failed to create the versions.xml document");
            throw new RuntimeException(e);
        }
        return tibiaVersion;
    }

    private String parseVersion(String picSignature) throws ParsingException, IOException {
        Builder parser = new Builder();
        Document doc = parser.build(getVersionXmlStream());
        Element root = doc.getRootElement();
        Elements versions = root.getChildElements();

        for (int i = 0; i < versions.size(); i++) {
            String picVersion = versions.get(i).getAttributeValue("pic");
            if (picVersion.equals(picSignature)) {
                return versions.get(i).getAttributeValue("string");
            }
        }

        return null;
    }

    private InputStream getVersionXmlStream() {
        return getClass().getClassLoader().getResourceAsStream(VERSION_XML_PATH);
    }

}
