package tw.ispan.librarysystem.service.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.springframework.stereotype.Service;
import tw.ispan.librarysystem.dto.function.FunctionItem;
import tw.ispan.librarysystem.dto.search.SearchResultDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FunctionSearchService {

    private final List<FunctionItem> functions;

    public FunctionSearchService() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/data/function-index.json");
        functions = Arrays.asList(mapper.readValue(is, FunctionItem[].class));
    }

    public List<SearchResultDto> search(String keyword) {
        return functions.stream()
                .filter(item -> matches(item.getTitle(), keyword) || matches(item.getDescription(), keyword))
                .map(item -> {
                    SearchResultDto dto = new SearchResultDto();
                    dto.setType("▶");
                    dto.setTitle(item.getTitle());
                    dto.setUrl(item.getUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    private boolean matches(String text, String keyword) {
        if (text == null || keyword == null) return false;
        if (text.toLowerCase().contains(keyword.toLowerCase())) return true;
        return toPinyin(text).contains(keyword.toLowerCase().replaceAll("\\s+", ""));
    }

    private String toPinyin(String text) {
        try {
            StringBuilder sb = new StringBuilder();
            for (char c : text.toCharArray()) {
                String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);
                if (pinyins != null) sb.append(pinyins[0].replaceAll("\\d", ""));
                else sb.append(c);
            }
            return sb.toString().toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }
}

