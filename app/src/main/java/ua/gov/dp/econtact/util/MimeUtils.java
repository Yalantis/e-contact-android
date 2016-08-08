package ua.gov.dp.econtact.util;

import java.io.File;

/**
 * Created by Yalantis
 * 20.10.2015.
 *
 * @author Aleksandr
 */
public final class MimeUtils {

    private static final String PDF = "pdf";
    private static final String DOC = "doc";
    private static final String DOCX = "docx";
    private static final String INTENT_PDF = "application/pdf";
    private static final String INTENT_IMAGE = "image/*";
    private static final String INTENT_WORD = "application/msword";

    private MimeUtils() {
    }

    public static String getMimeType(final File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(PDF)) {
            return INTENT_PDF;
        } else if (fileName.endsWith(DOC) || fileName.endsWith(DOCX)) {
            return INTENT_WORD;
        } else {
            return INTENT_IMAGE;
        }
    }
}
