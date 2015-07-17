package com.java.networking;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedInput;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Jive Response Interceptor
 *
 * Jive returns malformed and non-valid JSON responses. This causes Gson to break. So we find ourselves here.
 * Implementation for {@link #fromBody(TypedInput, Type)} is taken from {@link GsonConverter#fromBody(TypedInput, Type)}
 * and supplemented String manipulation to clean up the JSON input to Gson.
 *
 */
public class JiveResponseInterceptor extends GsonConverter {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(JiveResponseInterceptor.class);

    /**
     * Clean Jive's response because it is broken.
     * This is what is being returned to us:
     *        "throw 'allowIllegalResourceCall is false.'; { ... } "
     * We will trim off everything not in the JSON block
     *
     * @throws IOException
     */
    private String cleanInput(InputStream in) throws IOException {
        // save response to memory
        String jsonStr = IOUtils.toString(in, charset);

        log.info("INCOMING JSON: \t" + jsonStr);
        // trim text outside of JSON object
        int pos = jsonStr.indexOf('{');
        jsonStr = jsonStr.substring(pos, jsonStr.length());
        log.info("_CLEANED_ INCOMING JSON: \t" + jsonStr);
        return jsonStr;
    }

    /// TAKEN FROM retrofit.converter.GsonConverter ///
    private Gson gson;
    private String charset;

    public JiveResponseInterceptor(Gson gson) {
        super(gson);
        this.gson = gson;
        this.charset = "UTF-8";
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        String charset = this.charset;
        if (body.mimeType() != null) {
            charset = MimeUtil.parseCharset(body.mimeType(), charset);
        }
        InputStreamReader isr = null;
        try {
            return gson.fromJson(cleanInput(body.in()), type);
        } catch (IOException e) {
            throw new ConversionException(e);
        } catch (JsonParseException e) {
            throw new ConversionException(e);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}