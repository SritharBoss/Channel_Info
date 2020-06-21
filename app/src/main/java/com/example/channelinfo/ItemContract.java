package com.example.channelinfo;

import android.provider.BaseColumns;

class ItemContract {
    private ItemContract() {}

    public static final class ChannelEntry implements BaseColumns{
        public static final String TABLE_NAME="channelInfo";
        public static final String COLUMN_CHANNEL_NAME="channel";
        public static final String COLUMN_CATEGORY="category";
        public static final String COLUMN_IMG_URL="imgUrl";
        public static final String COLUMN_PRICE="price";
        public static final String COLUMN_LANGUAGE="language";
        public static final String COLUMN_HD="hd";
        public static final String COLUMN_TIMESTAMP="createdDate";
    }

}
