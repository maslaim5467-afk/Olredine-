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

            case 3:
                return validateEducation(context, data);

            case 4:
                Map<String, String> economicStatuses = readOptions(context, "economy_data", "status");
                if (economicStatuses == null) {
                    return error(4, "economicStatus", "فهرست وضعیت‌های اقتصادی قابل خواندن نیست.");
                }
                if (!economicStatuses.containsKey(clean(data.economicStatus))) {
                    return error(4, "economicStatus", "وضعیت اقتصادی اولیه را انتخاب کنید.");
                }
                return null;

            case 5:
                return validateFamily(context, data);

            case 6:
                return validateSkills(context, data);

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

    public static ValidationError validateAllSections(Context context,
                                                       CharacterCreationData data) {
        for (int section = 0; section <= 6; section++) {
            ValidationError result = validateSection(context, data, section);
            if (result != null) return result;
        }
        return null;
    }

    private static ValidationError validateEducation(Context context, CharacterCreationData data) {
        Map<String, String> levels = readOptions(context, "education_data", "level");
        if (levels == null) {
            return error(3, "educationLevel", "فهرست مقاطع تحصیلی قابل خواندن نیست.");
        }

        String completedLevel = clean(data.educationLevel);
        if (!levels.containsKey(completedLevel)) {
            return error(3, "educationLevel", "آخرین مقطع تکمیل‌شده را به‌درستی انتخاب کنید.");
        }

        int age;
        try {
            age = Integer.parseInt(clean(data.playerAge));
        } catch (NumberFormatException e) {
            return error(3, "playerAge", "سن برای بررسی سازگاری تحصیلات معتبر نیست.");
        }

        int minimumCompletedAge = minimumAgeToComplete(completedLevel);
        if (minimumCompletedAge > 0 && age < minimumCompletedAge) {
            return error(3, "educationLevel",
                "سن شخصیت برای آخرین مقطع تکمیل‌شده کمتر از حداقل سن منطقی آن مقطع است.");
        }

        Map<String, String> statuses = readOptions(context, "education_data", "status");
        if (statuses == null) {
            return error(3, "educationStatus", "فهرست وضعیت‌های تحصیلی قابل خواندن نیست.");
        }
        String status = clean(data.educationStatus);
        if (!statuses.containsKey(status)) {
            return error(3, "educationStatus", "وضعیت تحصیل را به‌درستی انتخاب کنید.");
        }

        String currentLevel = clean(data.educationCurrentLevel);
        if ("NOT_STUDYING".equals(status)) {
            if (!currentLevel.isEmpty()) {
                return error(3, "educationCurrentLevel",
                    "وقتی شخصیت در حال تحصیل نیست، نباید مقطع ناتمام فعلی ثبت شود.");
            }
        } else {
            if (currentLevel.isEmpty() || !levels.containsKey(currentLevel)
                    || "NO_FORMAL_EDUCATION".equals(currentLevel)) {
                return error(3, "educationCurrentLevel",
                    "برای وضعیت انتخاب‌شده باید یک مقطع ناتمام معتبر مشخص شود.");
            }

            int completedRank = educationLevelRank(completedLevel);
            int currentRank = educationLevelRank(currentLevel);
            if (currentRank < completedRank) {
                return error(3, "educationCurrentLevel",
                    "مقطع ناتمام نمی‌تواند پایین‌تر از آخرین مقطع تکمیل‌شده باشد.");
            }

            int minimumCurrentAge = minimumAgeToStudy(currentLevel);
            if (minimumCurrentAge > 0 && age < minimumCurrentAge) {
                return error(3, "educationCurrentLevel",
                    "سن شخصیت برای تحصیل در مقطع انتخاب‌شده کمتر از حداقل سن منطقی آن مقطع است.");
            }
        }

        String focusLevel = currentLevel.isEmpty() ? completedLevel : currentLevel;
        String field = clean(data.educationField);
        String performance = clean(data.educationPerformance);

        if ("NO_FORMAL_EDUCATION".equals(focusLevel)) {
            if (!field.isEmpty() || !performance.isEmpty()) {
                return error(3, "educationField",
                    "برای شخصی که هنوز هیچ مقطع رسمی را نگذرانده، رشته و عملکرد تحصیلی ثبت نمی‌شود.");
            }
            return null;
        }

        Map<String, String> fields = readOptions(context, "education_data", "field");
        if (fields == null) {
            return error(3, "educationField", "فهرست رشته‌های تحصیلی قابل خواندن نیست.");
        }
        if (!fields.containsKey(field)) {
            return error(3, "educationField", "رشتهٔ تحصیلی معتبر انتخاب کنید.");
        }

        if (("PRIMARY".equals(focusLevel) || "LOWER_SECONDARY".equals(focusLevel))
                && !"UNDECLARED".equals(field)) {
            return error(3, "educationField",
                "در مقاطع ابتدایی و متوسطه اول رشتهٔ تخصصی ثبت نمی‌شود؛ گزینهٔ «بدون رشته مشخص» را انتخاب کنید.");
        }

        Map<String, String> performances = readOptions(context, "education_data", "performance");
        if (performances == null) {
            return error(3, "educationPerformance", "فهرست عملکردهای تحصیلی قابل خواندن نیست.");
        }
        if (!performances.containsKey(performance)) {
            return error(3, "educationPerformance", "عملکرد تحصیلی مقطع مربوط را مشخص کنید.");
        }
        return null;
    }

    private static ValidationError validateFamily(Context context, CharacterCreationData data) {
        Map<String, String> structures = readOptions(context, "family_data", "structure");
        Map<String, String> parentStatuses = readOptions(context, "family_data", "parentStatus");
        Map<String, String> supportLevels = readOptions(context, "family_data", "support");
        Map<String, String> economicStatuses = readOptions(context, "family_data", "economicStatus");

        if (structures == null || parentStatuses == null
                || supportLevels == null || economicStatuses == null) {
            return error(5, "familyStructure", "فهرست اطلاعات خانواده قابل خواندن نیست.");
        }

        String structure = clean(data.familyStructure);
        String parentStatus = clean(data.familyParentStatus);
        if (!structures.containsKey(structure)) {
            return error(5, "familyStructure", "ساختار خانواده را به‌درستی انتخاب کنید.");
        }
        if (!parentStatuses.containsKey(parentStatus)) {
            return error(5, "familyParentStatus", "وضعیت والدین را به‌درستی مشخص کنید.");
        }
        if (!supportLevels.containsKey(clean(data.familySupport))) {
            return error(5, "familySupport", "میزان حمایت خانوادگی را به‌درستی انتخاب کنید.");
        }
        if (!economicStatuses.containsKey(clean(data.familyEconomicStatus))) {
            return error(5, "familyEconomicStatus", "وضعیت اقتصادی خانواده را به‌درستی انتخاب کنید.");
        }

        if ("TWO_PARENT_HOUSEHOLD".equals(structure)
                && !"BOTH_ALIVE".equals(parentStatus)) {
            return error(5, "familyParentStatus",
                "زندگی با هر دو والد مستلزم زنده بودن هر دو والد است؛ وضعیت والدین را اصلاح کنید.");
        }
        if ("SINGLE_PARENT_HOUSEHOLD".equals(structure)
                && "NEITHER_ALIVE".equals(parentStatus)) {
            return error(5, "familyParentStatus",
                "زندگی با یکی از والدین با فوت هر دو والد سازگار نیست.");
        }
        return null;
    }

    private static ValidationError validateSkills(Context context, CharacterCreationData data) {
        Map<String, String> generalSkills = readOptions(context, "skills_data", "generalSkill");
        Map<String, String> specializedSkills = readOptions(context, "skills_data", "specializedSkill");
        if (generalSkills == null || specializedSkills == null) {
            return error(6, "skills", "فهرست مهارت‌ها قابل خواندن نیست.");
        }
        if (data.selectedGeneralSkillIds.isEmpty() || data.selectedGeneralSkillIds.size() > 3) {
            return error(6, "selectedGeneralSkillIds", "باید بین یک تا سه مهارت عمومی انتخاب کنید.");
        }
        for (String id : data.selectedGeneralSkillIds) {
            if (!generalSkills.containsKey(clean(id))) {
                return error(6, "selectedGeneralSkillIds", "یکی از مهارت‌های عمومی انتخاب‌شده معتبر نیست.");
            }
        }
        for (String id : data.selectedSpecializedSkillIds) {
            if (!specializedSkills.containsKey(clean(id))) {
                return error(6, "selectedSpecializedSkillIds", "یکی از مهارت‌های تخصصی انتخاب‌شده معتبر نیست.");
            }
        }
        return null;
    }

    private static int educationLevelRank(String level) {
        if ("PRIMARY".equals(level)) return 1;
        if ("LOWER_SECONDARY".equals(level)) return 2;
        if ("SECONDARY".equals(level)) return 3;
        if ("VOCATIONAL_DIPLOMA".equals(level)) return 4;
        if ("SHORT_CYCLE_TERTIARY".equals(level)) return 5;
        if ("BACHELOR".equals(level)) return 6;
        if ("MASTER".equals(level)) return 7;
        if ("DOCTORATE".equals(level)) return 8;
        return 0;
    }

    private static int minimumAgeToComplete(String level) {
        switch (level) {
            case "PRIMARY": return 10;
            case "LOWER_SECONDARY": return 12;
            case "SECONDARY": return 15;
            case "VOCATIONAL_DIPLOMA": return 16;
            case "SHORT_CYCLE_TERTIARY": return 17;
            case "BACHELOR": return 18;
            case "MASTER": return 20;
            case "DOCTORATE": return 23;
            default: return 0;
        }
    }

    private static int minimumAgeToStudy(String level) {
        switch (level) {
            case "PRIMARY": return 6;
            case "LOWER_SECONDARY": return 10;
            case "SECONDARY": return 13;
            case "VOCATIONAL_DIPLOMA": return 14;
            case "SHORT_CYCLE_TERTIARY": return 16;
            case "BACHELOR": return 17;
            case "MASTER": return 19;
            case "DOCTORATE": return 21;
            default: return 0;
        }
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
