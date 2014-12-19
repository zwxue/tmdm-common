package org.talend.mdm.commmon.util.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonUtil {

    public static String getErrMsgFromException(Throwable e){
        String msg = e.getLocalizedMessage();
        if (msg != null) {
            Pattern p=Pattern.compile("(.*?):(.*?)"); //$NON-NLS-1$
            Matcher m = p.matcher(msg);
            if (m.matches()) {
                msg = m.group(2);
            }
        }
        return msg;
    }
}
