package com.eddy.emultimsm;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音
 * @author User
 *
 */
public class PYUtil {
	
	/**
	 * 得到字符串对应的拼音
	 * @param src
	 * @return
	 */
	public static Set<String> getPinYin(String src) {
		Set<String> retSet = new HashSet<String>();
		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

		try {
			for (int i = 0; i < src.length(); i++) {
				String[] pinYins = PinyinHelper.toHanyuPinyinStringArray(src.charAt(i), outputFormat);
				if (pinYins == null || pinYins.length == 0) {
					pinYins = new String[1];
					pinYins[0] = src.charAt(0)+"";
				}
				if(retSet.size() == 0) {
					for(String py : pinYins) {
						retSet.add(py.charAt(0)+"");
					}
				}
				else {
					Set<String> tempSet = new HashSet<String>();
					for(String py : pinYins) {
						for(String sb : retSet) {
							tempSet.add(sb + py.charAt(0));
						}
					}
					retSet.clear();
					retSet.addAll(tempSet);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
		}
		
		return retSet;
	}
	
	/**
	 * 得到首字符对应的拼音
	 * @param src
	 * @return
	 */
	public static String getPinYinFirst(String src) {
		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

		try {
			String[] pinYins = PinyinHelper.toHanyuPinyinStringArray(src.charAt(0), outputFormat);
			if (pinYins == null || pinYins.length == 0) {
				pinYins = new String[1];
				return src.charAt(0)+"";
			}
			else {
				return pinYins[0].charAt(0)+"";
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
		}
		
		return "";
	}
}
