package com.example.demo.main.util;

import com.example.demo.main.config.CutImgConfig;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class FileUtil {

    public static final int RESIZE_CUT_WIDTH = 300;
    public static final String CACHE_FILE_FOLDER = "D:\\cache_fileAnalysis_001";
    public static final String CACHE_RESIZE = "resize";
    public static final String CACHE_CUT = "cut";

    // 重置图片大小并保存
    public static boolean resizeImage (String srcPath, String destPath, int newWith, int newHeight, boolean forceSize) throws IOException {
        if (forceSize) {
            Thumbnails.of(srcPath).forceSize(newWith, newHeight).toFile(destPath);
        } else {
            Thumbnails.of(srcPath).width(newWith).height(newHeight).toFile(destPath);
        }
        return true;
    }

    public static boolean cutImage (String srcPath, String destPath, int x, int y, int width, int height, String formatName) {
        boolean cutSuccess = false;
        Iterator<ImageReader> ite = ImageIO.getImageReadersByFormatName(formatName);
        if (ite.hasNext()) {
            ImageReader reader = ite.next();
            InputStream is = null;
            try {
                is = new FileInputStream(srcPath);
                ImageInputStream iis = ImageIO.createImageInputStream(is);
                reader.setInput(iis);
                ImageReadParam defaultReadParam = reader.getDefaultReadParam();
                Rectangle rec = new Rectangle(x, y, width, height);
                defaultReadParam.setSourceRegion(rec);
                BufferedImage bi = reader.read(0, defaultReadParam);
                cutSuccess =  ImageIO.write(bi, formatName, new File(destPath));
            } catch (IOException e) {

            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return cutSuccess;
    }

    // 对需要分析的图片做截取处理，减小图片需要分析部分大小
    public static File cutImage(File file, String uuid) {
        String resizeFilePath = CACHE_FILE_FOLDER + File.separator + uuid
                + File.separator + CACHE_RESIZE + File.separator + file.getName();
        String cutFilePath = CACHE_FILE_FOLDER + File.separator + uuid
                + File.separator + CACHE_CUT + File.separator + file.getName();
        try{
            Map<String, Integer> cutMap = checkCutAndResizeNum(file);
            if(cutMap == null){
                return file;
            }
            cutImage(file.getAbsolutePath(), cutFilePath, cutMap.get("x"), cutMap.get("y"),
                    cutMap.get("w"), cutMap.get("h"), "jpg");
            resizeImage(cutFilePath, resizeFilePath,
                    cutMap.get("re_w"), cutMap.get("re_h"), false);
            return new File(cutFilePath);
        }catch (Exception e){
            e.printStackTrace();
            return file;
        }
    }

    // 计算图片应该被截取的大小和位置
    public static Map<String, Integer> checkCutAndResizeNum (File f) throws IOException {
        Map<String, Integer> map = new HashMap<>(6);
        Image img = ImageIO.read(f);
        double w = img.getWidth(null);
        double h = img.getHeight(null);
        double hw_proportion = h / w;

        if(hw_proportion > CutImgConfig.MIN_045 && hw_proportion <= CutImgConfig.MAX_045){
            int x_ = (int)(w * CutImgConfig.LEFT_CUT_PROPORTION_045.doubleValue());
            int y_ = (int)(h * CutImgConfig.TOP_CUT_PROPORTION_045.doubleValue());
            int w_ = (int)(w * (1 - (CutImgConfig.LEFT_CUT_PROPORTION_045.doubleValue()
                    + CutImgConfig.RIGHT_CUT_PROPORTION_045.doubleValue())));
            int h_ = (int)(h * (1 - (CutImgConfig.TOP_CUT_PROPORTION_045.doubleValue()
                    + CutImgConfig.BOTTOM_CUT_PROPORTION_045.doubleValue())));

            map.put("re_w", RESIZE_CUT_WIDTH);
            map.put("re_h", (int) (w_/RESIZE_CUT_WIDTH*h_));
            map.put("x", x_);
            map.put("y", y_);
            map.put("w", w_);
            map.put("h", h_);

        }else if(hw_proportion > CutImgConfig.MIN_056 && hw_proportion <= CutImgConfig.MAX_056){
            int x_ = (int)(w * CutImgConfig.LEFT_CUT_PROPORTION_056.doubleValue());
            int y_ = (int)(h * CutImgConfig.TOP_CUT_PROPORTION_056.doubleValue());
            int w_ = (int)(w * (1 - (CutImgConfig.LEFT_CUT_PROPORTION_056.doubleValue()
                    + CutImgConfig.RIGHT_CUT_PROPORTION_056.doubleValue())));
            int h_ = (int)(h * (1 - (CutImgConfig.TOP_CUT_PROPORTION_056.doubleValue()
                    + CutImgConfig.BOTTOM_CUT_PROPORTION_056.doubleValue())));

            map.put("re_w", RESIZE_CUT_WIDTH);
            map.put("re_h", (int) (w_/RESIZE_CUT_WIDTH*h_));
            map.put("x", x_);
            map.put("y", y_);
            map.put("w", w_);
            map.put("h", h_);
        }else{
            return null;
        }
        return map;
    }

    // 创建文件夹
    public static void createCacheFolder(String uuid){
        String resizeFilePath = CACHE_FILE_FOLDER + File.separator + uuid
                + File.separator + CACHE_RESIZE;
        String cutFilePath = CACHE_FILE_FOLDER + File.separator + uuid
                + File.separator + CACHE_CUT;
        createFolder(resizeFilePath);
        createFolder(cutFilePath);
    }

    public static void createFolder(String path){
        File f1 = new File(path);
        if(!f1.exists()){
            f1.mkdirs();
        }else if(!f1.isDirectory()){
            f1.delete();
            createFolder(path);
        }
    }

    // 获取指定文件夹下所有图片文件
    public static java.util.List<String> getAllFilePath(String path) {
        List<String> list = new ArrayList<String>();
        File folder = new File(path);
        if (folder.isFile() || !folder.exists()) {
            return list;
        }
        File[] files = folder.listFiles();
        for (File file : files) {
            if(!file.exists() || file.isDirectory() || file.getName().indexOf(".")==-1){
                continue;
            }
            String fileType = file.getName().substring(file.getName().lastIndexOf("."))
                    .toUpperCase(Locale.ROOT);
            if(!fileType.equals(".JPG") && !fileType.equals(".PNG")){
                continue;
            }
            list.add(file.getAbsolutePath());
        }
        return list;
    }
}
