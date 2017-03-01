package GW2Navi;

/**
 * Translation.java loads the translation file and retrieves translations
 * depending on the requested language by other classes.
 */
import org.ini4j.Ini;

public class Translation {
	
	public Ini File;
	Ini.Section nameandcode;
	String DELIMITER_LANGUAGES = ",";
	String DELIMITER_NAMEANDCODE = "\\|";
	String SUFFIX_TRANSLATION = "s_";
	
	public String[] lang;
	public String[] code;
	
	String currentLanguage;
	String defaultLanguage = "en";
	
	/**
	 * Constructs this object by parsing the language names and codes string.
	 * @param pIni object created from reading the translation file.
	 * @param pLanguage current language as set by the options file.
	 */
	public Translation(Ini pIni, String pLanguage)
	{
		this.File = pIni;
		currentLanguage = pLanguage;
		/**
		 * Extract the language names; assumes it is one long string as such:
		 * "name|code, name|code, ..."
		 */
		nameandcode = File.get("NamesAndCodes");
		String[] langs = nameandcode.get("lang").split(DELIMITER_LANGUAGES);
		
		lang = new String[langs.length];
		code = new String[langs.length];

		for (int i = 0; i < langs.length; i++)
		{
			String[] temp = langs[i].trim().split(DELIMITER_NAMEANDCODE);
			lang[i] = temp[0];
			code[i] = temp[1];
		}
	}
	
	/**
	 * Gets translation for the requested word or phrase.
	 * @param pEntry to lookup.
	 * @param pLanguage the language code for lookup.
	 * @return Translation string.
	 */
	public String getFromLang(String pEntry, String pLanguage)
	{
		Ini.Section section = File.get(SUFFIX_TRANSLATION + pEntry);
		if (section != null)
		{
			String str = section.get(pLanguage);
			if (str != null)
			{
				return str;
			}
			else 
			{
				str = section.get(defaultLanguage);
				if (str != null)
				{
					return str;
				}
				return "LANGUAGENOTFOUND";
			}
		}
		return "TRANSLATIONNOTFOUND";
	}
	
	/**
	 * Gets multiple translation entries separated by spaces.
	 * @param pWord words to translate consecutively.
	 * @return word or phrase.
	 */
	public String get(String... pWord)
	{
		String str = "";
		if (pWord.length == 1)
		{
			return getFromLang(pWord[0], currentLanguage);
		}
		else if (pWord.length >= 2)
		{
			int i;
			for (i = 0; i < pWord.length - 1; i++)
			{
				str += getFromLang(pWord[i], currentLanguage) + " ";
			}
			// Don't suffix space for last word
			str += getFromLang(pWord[i], currentLanguage);
		}
		
		return str;
	}
}
