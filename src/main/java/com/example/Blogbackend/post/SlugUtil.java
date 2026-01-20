package com.example.Blogbackend.post;

import java.text.Normalizer;

public class SlugUtil {
    private SlugUtil(){}

    public static String toSlug(String input){
        if (input==null)return "post";

        String s =input.trim().toLowerCase()
                .replace('ı','i')
                .replace('ğ','g')
                .replace('ü','u')
                .replace('ş','s')
                .replace('ö','o')
                .replace('ç','c');

                s= Normalizer.normalize(s,Normalizer.Form.NFD)
                        .replaceAll("\\p{M}+","");

                s=s.replaceAll("[^a-z0-9\\s-]", "");
                s=s.replaceAll("[\\s]+", "-");
                s=s.replaceAll("-{2,}", "-");
                s=s.replaceAll("^-|-$", "");

                return s.isBlank() ? "post" : s;
    }

}
