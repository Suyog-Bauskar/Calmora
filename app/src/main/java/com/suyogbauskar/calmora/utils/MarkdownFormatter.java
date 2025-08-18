package com.suyogbauskar.calmora.utils;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.BulletSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownFormatter {
    
    public static SpannableString formatMarkdown(String text) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        
        // Split text by lines to handle bullet points
        String[] lines = text.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            // Handle bullet points (lines starting with - or •)
            if (line.trim().startsWith("- ") || line.trim().startsWith("• ")) {
                String bulletText = line.trim().substring(2); // Remove "- " or "• "
                SpannableString formattedLine = formatInlineMarkdown(bulletText);
                
                // Add bullet span without adding another bullet character
                SpannableStringBuilder bulletBuilder = new SpannableStringBuilder(formattedLine);
                bulletBuilder.setSpan(new BulletSpan(20), 0, bulletBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                
                builder.append(bulletBuilder);
            } else {
                // Regular line - format inline markdown
                SpannableString formattedLine = formatInlineMarkdown(line);
                builder.append(formattedLine);
            }
            
            // Add newline if not the last line
            if (i < lines.length - 1) {
                builder.append("\n");
            }
        }
        
        return new SpannableString(builder);
    }
    
    private static SpannableString formatInlineMarkdown(String text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        
        // Handle bold text (**text** or __text__)
        Pattern boldPattern = Pattern.compile("\\*\\*(.*?)\\*\\*|__(.*?)__");
        Matcher boldMatcher = boldPattern.matcher(text);
        
        int offset = 0;
        while (boldMatcher.find()) {
            String boldText = boldMatcher.group(1) != null ? boldMatcher.group(1) : boldMatcher.group(2);
            int start = boldMatcher.start() - offset;
            int end = boldMatcher.end() - offset;
            
            // Replace the markdown syntax with just the text
            builder.replace(start, end, boldText);
            
            // Apply bold style
            builder.setSpan(new StyleSpan(Typeface.BOLD), start, start + boldText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            
            // Update offset for next replacements
            offset += (boldMatcher.group().length() - boldText.length());
        }
        
        // Handle italic text (*text* or _text_)
        text = builder.toString(); // Get updated text after bold processing
        Pattern italicPattern = Pattern.compile("(?<!\\*)\\*([^*]+?)\\*(?!\\*)|(?<!_)_([^_]+?)_(?!_)");
        Matcher italicMatcher = italicPattern.matcher(text);
        
        offset = 0;
        while (italicMatcher.find()) {
            String italicText = italicMatcher.group(1) != null ? italicMatcher.group(1) : italicMatcher.group(2);
            int start = italicMatcher.start() - offset;
            int end = italicMatcher.end() - offset;
            
            // Replace the markdown syntax with just the text
            builder.replace(start, end, italicText);
            
            // Apply italic style
            builder.setSpan(new StyleSpan(Typeface.ITALIC), start, start + italicText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            
            // Update offset for next replacements
            offset += (italicMatcher.group().length() - italicText.length());
        }
        
        return new SpannableString(builder);
    }
}
