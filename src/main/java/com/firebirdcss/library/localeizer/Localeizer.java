package com.firebirdcss.library.localeizer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a library tool to aid in the translation of messages for various locales.
 * <p>
 * This tool is used to allow Java programmers to quickly add future support
 * for multiple languages for various locales in their coding up-front without 
 * having to code any differently to support the swapping out of messages for different
 * locales.
 * <p>
 * This means that messages can still appear in the code where they are being used without
 * having to replace them with less descriptive variables.
 * <p>
 * Also, This tool allows for initial functionality without a language library file and then
 * one can be added easily in the future, by simply specifying the path to where you plan on
 * putting the file once it is available.
 * <p>
 * The language library files are required to be in a JSON format like the following example:
 * <pre>
 * {
 *     "zh" : {
 *         "Hello how are you today?" : "你好，今天好嗎？",
 *         "Hope you day is nice!" : "希望你的一天好！"
 *     },
 *     "sv" : {
 *         "Hello how are you today?" : "Hej hur mår du idag?",
 *         "Hope you day is nice!" : "Hoppas att dagen är snäll!"
 *     },
 *     "fr" : {
 *         "Hello how are you today?" : "Bonjour comment vas tu aujourd'hui?",
 *         "Hope you day is nice!" : "J'espère que votre journée est belle!"
 *     },
 *     "ja" : {
 *         "Hello how are you today?" : "今日こんにちは。",
 *         "Hope you day is nice!" : "あなたは1日がいいといいですね！"
 *     }
 * }
 * </pre>
 * 
 * @author Scott Griffis
 *
 */
public class Localeizer {
	private static volatile Locale defaultGlobalLocale = Locale.getDefault();
	private static final Map<Locale, Map<String/*Key*/, String/*Message*/>> localeMessageCache = new HashMap<>();
	
	private Locale currentLocale = defaultGlobalLocale;
	
	/**
	 * CONSTRUCTOR: Used to load the Language Library on instantiation.
	 * 
	 */
	public Localeizer() {
		// Do nothing, just here for instantiation.
	}
	
	/**
	 * Used to load a language library file where if there is a problem loading the file
	 * then the method will return a boolean false, otherwise it will return a boolean true. <br>
	 * <br>
	 * Please Note: This Translator tool does NOT need a language library to function
	 * in that if no language library file is found or doesn't contain the current locale then 
	 * default messages can still be given regardless of the actual locale.
	 * 
	 * @param pathToLibraryJson - The full path and filename of the language library file, as {@link String}
	 * 
	 * @throws JsonParseException - Indicates a problem with the JSON in the file.
	 * @throws JsonMappingException - Likely indicates a problem with the validity of the JSON in the file.
	 * @throws IOException - Indicates a problem with accessing the specified file.
	 */
	public boolean loadLibrary(String pathToLibraryJson) {
		try {
			loadLibraryWithExceptions(pathToLibraryJson);
			
			return true;
		} catch (Exception supressed) {}
		
		return false;
	}
	
	/**
	 * Used to load a language library file where if there is a problem loading the file
	 * then exceptions are thrown so that the can be caught and acted upon by the 
	 * surrounding implementing code.<br>
	 * <br>
	 * Please Note: This Translator tool does NOT need a language library to function
	 * in that if no language library file is found or doesn't contain the current locale then 
	 * default messages can still be given regardless of the actual locale.
	 * 
	 * @param pathToLibraryJson - The full path and filename of the language library file, as {@link String}
	 * 
	 */
	public void loadLibraryWithExceptions(String pathToLibraryJson) throws JsonParseException, JsonMappingException, IOException {
		File file = new File(pathToLibraryJson);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(file, JsonNode.class);
		
		Iterator<Entry<String, JsonNode>> fields = rootNode.fields();
		while (fields.hasNext()) { // Iterating Language codes...
			Entry<String, JsonNode> field = fields.next();
			Locale locale = new Locale(field.getKey());
			Iterator<Entry<String, JsonNode>> messages = field.getValue().fields();
			while (messages.hasNext()) { // Iterating keyed messages for current locale...
				Entry<String, JsonNode> message = messages.next();
				if (localeMessageCache.containsKey(locale)) {
					localeMessageCache.get(locale).put(message.getKey(), message.getValue().asText());
				} else {
					Map<String/*MessageKey*/, String/*Message*/> tempMap = new HashMap<>();
					tempMap.put(message.getKey(), message.getValue().asText());
					localeMessageCache.put(locale, tempMap);
				}
			}
		}
	}
	
	/**
	 * Used to fetch a translation for a given message key. If the current locale does
	 * not match the specified defaultLocale then the key will be used to attempt to 
	 * fetch a message in the current locale from the currentMessageCache. If no message
	 * is cached for the current locale then the defaultMessage will be returned instead.
	 * 
	 * @param key - The message key which is the key used to represent the message across 
	 * the various locales, as {@link String}
	 * @param defaultMessage - The default message to use if no translation can be found for
	 * the current locale, as {@link String}
	 * @param defaultLocale - The {@link Locale} the defaultMessage belongs to; This helps to 
	 * prevent searching for a translation if the locale of the message matches current locale.
	 * 
	 * @return Returns the appropriately translated message if one exists or the defaultMessage if
	 * one does not exist, as {@link String}
	 */
	public String translate(String key, String defaultMessage, Locale defaultLocale) {
		if (currentLocale.equals(defaultLocale)) {
			
			return defaultMessage;
		} else {
			if (localeMessageCache.containsKey(currentLocale) && localeMessageCache.get(currentLocale).containsKey(key)) {
				
				return localeMessageCache.get(currentLocale).get(key);
			} else {
				
				return key;
			}
		}
	}
	
	/**
	 * Used to fetch a translation for a given message key. If the current locale does
	 * not match the set defaultLocale then the key will be used to attempt to 
	 * fetch a message in the current locale from the currentMessageCache. If no message
	 * is cached for the current locale then the defaultMessage will be returned instead.
	 * 
	 * @param key - The message key which is the key used to represent the message across 
	 * the various locales, as {@link String}
	 * @param defaultMessage - The default message to use if no translation can be found for
	 * the current locale, as {@link String}
	 * 
	 * @return Returns the appropriately translated message if one exists or the defaultMessage if
	 * one does not exist, as {@link String}
	 */
	public String translate(String key, String defaultMessage) {
		
		return translate(key, defaultMessage, defaultGlobalLocale);
	}
	
	/**
	 * Used to fetch a translation for a given message key. If one cannot be found for the
	 * set currentLocale then the key will be returned as the message. Because of this the 
	 * default translation message may be used as said key if all other locals are to be 
	 * keyed by the default message being the key.
	 * 
	 * @param key - The message key or the message which is the key used to represent the message across 
	 * the various locales, as {@link String}
	 * 
	 * @return Returns the appropriately translated message if one exists or the key if
	 * one does not exist, as {@link String}
	 */
	public String translate(String keyOrMessage) {
		
		return translate(keyOrMessage, keyOrMessage);
	}
	
	/**
	 * Allows for the {@link Locale} to be manually set for translations.
	 * This is useful if say someone is using a machine with a Locale that is different 
	 * from their native Locale and they wanted to adjust the app using this utility to their
	 * native locale.
	 * 
	 * @param locale - The desired locale as {@link Locale}.
	 */
	public void setLocale(Locale locale) {
		this.currentLocale = locale;
	}
	
	/**
	 * Allows for the {@link Locale} to be manually set for translations.
	 * This is useful if say someone is using a machine with a Locale that is different 
	 * from their native Locale and they wanted to adjust the app using this utility to their
	 * native locale.
	 * 
	 * @param locale - The {@link String} representation of the desired Locale.
	 */
	public void setLocale(String locale) {
		setLocale(new Locale(locale));
	}
	
	/**
	 * Used to set the default locale of the application. The default locale is
	 * the locale that all of the default messages belong to. If the default locale
	 * is not set then the default messages will be used regardless of the current locale
	 * unless the default locale is specified at the time the message is given.
	 * 
	 * @param locale - The default locale to set as {@link Locale}
	 */
	public static void setDefaultLocale(Locale locale) {
		defaultGlobalLocale = locale;
	}
	
	/**
	 * Used to set the default locale of the application. The default locale is
	 * the locale that all of the default messages belong to. If the default locale
	 * is not set then the default messages will be used regardless of the current locale
	 * unless the default locale is specified at the time the message is given.
	 * 
	 * @param locale - The default locale to set as {@link String}
	 */
	public static void setDefaultLocale(String locale) {
		setDefaultLocale(new Locale(locale));
	}
}
