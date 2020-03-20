package com.hzh.frame.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;

import com.hzh.frame.core.BaseSP;

import java.util.Locale;

/**
 * 语言
 *
 * @date 2019/10/17
 */
public class LanguageTools {
    private static final String TAG = "LanguageTools";

//在这里添加你需要的所有支持的切换语言
//    public static List<HashMap<String, Object>> getSelectLanguage(Context context) {
//        String language = BaseSP.getInstance().getString("language");
//        List<HashMap<String, Object>> list = new ArrayList<>();
//        HashMap<String, Object> map1 = new HashMap<>();
//        map1.put("name", context.getString(R.string.comn_language_cn));
//        map1.put("value", ComnConfig.LANG_CN);
//        map1.put("isSelected", ComnConfig.LANG_CN.equals(language));
//        list.add(map1);
//
//        HashMap<String, Object> map2 = new HashMap<>();
//        map2.put("name", context.getString(R.string.comn_language_en));
//        map2.put("value", ComnConfig.LANG_EN);
//        map2.put("isSelected", ComnConfig.LANG_EN.equals(language));
//        list.add(map2);
//
//        HashMap<String, Object> map3 = new HashMap<>();
//        map3.put("name", context.getString(R.string.comn_language_ru));
//        map3.put("value", ComnConfig.LANG_RU);
//        map3.put("isSelected", ComnConfig.LANG_RU.equals(language));
//        list.add(map3);
//
//        return list;
//    }

    public static Context setAppLanguage(Context context, String language) {
        BaseSP.getInstance().put("language", language);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //7.0及以上的方法
            return createConfiguration(context, language);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //4.2及以上的方法
            updateConfiguration(context, language);
            return context;
        }
        return context;
    }

    /**
     * 7.0及以上的修改app语言的方法
     *
     * @param context  context
     * @param language language
     * @return context
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static Context createConfiguration(Context context, String language) {
        Resources resources = context.getResources();
        Locale locale = new Locale(language);
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        configuration.setLocales(localeList);
        return context.createConfigurationContext(configuration);
    }

    /**
     * 7.0以下的修改app语言的方法
     *
     * @param context  context
     * @param language language
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void updateConfiguration(Context context, String language) {
        Resources resources = context.getResources();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, displayMetrics);
    }

}
