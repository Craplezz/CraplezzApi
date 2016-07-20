package me.mani.clapi.various;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Schuckmann on 20.05.2016.
 */
public class StreamDataParser {

    private String streamData;
    private Map<String, String> map = new HashMap<>();

    public StreamDataParser(String streamData) throws ParseException {
        this.streamData = streamData;
        parse();
    }

    private void parse() throws ParseException {
        StringBuffer buffer = new StringBuffer();
        String key = null;
        String value;
        for (int i = 0; i < streamData.length(); i++) {
            switch (streamData.charAt(i)) {
                case '=':
                    key = buffer.toString();
                    buffer = new StringBuffer();
                    break;
                case ';':
                    value = buffer.toString();
                    buffer = new StringBuffer();
                    if (key == null)
                        throw new ParseException("Expected = but found ;", i);
                    map.put(key, value);
                    break;
                case '\'':
                    break;
                default:
                    buffer.append(streamData.charAt(i));
            }
        }
    }

    public String getString(String key) {
        return map.get(key);
    }

}
