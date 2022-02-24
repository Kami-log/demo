package com.example.demo.main.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ocr_api20210707.Client;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralRequest;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralResponse;
import com.example.demo.main.util.FileUtil;
import com.example.demo.main.util.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

@Service
public class FileAnalysisService {

    @Autowired
    private AliYunOcrService aliYunOcrService;

    public static final String DEF_FILE_FOLDER = "D:" + File.separator + "fileCheck";

    public Object getFileMsg(String fileFolder){
        String manageId = System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        if(StringUtils.isEmpty(fileFolder)){
            fileFolder = DEF_FILE_FOLDER;
        }

        Long startTime = System.currentTimeMillis();
        List<Map<String, Object>> re = getFileTo(fileFolder, manageId);
        Long endTime = System.currentTimeMillis();
        System.out.println("总耗时：" + (endTime - startTime));
        return re;
    }

    private List<Map<String, Object>> getFileTo(String fileFolder, String uuid){
        List<String> filePathList = FileUtil.getAllFilePath(fileFolder);
        CountDownLatch latch = new CountDownLatch(filePathList.size());
        List<Map<String, Object>> fileParseList = new Vector<>();
        System.out.println("文件个数：" + filePathList.size());
        try {
            for(String string : filePathList){
                Runnable c = new Runnable(){
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();

                        try{
                            File f = new File(string);

                            FileUtil.createCacheFolder(uuid);
                            File newFile = FileUtil.cutImage(f, uuid);
                            Map<String, Object> m = new HashMap<>();

                            //Thread.sleep(1000 + ((int)(Math.random()*1000)));

                            Client c = aliYunOcrService.createClient();
                            RecognizeGeneralRequest q = new RecognizeGeneralRequest();
                            q.setBody(new FileInputStream(newFile));
                            RecognizeGeneralResponse r = c.recognizeGeneral(q);
                            JSONObject s = JSON.parseObject(r.getBody().getData());
                            JSONArray dataArr = s.getJSONArray("prism_wordsInfo");
                            int f_h = 0;
                            Map<String, String> f_m = new HashMap<>(4);
                            for(int i=0;i<dataArr.size() && i<10;i++){
                                JSONObject data = dataArr.getJSONObject(i);
                                //if(data.getInteger("prob") > 95){ // 可信度
                                String word = data.getString("word");
                                int height = data.getInteger("height");
                                //}
                                if(i == 0){
                                    m.put("名称", word);
                                }else if(i == 1){
                                    m.put("部件", word);
                                }else if(i == 2){
                                    m.put("主属性", word);
                                }else if(i == 3){
                                    m.put("主属性值", word);
                                }else if(i == 4){
                                    m.put("等级", word);
                                }else if(i == 5){
                                    f_h = height;
                                    String[] fa = word.split("\\+");
                                    f_m.put(fa[0], fa[1]);
                                }else{
                                    if (height + 3 >= f_h && height - 3 <= f_h) {
                                        if(word.indexOf("+") > -1){
                                            String[] fa = word.split("\\+");
                                            f_m.put(fa[0], fa[1]);
                                        }else if(word.indexOf(":") == word.length()-1
                                                || word.indexOf("：") == word.length()-1){
                                            m.put("套装名称", word);
                                        }
                                    }
                                }
                                m.put("副词条", f_m);
                            }

                            fileParseList.add(m);
                        }catch(Exception e){
                            e.printStackTrace();
                            System.out.println("线程异常");
                        }finally {
                            long endTime = System.currentTimeMillis();
                            System.out.println("线程执行耗时：" + (endTime - startTime));
                            latch.countDown();
                        }
                    }
                };
                ThreadPool.pool.submit(c);
                Thread.sleep(100);
            }
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return fileParseList;
    }
}
