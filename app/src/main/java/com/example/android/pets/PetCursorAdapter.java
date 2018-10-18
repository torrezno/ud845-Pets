package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate( R.layout.item_pet,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView itemPetNameView = (TextView) view.findViewById(R.id.item_pet_name);
        TextView itemPetBreedView = (TextView) view.findViewById(R.id.item_pet_breed);

        String petName = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME));
        String petBreed = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED));

        itemPetNameView.setText(petName);
        itemPetBreedView.setText(petBreed);
    }
}
