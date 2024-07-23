package com.bod.bod.challenge.entity;

public enum Category {

    HEALTH(CategoryType.HEALTH),
    STUDY(CategoryType.STUDY),
    HOBBY(CategoryType.HOBBY),
    ECONOMY(CategoryType.ECONOMY),
    ETC(CategoryType.ETC);

    private final String category;

    Category(String category) {
        this.category = category;
    }

    public String getCategory() {return this.category;}

    public static class CategoryType{
        public static final String HEALTH = "건강";
        public static final String STUDY = "학습";
        public static final String HOBBY = "취미";
        public static final String ECONOMY = "경제";
        public static final String ETC = "기타";
    }
}
