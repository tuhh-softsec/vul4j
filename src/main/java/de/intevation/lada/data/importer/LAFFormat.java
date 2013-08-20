package de.intevation.lada.data.importer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LAFFormat
{
    private JSONObject fileContent;

    public boolean readConfigFile(String fileName) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(fileName));
            Charset encoding = Charset.defaultCharset();
            String content =
                encoding.decode(ByteBuffer.wrap(encoded)).toString();
            fileContent = new JSONObject(content);
            return true;
        }
        catch (IOException ioe) {
            return false;
        }
        catch (JSONException je) {
            return false;
        }
    }

    public List<EntryFormat> getFormat(String dataType) {
        List<EntryFormat> formats = new LinkedList<EntryFormat>();
        try {
            JSONArray block = fileContent.getJSONArray(dataType);
            for (int i = 0; i < block.length(); i++) {
                JSONObject jEntry = block.getJSONObject(i);
                EntryFormat entry = new EntryFormat();
                entry.setKey(jEntry.getString("key"));
                Pattern pattern =
                    Pattern.compile(
                        jEntry.getString("regex"),
                        Pattern.MULTILINE);
                entry.setPattern(pattern);
                entry.setDefaultValue(jEntry.get("default"));
                formats.add(entry);
            }
            return formats;
        }
        catch (JSONException e) {
            return null;
        }
    }
}
