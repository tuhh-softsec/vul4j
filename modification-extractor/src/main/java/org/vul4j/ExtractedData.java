package org.vul4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtractedData {
    public List<String> methods = new ArrayList<>();
    public List<String> attributes = new ArrayList<>();
    public Map<String, ExtractedData> classes = new HashMap<>();
}
