package com.eagle.jobcrawler;

import com.eagle.jobcrawler.model.Info;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 读取城市、职位配置信息
 */
public class ConfigReader {
    public static List<Info.Data> readJobInfo(SourceType sourceType) {
        return readInfo("job.json", sourceType);
    }

    public static List<Info.Data> readCityInfo(SourceType sourceType) {
        return readInfo("city.json", sourceType);
    }

    private static List<Info.Data> readInfo(String path, SourceType sourceType) {
        File file = new File("src/main/resources/" + path);
        String json = null;
        try {
            json = FileUtils.readFileToString(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        List<Info> list = gson.fromJson(json, new TypeToken<List<Info>>() {}.getType());
        for (Info info : list) {
            if (info.getCity().equalsIgnoreCase(sourceType.getName())) {
                return info.getData();
            }
        }
        return null;
    }
}
