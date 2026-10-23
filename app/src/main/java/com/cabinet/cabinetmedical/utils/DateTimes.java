package com.cabinet.cabinetmedical.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Dates de l'API au format ISO local ("2026-02-03T14:00:00"), interprétées dans le fuseau
 * du téléphone. java.time n'est pas utilisé : minSdk 24 sans désucrage de la bibliothèque.
 */
public final class DateTimes {

    private DateTimes() {
    }

    /** Date correspondante, ou null si la valeur est absente ou mal formée. */
    public static Date parse(String isoLocalDateTime) {
        if (isoLocalDateTime == null || isoLocalDateTime.length() < 16) {
            return null;
        }
        String value = isoLocalDateTime.length() >= 19 ? isoLocalDateTime.substring(0, 19)
                : isoLocalDateTime.substring(0, 16) + ":00";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        format.setLenient(false);
        try {
            return format.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    /** "03/02/2026 à 14:00" ; la valeur brute si elle est illisible. */
    public static String format(String isoLocalDateTime) {
        Date date = parse(isoLocalDateTime);
        if (date == null) {
            return isoLocalDateTime == null ? "" : isoLocalDateTime;
        }
        return new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm", Locale.FRANCE).format(date);
    }

    public static boolean isAfter(String isoLocalDateTime, Date now) {
        Date date = parse(isoLocalDateTime);
        return date != null && date.after(now);
    }
}
