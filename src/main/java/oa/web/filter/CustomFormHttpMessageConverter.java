package oa.web.filter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/***
 * see  DefaultFormRequestWrapperFilter<br />
 * RecordAPIExecuteTimeFilter <br />
 * DecideUseCacheWhenOvertimeFilter <br />
 * RecordEveryReqParamInfoFilter
 */
public class CustomFormHttpMessageConverter extends AllEncompassingFormHttpMessageConverter {
    private String requestBody;

    @Override
    public MultiValueMap<String, String> read(Class<? extends MultiValueMap<String, ?>> clazz,
                                              HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = (contentType.getCharSet() != null ? contentType.getCharSet() : Charset.forName("UTF-8"));
        String body = StreamUtils.copyToString(inputMessage.getBody(), charset);
        requestBody = body;
        String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
        MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>(pairs.length);
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx == -1) {
                result.add(URLDecoder.decode(pair, charset.name()), null);
            } else {
                String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                result.add(name, value);
                //拦截器里面把communityId转化为community.id
                if (name.length() > 2 && name.endsWith("Id") && (!name.contains("."))) {
                    int index = name.indexOf("Id");
                    result.add(name.substring(0, index) + ".id", value);
                }
            }
        }
        return result;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
