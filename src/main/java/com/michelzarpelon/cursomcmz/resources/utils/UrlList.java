package com.michelzarpelon.cursomcmz.resources.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class UrlList {
	
	public static String decodeParam(String code) {
		try {
			return URLDecoder.decode(code, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static List<Integer> decodeIntList(String code) {
		String[] vetor = code.split(",");
		List<Integer> categorias = new ArrayList<>();
		for (int i = 0; i < vetor.length; i++) {
			categorias.add(Integer.parseInt(vetor[i]));
		}
		return categorias;
	}

}
