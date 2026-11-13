package com.example.mycgame.data;

import android.content.Context;
import android.content.res.XmlResourceParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Validates the first three character-creation sections against the canonical XML catalogs.
 * This class is independent from the screen and can be reused by other creation entry points.
 */
public final class CharacterCreationValidator {
    private CharacterCreationValidator() { }

    public static final class ValidationError {
        public final int section;
        public final String field;
        public final String message;

        private ValidationError(int section, String field, String message) {
            this.section = section;
            this.field = field;
            this.message = message;
        }
    }

    private static final class LocationInfo {
        final String countryCode;
        final String name;
        final String type;

        LocationInfo(String countryCode, String name, String type) {
            this.countryCode = countryCode;
            this.name = name;
            this.type = type;
        }
    }

    public static ValidationError validateSection(Context context,
                                                  CharacterCreationData data,
                                                  int section) {
        if (context == null) return error(section, "data", "امکان بررسی اطلاعات مرجع وجود ندارد.");
        if (data == null) return error(section, "data", "اطلاعات شخصیت در دسترس نیست.");

        switch (section) {
            case 0:
                String name = clean(data.playerName);
                if (name.isEmpty()) {
                    return error(0, "playerName", "نام شخصیت را وارد کنید.");
                }

                String ageText = clean(data.playerAge);
                if (ageText.isEmpty()) {
                    return error(0, "playerAge", "سن را وارد کنید.");
                }
                final int age;
                try {
                    age = Integer.parseInt(ageText);
                } catch (NumberFormatException e) {
                    return error(0, "playerAge", "سن واردشده معتبر نیست.");
                }
                if (age < 12 || age > 30) {
                    return error(0, "playerAge", "سن باید بین ۱۲ تا ۳۰ سال باشد.");
                }

                Map<String, String> genders = readOptions(context, "character_data", "gender");
                if (genders == null) {
                    return error(0, "gender", "فهرست جنسیت‌ها قابل خواندن نیست.");
                }
                if (!genders.containsKey(clean(data.gender))) {
                    return error(0, "gender", "جنسیت انتخاب‌شده معتبر نیست.");
                }

                Map<String, String> traits = readOptions(context, "character_data", "trait");
                if (traits == null) {
                    return error(0, "innateTrait", "فهرست ویژگی‌های ذاتی قابل خواندن نیست.");
                }
                if (!traits.containsKey(clean(data.innateTrait))) {
                    return error(0, "innateTrait", "یک ویژگی ذاتی معتبر انتخاب کنید.");
                }
                return null;

            case 1:
                Map<String, String> countries = readCountries(context);
                if (countries == null) {
                    return error(1, "countryCode", "فهرست کشورها قابل خواندن نیست.");
                }
                String countryCode = clean(data.countryCode);
                if (countryCode.isEmpty() || !countries.containsKey(countryCode)) {
                    return error(1, "countryCode", "یک کشور معتبر برای ملیت اصلی انتخاب کنید.");
                }
                if (!countries.get(countryCode).equals(clean(data.countryName))) {
                    return error(1, "countryName", "نام کشور با کد کشور انتخاب‌شده مطابقت ندارد؛ کشور را دوباره انتخاب کنید.");
                }
                return null;

            case 2:
                Map<String, String> residenceCountries = readCountries(context);
                if (residenceCountries == null) {
                    return error(2, "residenceCountryCode", "فهرست کشورها قابل خواندن نیست.");
                }
                String residenceCode = clean(data.residenceCountryCode);
                if (residenceCode.isEmpty() || !residenceCountries.containsKey(residenceCode)) {
                    return error(2, "residenceCountryCode", "یک کشور معتبر برای محل زندگی انتخاب کنید.");
                }
                if (!residenceCountries.get(residenceCode).equals(clean(data.residenceCountryName))) {
                    return error(2, "residenceCountryName", "نام کشور محل زندگی با کد آن مطابقت ندارد؛ کشور را دوباره انتخاب کنید.");
                }

                Map<String, LocationInfo> locations = readLocations(context);
                if (locations == null) {
                    return error(2, "residenceLocationId", "فهرست مکان‌ها قابل خواندن نیست.");
                }
                String locationId = clean(data.residenceLocationId);
                if (locationId.isEmpty()) {
                    return error(2, "residenceLocationId", "شهر یا منطقه محل زندگی را انتخاب کنید.");
                }
                LocationInfo location = locations.get(locationId);
                if (location == null || !residenceCode.equals(location.countryCode)) {
                    return error(2, "residenceLocationId", "مکان انتخاب‌شده متعلق به کشور محل زندگی نیست؛ مکان را دوباره انتخاب کنید.");
                }
                if (!location.name.equals(clean(data.residenceLocationName))) {
                    return error(2, "residenceLocationName", "نام مکان با شناسه آن مطابقت ندارد؛ مکان را دوباره انتخاب کنید.");
                }
                if (!location.type.equals(clean(data.residenceType))) {
                    return error(2, "residenceType", "نوع منطقه با داده مرجع مطابقت ندارد؛ مکان را دوباره انتخاب کنید.");
                }
                return null;

            default:
                return null;
        }
    }

    public static ValidationError validateFirstThreeSections(Context context,
                                                              CharacterCreationData data) {
        for (int section = 0; section <= 2; section++) {
            ValidationError result = validateSection(context, data, section);
            if (result != null) return result;
        }
        return null;
    }

    private static ValidationError error(int section, String field, String message) {
        return new ValidationError(section, field, message);
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private static Map<String, String> readOptions(Context context,
                                                   String resourceName,
                                                   String elementName) {
        int resourceId = context.getResources().getIdentifier(
            resourceName, "xml", context.getPackageName());
        if (resourceId == 0) return null;

        Map<String, String> result = new HashMap<>();
        XmlResourceParser parser = context.getResources().getXml(resourceId);
        try {
            int event;
            while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG
                        && elementName.equals(parser.getName())) {
                    String id = parser.getAttributeValue(null, "id");
                    String label = parser.getAttributeValue(null, "label");
                    if (id != null && label != null) result.put(id, label);
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        } finally {
            parser.close();
        }
    }

    private static Map<String, String> readCountries(Context context) {
        int resourceId = context.getResources().getIdentifier(
            "world_data", "xml", context.getPackageName());
        if (resourceId == 0) return null;

        Map<String, String> result = new HashMap<>();
        XmlResourceParser parser = context.getResources().getXml(resourceId);
        try {
            int event;
            while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG && "country".equals(parser.getName())) {
                    String code = parser.getAttributeValue(null, "code");
                    String name = parser.getAttributeValue(null, "name");
                    if (code != null && name != null) result.put(code, name);
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        } finally {
            parser.close();
        }
    }

    private static Map<String, LocationInfo> readLocations(Context context) {
        int resourceId = context.getResources().getIdentifier(
            "world_data", "xml", context.getPackageName());
        if (resourceId == 0) return null;

        Map<String, LocationInfo> result = new HashMap<>();
        XmlResourceParser parser = context.getResources().getXml(resourceId);
        try {
            int event;
            while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG && "location".equals(parser.getName())) {
                    String country = parser.getAttributeValue(null, "country");
                    String id = parser.getAttributeValue(null, "id");
                    String name = parser.getAttributeValue(null, "name");
                    String type = parser.getAttributeValue(null, "type");
                    if (country != null && id != null && name != null && type != null) {
                        result.put(id, new LocationInfo(country, name, type));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        } finally {
            parser.close();
        }
    }
}
