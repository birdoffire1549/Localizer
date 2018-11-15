package com.firebirdcss.test.localizer;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.firebirdcss.library.localeizer.Localeizer;

public class TestLocaleConversion {

	@Test
	public void test() throws JsonParseException, JsonMappingException, IOException {
		URL url = this.getClass().getClassLoader().getResource("language_library.json");
		Localeizer tran = new Localeizer();
		
		tran.loadLibrary(url.getFile());
		Localeizer.setDefaultLocale(Locale.ENGLISH);
		
		tran.setLocale(Locale.ENGLISH);
		assertEquals("Hello how are you today?", tran.translate("Hello how are you today?"));
		
		tran.setLocale(Locale.JAPANESE);
		assertEquals("今日こんにちは。", tran.translate("Hello how are you today?"));
		
		tran.setLocale(Locale.FRENCH);
		assertEquals("Bonjour comment vas tu aujourd'hui?", tran.translate("Hello how are you today?"));
		
		tran.setLocale(Locale.forLanguageTag("sv"));
		assertEquals("Hej hur mår du idag?", tran.translate("Hello how are you today?"));
		
		tran.setLocale(Locale.CHINESE);
		assertEquals("你好，今天好嗎？", tran.translate("Hello how are you today?"));
	}

}
