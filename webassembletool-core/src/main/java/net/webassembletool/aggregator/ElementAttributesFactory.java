package net.webassembletool.aggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.webassembletool.Driver;
import net.webassembletool.DriverFactory;

/**
 * ElementAttributs's Factory
 * @author athaveau
 */
final class ElementAttributesFactory {

    /**
     * Parse the tag and return the ElementAttributes representation
     * @param tag the tag to parse
     * @return ElementAttributes
     */
    final static ElementAttributes createElementAttributes(String tag) {
        //<!--$includetemplate$aggregated2$templatewithparams.jsp$-->
        //Parsing sting <!--$includeblock$aggregated2$$(block)$myblock$-->
        //in order to retrieve includeblock, aggregated2, $(block), myblock
        Pattern pattern = Pattern.compile("(?<=\\$)(?:[^\\$]|\\$\\()*(?=\\$)");
        Matcher matcher = pattern.matcher(tag);
        List<String> listparameters = new ArrayList<String>();
        while (matcher.find()) {
            listparameters.add(matcher.group());
        }

        String[] parameters = (String[]) listparameters.toArray(new String[listparameters.size()]);


        Driver driver;
        String page = "";
        String name = null;

        if (parameters.length > 1) {
            driver = DriverFactory.getInstance(parameters[1]);
        } else {
            driver = DriverFactory.getInstance();
        }

        if (parameters.length > 2) {
            page = parameters[2];
        }

        if (parameters.length > 3) {
            name = parameters[3];
        }
        ElementAttributes tagattributes = new ElementAttributes(driver, page, name);
        return tagattributes;

    }
}
