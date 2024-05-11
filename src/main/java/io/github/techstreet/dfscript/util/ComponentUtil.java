package io.github.techstreet.dfscript.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentUtil {

    public static MutableText fromString(String message) {
        MutableText result = Text.literal("");

        try {
            Regex pattern = Regex.of("(§[a-f0-9lonmkrA-FLONMRK]|§x(§[a-f0-9A-F]){6})");
            Matcher matcher = pattern.getMatcher(message);

            Style s = Style.EMPTY;

            int lastIndex = 0;
            while (matcher.find()) {
                int start = matcher.start();
                String text = message.substring(lastIndex, start);
                if (text.length() != 0) {
                    MutableText t = Text.literal(text);
                    t.setStyle(s);
                    result.append(t);
                }
                String col = matcher.group();

                if (col.length() == 2) {
                    s = s.withFormatting(Formatting.byCode(col.charAt(1)));
                } else {
                    s = Style.EMPTY.withColor(
                        TextColor.parse("#" + col.replaceAll("§", "").substring(1)).getOrThrow(false, (h) -> {throw new IllegalArgumentException(h);}));
                }
                lastIndex = matcher.end();
            }
            String text = message.substring(lastIndex);
            if (text.length() != 0) {
                MutableText t = Text.literal(text);
                t.setStyle(s);
                result.append(t);
            }
        } catch (Exception err) {
            err.printStackTrace();
            return Text.literal("DFScript Text Error");
        }

        return result;
    }

    public static String toFormattedString(Text message) {
        StringBuilder result = new StringBuilder();

        if(message.getSiblings().isEmpty()){
            Style style = message.getStyle();

            String format = "";

            if (style.getColor() != null) {
                format += "§x§" + String.join("§", String.format("%06X", style.getColor().getRgb()).split(""));
            }

            if (style.isBold()) {
                format += "§l";
            }
            if (style.isItalic()) {
                format += "§o";
            }
            if (style.isUnderlined()) {
                format += "§n";
            }
            if (style.isStrikethrough()) {
                format += "§m";
            }
            if (style.isObfuscated()) {
                format += "§k";
            }

            result.append(format);
            result.append(message.getString());
        }
        else {
            for (Text sibling : message.getSiblings()) {
                result.append("§r");
                result.append(toFormattedString(sibling));
            }
        }

        return result.toString();
    }

    public static String sectionSignsToAnds(String msg) {
        StringBuilder result = new StringBuilder();

        Pattern p = Regex.of("(§[a-f0-9lonmkrA-FLONMRK]|§x(§[a-f0-9A-F]){6})").getPattern();
        Matcher m = p.matcher(msg);

        int lastIndex = 0;
        while (m.find()) {
            int start = m.start();
            String between = msg.substring(lastIndex, start);
            if (between.length() != 0) {
                result.append(between);
            }
            String replace = m.group().replaceAll("§", "&");
            result.append(replace);
            lastIndex = m.end();
        }

        String between = msg.substring(lastIndex);
        if (between.length() != 0) {
            result.append(between);
        }

        return result.toString();
    }

    public static String andsToSectionSigns(String msg) {
        StringBuilder result = new StringBuilder();

        Pattern p = Regex.of("(&[a-f0-9lonmkrA-FLONMRK]|&x(&[a-f0-9A-F]){6})").getPattern();
        Matcher m = p.matcher(msg);

        int lastIndex = 0;
        while (m.find()) {
            int start = m.start();
            String between = msg.substring(lastIndex, start);
            if (between.length() != 0) {
                result.append(between);
            }
            String replace = m.group().replaceAll("&", "§");
            result.append(replace);
            lastIndex = m.end();
        }

        String between = msg.substring(lastIndex);
        if (between.length() != 0) {
            result.append(between);
        }

        return result.toString();
    }
}
