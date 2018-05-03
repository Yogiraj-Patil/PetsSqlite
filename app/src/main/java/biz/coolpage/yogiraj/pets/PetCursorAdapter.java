package biz.coolpage.yogiraj.pets;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import biz.coolpage.yogiraj.pets.data.PetsContract.*;

public class PetCursorAdapter extends CursorAdapter {
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView petName = (TextView) view.findViewById(R.id.petName);
        TextView petBreed = (TextView) view.findViewById(R.id.petBreed);
        int petNameIndex = cursor.getColumnIndex(PetEntry.COLOUMN_PET_NAME);
        int petBreedIndex = cursor.getColumnIndex(PetEntry.COLOUMN_PET_BREED);

        String petNameName = cursor.getString(petNameIndex);
        String petBreedName = cursor.getString(petBreedIndex);

        petName.setText(petNameName);
        petBreed.setText(petBreedName);

        /*

        //here does not need to increment cursor it will increment automatically by bind view when user
        //scrolling down a RecycleView

         */

    }
}
