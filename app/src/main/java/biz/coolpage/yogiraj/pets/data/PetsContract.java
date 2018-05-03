package biz.coolpage.yogiraj.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Gaurav on 12-03-2018.
 */

public final class PetsContract {

    public static final String PATH_PET = "pets";
    public static final String CONTENT_AUTHORITY = "biz.coolpage.yogiraj.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final int INSRT1 = 102;

    PetsContract() {
    }

    public static final class PetEntry implements BaseColumns {
        public static final String TABLE_NAME = "pets";

        public static final String _ID = BaseColumns._ID;
        public static final String COLOUMN_PET_NAME = "name";
        public static final String COLOUMN_PET_BREED = "breed";
        public static final String COLOUMN_PET_WEIGHT = "weight";
        public static final String COLOUMN_PET_GENDER = "gender";

        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PET);


        //for MIME
        //Here
        /*
        CURSOR_DIR_BASE_TYPE (which maps to the constant "vnd.android.cursor.dir")
        and CURSOR_ITEM_BASE_TYPE (which maps to the constant “vnd.android.cursor.item”).

         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY
                + "/" + PATH_PET;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY
                + "/" + PATH_PET;


        public static boolean isValidGender(int i) {
            if (i == GENDER_MALE || i == GENDER_FEMALE) {
                return true;
            }
            return false;
        }

    }

}
